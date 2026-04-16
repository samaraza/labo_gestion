package labo_gestion_api.config;

import labo_gestion_api.model.Role;
import labo_gestion_api.model.RoleEnum;
import labo_gestion_api.model.User;
import labo_gestion_api.repository.RoleRepository;
import labo_gestion_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {


        // ===== 1. التأكد من وجود الأدوار الأساسية =====
        if (roleRepository.count() == 0) {
            for (RoleEnum roleEnum : RoleEnum.values()) {
                Role role = new Role();
                role.setName(roleEnum);
                roleRepository.save(role);
            }
            System.out.println("✅ الأدوار الأساسية تم إنشاؤها.");
        }

        // التأكد من وجود الأدوار في قاعدة البيانات (RoleInitializer سيقوم بذلك أيضاً)
        // ولكن نضيف تحقق إضافي

        // إنشاء مستخدم PREPARATEUR
        if (userRepository.findByEmail("samar@gmail.com").isEmpty()) {
            Role role = roleRepository.findByName(RoleEnum.PREPARATEUR)
                    .orElseThrow(() -> new RuntimeException("Role PREPARATEUR not found"));

            User user = User.builder()
                    .email("samar@gmail.com")
                    .password(passwordEncoder.encode("prep12345"))
                    .firstname("Samar")
                    .lastname("Prep")
                    .enabled(true)
                    .accountLocked(false)
                    .roles(List.of(role))  // ✅ تعيين الدور
                    .build();
            userRepository.save(user);
            System.out.println("✅ مستخدم PREPARATEUR تم إنشاؤه بنجاح مع الدور!");
        }

        // إنشاء مستخدم ADMINISTRATEUR
        if (userRepository.findByEmail("ala@gmail.com").isEmpty()) {
            Role role = roleRepository.findByName(RoleEnum.ADMINISTRATEUR)
                    .orElseThrow(() -> new RuntimeException("Role ADMINISTRATEUR not found"));

            User user = User.builder()
                    .email("ala@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstname("Ala")
                    .lastname("Admin")
                    .enabled(true)
                    .accountLocked(false)
                    .roles(List.of(role))
                    .build();
            userRepository.save(user);
            System.out.println("✅ مستخدم ADMINISTRATEUR تم إنشاؤه بنجاح مع الدور!");
        }

        // إنشاء مستخدم DIRECTEUR
        if (userRepository.findByEmail("amrouni@gmail.com").isEmpty()) {
            Role role = roleRepository.findByName(RoleEnum.DIRECTEUR)
                    .orElseThrow(() -> new RuntimeException("Role DIRECTEUR not found"));

            User user = User.builder()
                    .email("amrouni@gmail.com")
                    .password(passwordEncoder.encode("directeur123"))
                    .firstname("Amrouni")
                    .lastname("Ben Saleh")
                    .enabled(true)
                    .accountLocked(false)
                    .roles(List.of(role))
                    .build();
            userRepository.save(user);
            System.out.println("✅ مستخدم DIRECTEUR تم إنشاؤه بنجاح مع الدور!");
        }

        // إنشاء مستخدم PROFFESSEUR
        if (userRepository.findByEmail("aycha@gmail.com").isEmpty()) {
            Role role = roleRepository.findByName(RoleEnum.PROFFESSEUR)
                    .orElseThrow(() -> new RuntimeException("Role PROFFESSEUR not found"));

            User user = User.builder()
                    .email("aycha@gmail.com")
                    .password(passwordEncoder.encode("prof12345"))
                    .firstname("Aycha")
                    .lastname("Prof")
                    .enabled(true)
                    .accountLocked(false)
                    .roles(List.of(role))
                    .build();
            userRepository.save(user);
            System.out.println("✅ مستخدم PROFFESSEUR تم إنشاؤه بنجاح مع الدور!");
        }

        System.out.println("=====================================");
        System.out.println("🎉 جميع المستخدمين تم إنشاؤهم بنجاح!");
        System.out.println("=====================================");
    }
}