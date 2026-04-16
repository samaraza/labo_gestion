package labo_gestion_api.service;

import labo_gestion_api.model.Ecole;
import labo_gestion_api.model.RoleEnum;
import labo_gestion_api.model.User;
import labo_gestion_api.repository.EcoleRepository;
import labo_gestion_api.repository.TokenRepository;
import labo_gestion_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EcoleRepository ecoleRepository;  // تأكد من وجود هذا الـ repository

    // تسجيل مستخدم جديد
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountLocked(false);
        user.setEnabled(true);
        user.setCreatedDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User save(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getId() == null) {
            user.setCreatedDate(LocalDateTime.now());
        }
        user.setLastModifiedDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User saveFromAdmin(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getId() == null) {
            user.setCreatedDate(LocalDateTime.now());
        }
        user.setLastModifiedDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void deleteById(Integer id) {
        tokenRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // ✅ دالة لربط مستخدم بمدرسة محددة (معرف المدرسة معروف)
    public User assignUserToSchool(Integer userId, Integer schoolId, String schoolRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Ecole ecole = (Ecole) ecoleRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found with id: " + schoolId));
        user.setEcole(ecole);
        user.setSchoolRole(RoleEnum.valueOf(schoolRole.toUpperCase()));
        return userRepository.save(user);
    }

    // ✅ دالة لربط مستخدم بمدرسة المدير الحالي (هذه هي التي يناديها الـ Controller)
    public User assignUserToDirectorSchool(Integer userId, String schoolRole, Integer directorId) {
        User director = findById(directorId);
        if (director.getEcole() == null) {
            throw new RuntimeException("The director does not have an associated school.");
        }
        return assignUserToSchool(userId, director.getEcole().getId(), schoolRole);
    }
}