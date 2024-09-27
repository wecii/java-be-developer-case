package org.byesildal.inghubsbe.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.auth.model.*;
import org.byesildal.inghubsbe.auth.service.AuthService;
import org.byesildal.inghubsbe.auth.service.JwtService;
import org.byesildal.inghubsbe.auth.util.TokenUtil;
import org.byesildal.inghubsbe.data.model.JsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping(value = "/register")
    public ResponseEntity<JsonResponse> create(@RequestBody @Valid UserCreateRequestModel userCreateRequestModel) {
        var createdCustomerId = authService.create(userCreateRequestModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(new JsonResponse().data(new UserCreateResponseModel(createdCustomerId)));
    }

    @PostMapping(value = "/register/admin")
    public ResponseEntity<JsonResponse> createAdmin(@RequestBody @Valid UserCreateRequestModel userCreateRequestModel) {
        var createdCustomerId = authService.createAdmin(userCreateRequestModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(new JsonResponse().data(new UserCreateResponseModel(createdCustomerId)));
    }

    @PostMapping
    public ResponseEntity<JsonResponse> login(@RequestBody @Valid UserLoginModel userLoginModel) {
        var token = authService.login(userLoginModel.getEmail(), userLoginModel.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(new JsonResponse().data(token));
    }

    @PostMapping(value = "/validate")
    public ResponseEntity<JsonResponse> validate(@RequestHeader(value = "Authorization") String token) {
        boolean isValid = authService.validateToken(TokenUtil.cleanToken(token));
        return ResponseEntity.status(isValid ? HttpStatus.OK : HttpStatus.UNAUTHORIZED).body(new JsonResponse().data(new TokenValidationResponse(isValid)));
    }

    @GetMapping(value = "/extract-username")
    public ResponseEntity<JsonResponse> extractUsername(@RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new JsonResponse().data(new UsernameResponse(jwtService.extractUserName(TokenUtil.cleanToken(token)))));
    }
}
