package org.byesildal.inghubsbe.customer.service;

import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.customer.exception.UserNotExistException;
import org.byesildal.inghubsbe.customer.model.UsernameResponse;
import org.byesildal.inghubsbe.data.entity.User;
import org.byesildal.inghubsbe.data.enums.UserRole;
import org.byesildal.inghubsbe.data.model.JsonResponse;
import org.byesildal.inghubsbe.data.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerMService {

    @Value("${auth-service-uri}")
    private String authServiceUri;

    private final UserService userService;
    private final WebClient.Builder webClientBuilder;

    public List<User> list(){
        return userService.getAllUsers();
    }

    public String extractUsername(String token){
        WebClient client = webClientBuilder.build();
        var responseMono = client.get()
                .uri(authServiceUri)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(LinkedHashMap.class);
        var map = (HashMap) responseMono.block().get("data");
        return (String) map.get("username");
    }

    public boolean isAdmin(String token){
        var email = extractUsername(token);
        var user = userService.getByEmail(email);
        if (user.isEmpty())
            throw new UserNotExistException("User not found");

        return user.get().getRole().equals(UserRole.ADMIN);
    }

    public User getUserByToken(String token){
        var email = extractUsername(token);
        var user = userService.getByEmail(email);
        if (user.isEmpty())
            throw new UserNotExistException("User not found");

        return user.get();
    }

    public User getUserByUUID(String uuid){
        var user = userService.getByUuid(UUID.fromString(uuid));
        if (user.isEmpty())
            throw new UserNotExistException("User not found");
        return user.get();
    }
}
