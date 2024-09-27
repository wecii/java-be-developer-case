package org.byesildal.inghubsbe.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginModel {
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{8,16}$", message = "password policy does not meet. requires only letter and digits min 8 char max 16 char")
    private String password;
}
