package labo_gestion_api;

import labo_gestion_api.repository.RoleRepository;
import labo_gestion_api.model.Role;
import labo_gestion_api.model.RoleEnum;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
public class LaboGestionApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaboGestionApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			for (RoleEnum roleEnum : RoleEnum.values()) {
				if (roleRepository.findByName(roleEnum).isEmpty()) {
					roleRepository.save(
							Role.builder()
									.name(roleEnum)
									.build()
					);
					System.out.println("Role added: " + roleEnum.name());
				}
			}
		};
	}
}