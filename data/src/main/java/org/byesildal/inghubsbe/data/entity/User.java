package org.byesildal.inghubsbe.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.byesildal.inghubsbe.data.enums.UserRole;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Base implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "email", length = 128, nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role = UserRole.CUSTOMER;

    @Column(name = "password", nullable = false, length = 128)
    private String password;
}
