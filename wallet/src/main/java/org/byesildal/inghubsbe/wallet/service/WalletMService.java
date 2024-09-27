package org.byesildal.inghubsbe.wallet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.wallet.configuration.RabbitMQConfig;
import org.byesildal.inghubsbe.wallet.exception.*;
import org.byesildal.inghubsbe.wallet.model.WalletResponse;
import org.byesildal.inghubsbe.wallet.model.AssetResponse;
import org.byesildal.inghubsbe.wallet.model.DepositWithdrawRequest;
import org.byesildal.inghubsbe.data.entity.Wallet;
import org.byesildal.inghubsbe.data.entity.User;
import org.byesildal.inghubsbe.data.enums.UserRole;
import org.byesildal.inghubsbe.data.model.DepositEvent;
import org.byesildal.inghubsbe.data.model.WithdrawEvent;
import org.byesildal.inghubsbe.data.service.WalletService;
import org.byesildal.inghubsbe.data.service.AssetService;
import org.byesildal.inghubsbe.data.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletMService {

    @Value("${auth-service-uri}")
    private String authServiceUri;

    private final ObjectMapper mapper = new ObjectMapper();

    private final RabbitTemplate rabbitTemplate;
    private final UserService userService;
    private final AssetService assetService;
    private final WalletService walletService;
    private final WebClient.Builder webClientBuilder;

    public AssetResponse createAsset(String assetName){
        return new AssetResponse(assetService.create(assetName));
    }

    public List<AssetResponse> listAssets(){
        return assetService.getAll().stream().map(AssetResponse::new).toList();
    }

    public List<WalletResponse> listWallets(String token, String userId){
        var currentUser = getUserByToken(token);
        var processUser = currentUser;
        if (userId != null && currentUser.getRole().equals(UserRole.ADMIN)) {
            var optUser = userService.getByUuid(UUID.fromString(userId));
            if (optUser.isEmpty())
                throw new UserNotExistException("User does not exist");
            processUser = optUser.get();
        }

        return walletService.getByUser(processUser).stream().map(WalletResponse::new).toList();
    }

    public WalletResponse create(String assetId, String userId, String token){
        var currentUser = getUserByToken(token);
        var processUser = currentUser;
        if (currentUser.getRole() == UserRole.ADMIN && userId != null) {
            var tempUser = userService.getByUuid(UUID.fromString(userId));
            if (tempUser.isEmpty())
                throw new UserNotExistException("User not found");
            processUser = tempUser.get();
        }

        var asset = assetService.getById(assetId);
        if (asset == null)
            throw new AssetNotExistException("Asset not found");

        var assetExist = walletService.getByUserAndAsset(processUser, asset);
        if (assetExist != null)
            throw new DuplicateException("Asset already exist");

        var wallet = new Wallet();
        wallet.setBalance(new BigDecimal(0));
        wallet.setUsableBalance(new BigDecimal(0));
        wallet.setAsset(asset);
        wallet.setUser(processUser);
        var saved = walletService.save(wallet);

        return new WalletResponse(saved.getId().toString(), saved.getBalance(), saved.getUsableBalance(), new AssetResponse(saved.getAsset().getId().toString(), saved.getAsset().getName()));
    }

    public void deposit(String token, String walletId, DepositWithdrawRequest depositWithdrawRequest) throws JsonProcessingException {
        var user = getUserByToken(token);
        var wallet = walletService.getById(walletId);
        if (wallet == null)
            throw new ForbiddenException("Asset Not Found");

        if (!user.getRole().equals(UserRole.ADMIN) && !wallet.getUser().getId().equals(user.getId()))
            throw new ForbiddenException("You do not have permission to withdraw this asset");

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.TRANSACTION_ROUTING_KEY, mapper.writeValueAsString(new DepositEvent(depositWithdrawRequest.getAmount(), wallet.getId().toString())));
    }

    public void withdraw(String token, String walletId, DepositWithdrawRequest depositWithdrawRequest) throws JsonProcessingException {
        var user = getUserByToken(token);
        var wallet = walletService.getById(walletId);

        if (!user.getRole().equals(UserRole.ADMIN) && !wallet.getUser().getId().equals(user.getId()))
            throw new ForbiddenException("You do not have permission to withdraw this wallet");

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.TRANSACTION_ROUTING_KEY, mapper.writeValueAsString(new WithdrawEvent(depositWithdrawRequest.getAmount(), wallet.getId().toString())));
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
