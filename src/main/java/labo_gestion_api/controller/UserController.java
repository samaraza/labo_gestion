package labo_gestion_api.controller;

import labo_gestion_api.model.Role;
import labo_gestion_api.model.RoleEnum;
import labo_gestion_api.model.User;
import labo_gestion_api.repository.RoleRepository;
import labo_gestion_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return ResponseEntity.ok(user);
    }

    // Create new user (Admin only)
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody Map<String, Object> payload) {
        String firstName = (String) payload.get("firstName");
        String lastName = (String) payload.get("lastName");
        String email = (String) payload.get("email");
        String password = (String) payload.get("password");
        String roleString = (String) payload.get("role");

        RoleEnum roleEnum = RoleEnum.valueOf(roleString.toUpperCase());

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleEnum));

        User user = new User();
        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRoles(List.of(role));

        User savedUser = userService.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // Update user (general)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> payload) {

        String firstName = (String) payload.get("firstName");
        String lastName = (String) payload.get("lastName");
        String password = (String) payload.get("password");
        String email = (String) payload.get("email");

        User user = userService.findById(id);

        if (firstName != null) user.setFirstname(firstName);
        if (lastName != null) user.setLastname(lastName);
        if (password != null && !password.isEmpty()) user.setPassword(password);
        if (email != null && !email.isEmpty()) {
            if (!email.equals(user.getEmail())) {
                boolean emailExists = userService.existsByEmail(email);
                if (emailExists) throw new RuntimeException("Email already exists: " + email);
            }
            user.setEmail(email);
        }

        User updatedUser = userService.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    // Admin update (can change role)
    @PutMapping("/admin/{id}")
    public ResponseEntity<User> updateFromAdmin(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> payload) {

        String firstName = (String) payload.get("firstName");
        String lastName = (String) payload.get("lastName");
        String email = (String) payload.get("email");
        String roleString = (String) payload.get("role");

        User user = userService.findById(id);

        if (firstName != null) user.setFirstname(firstName);
        if (lastName != null) user.setLastname(lastName);
        if (email != null && !email.isEmpty()) user.setEmail(email);

        if (roleString != null && !roleString.isEmpty()) {
            RoleEnum roleEnum = RoleEnum.valueOf(roleString.toUpperCase());
            Role role = roleRepository.findByName(roleEnum)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleEnum));
            user.setRoles(List.of(role));
        }

        User updatedUser = userService.saveFromAdmin(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ NEW ENDPOINT: Assign existing user to the director's school
    // POST /users/{userId}/affecter
    // Request body: { "role": "PROFFESSEUR" or "PREPARATEUR" }
    // Only accessible by users with role DIRECTEUR
    @PostMapping("/{userId}/affecter")
    public ResponseEntity<User> affecterUserToEcole(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> payload) {

        // 1. Get currently authenticated user (must be a director)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User director = userService.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // 2. Verify that the authenticated user has the DIRECTEUR role
        boolean isDirector = director.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleEnum.DIRECTEUR);
        if (!isDirector) {
            throw new RuntimeException("Only a director can assign users to a school");
        }

        // 3. Extract role from request body
        String roleInSchool = payload.get("role");
        if (roleInSchool == null || roleInSchool.isEmpty()) {
            throw new RuntimeException("Role is required (PROFFESSEUR or PREPARATEUR)");
        }

        // 4. Call service to assign user to director's school
        User updatedUser = userService.assignUserToDirectorSchool(userId, roleInSchool, director.getId());
        return ResponseEntity.ok(updatedUser);
    }




    // ✅ جلب جميع مستخدمي نفس المدرسة (مدير + أساتذة + محضرين)
    @GetMapping("/my-school/all")
    public ResponseEntity<List<User>> getAllUsersInMySchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);

        if (currentUser.getEcole() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<User> users = userService.getAllUsersByEcole(currentUser.getEcole().getId());
        return ResponseEntity.ok(users);
    }

    // ✅ جلب الأساتذة فقط من نفس المدرسة
    @GetMapping("/my-school/professeurs")
    public ResponseEntity<List<User>> getProfesseursInMySchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);

        if (currentUser.getEcole() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<User> professeurs = userService.getProfesseursByEcole(currentUser.getEcole().getId());
        return ResponseEntity.ok(professeurs);
    }

    // ✅ جلب المحضرين فقط من نفس المدرسة
    @GetMapping("/my-school/preparateurs")
    public ResponseEntity<List<User>> getPreparateursInMySchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);

        if (currentUser.getEcole() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<User> preparateurs = userService.getPreparateursByEcole(currentUser.getEcole().getId());
        return ResponseEntity.ok(preparateurs);
    }
}