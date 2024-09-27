package org.byesildal.inghubsbe.auth.service;

import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.auth.exception.InvalidCredentialsException;
import org.byesildal.inghubsbe.auth.exception.UserExistException;
import org.byesildal.inghubsbe.auth.exception.UserNotExistException;
import org.byesildal.inghubsbe.auth.model.TokenModel;
import org.byesildal.inghubsbe.auth.model.UserCreateRequestModel;
import org.byesildal.inghubsbe.data.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService{
    private final UserService userService;
    private final UserDetailService userDetailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UUID create(UserCreateRequestModel customerCreateRequestModel) {
        if (userService.isCustomerExists(customerCreateRequestModel.getEmail()))
            throw new UserExistException("Email already exists");

        return userService.createCustomer(customerCreateRequestModel.getName(), customerCreateRequestModel.getEmail(), passwordEncoder.encode(customerCreateRequestModel.getPassword()));
    }

    public UUID createAdmin(UserCreateRequestModel customerCreateRequestModel) {
        if (userService.isCustomerExists(customerCreateRequestModel.getEmail()))
            throw new UserExistException("Email already exists");

        return userService.createCustomer(customerCreateRequestModel.getName(), customerCreateRequestModel.getEmail(), passwordEncoder.encode(customerCreateRequestModel.getPassword()));
    }

    public TokenModel login(String email, String password) {
        var user = userService.getByEmail(email);
        if (user.isEmpty())
            throw new UserNotExistException("User does not exist");

        if (!passwordEncoder.matches(password, user.get().getPassword()))
            throw new InvalidCredentialsException("Invalid credentials");

        return new TokenModel(jwtService.generateToken(email));
    }

    public boolean validateToken(String token) {
        var user = userDetailService.loadUserByUsername(jwtService.extractUserName(token));
        return jwtService.validateToken(token, user);
    }
}
