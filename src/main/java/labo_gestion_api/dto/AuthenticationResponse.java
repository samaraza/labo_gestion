package labo_gestion_api.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationResponse {
    private Integer id;
    private String token;
    private String email;
    private String firstname;
    private String lastname;
    private String role;
}
