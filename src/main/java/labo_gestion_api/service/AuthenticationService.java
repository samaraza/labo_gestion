package labo_gestion_api.service;

import jakarta.mail.MessagingException;
import labo_gestion_api.dto.AuthenticationRequest;
import labo_gestion_api.dto.AuthenticationResponse;
import labo_gestion_api.dto.RegistrationRequest;
import labo_gestion_api.model.RoleEnum;
import labo_gestion_api.model.Token;
import labo_gestion_api.model.User;
import labo_gestion_api.repository.RoleRepository;
import labo_gestion_api.repository.TokenRepository;
import labo_gestion_api.repository.UserRepository;
import labo_gestion_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import labo_gestion_api.model.Ecole;
import labo_gestion_api.repository.EcoleRepository;
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EcoleRepository ecoleRepository;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        // Détermination du nom du rôle
        String tempRole = request.getRole();
        String roleName;

        if (tempRole == null || tempRole.isBlank()) {
            roleName = "USER"; // rôle par défaut
        } else {
            roleName = tempRole;
        }

        // Recherche du rôle en base
        RoleEnum roleEnum = RoleEnum.valueOf(roleName);
        var userRole = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new IllegalStateException("Role " + roleName + " was not initiated"));

        // Création de l'utilisateur
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(true)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }


    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. المصادقة
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. جلب المستخدم مع الأدوار (JOIN FETCH)
        User user = userRepository.findByEmailWithRoles(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 3. إضافة الـ claims الإضافية
        var claims = new HashMap<String, Object>();
        claims.put("fullName", user.fullName());

        // 4. تحويل الأدوار إلى GrantedAuthority
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toList());

        // 5. إنشاء UserDetails لـ Spring Security
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );

        // 6. توليد التوكن
        var jwtToken = jwtService.generateToken(claims, userDetails);

        // 7. استخراج الدور الأول لإرساله في الرد
        String role = user.getRoles().stream()
                .findFirst()
                .map(r -> r.getName().name())
                .orElse(null);

        // 8. الرد
        return AuthenticationResponse.builder()
                .id(user.getId())
                .token(jwtToken)
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(role)
                .build();
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }




    // ✅ دالة جديدة لتسجيل مستخدم مع مدرسة (للمدير)
    @Transactional
    public User registerUserWithSchool(RegistrationRequest request, Integer ecoleId, RoleEnum schoolRole) throws MessagingException {
        // 1. تحديد الدور الرئيسي للمستخدم
        String tempRole = request.getRole();
        String roleName = (tempRole == null || tempRole.isBlank()) ? "USER" : tempRole;

        RoleEnum roleEnum = RoleEnum.valueOf(roleName);
        var userRole = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new IllegalStateException("Role " + roleName + " was not initiated"));

        // 2. جلب المدرسة
        Ecole ecole = ecoleRepository.findById(ecoleId)
                .orElseThrow(() -> new RuntimeException("School not found with id: " + ecoleId));

        // 3. إنشاء المستخدم مع المدرسة
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(true)
                .roles(List.of(userRole))
                .ecole(ecole)  // ✅ ربط المستخدم بمدرسة
                .schoolRole(schoolRole)  // ✅ تحديد دوره داخل المدرسة (PROFFESSEUR أو PREPARATEUR)
                .build();

        userRepository.save(user);
        sendValidationEmail(user);
        return user;
    }
}