package org.byesildal.inghubsbe.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class UserCreateRequestModel {
    @NotBlank(message = "name-empty-or-null")
    @Length(min = 3, max = 64, message = "name-min-3-max-64")
    private String name;

    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{8,16}$", message = "password policy does not meet. requires only letter and digits min 8 char max 16 char")
    private String password;
}
