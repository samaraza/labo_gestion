package labo_gestion_api.config;

import labo_gestion_api.model.Role;
import labo_gestion_api.model.RoleEnum;
import labo_gestion_api.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            // نبحث عن الدور في قاعدة البيانات
            if (roleRepository.findByName(roleEnum).isEmpty()) {
                // إذا غير موجود، ننشئه ونحفظه
                Role role = Role.builder()
                        .name(roleEnum)
                        .build();
                roleRepository.save(role);
                System.out.println("تمت إضافة الدور: " + roleEnum.name());
            }
        }
    }
}