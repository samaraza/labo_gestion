package labo_gestion_api.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/prof")
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Accès autorisé !";
    }

    @GetMapping("/authorities")
    public String getAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return "Not authenticated";
        Collection<?> authorities = auth.getAuthorities();
        return "Authorities: " + authorities.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }
}
