package org.byesildal.inghubsbe.wallet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.data.entity.Asset;
import org.byesildal.inghubsbe.wallet.model.DepositWithdrawRequest;
import org.byesildal.inghubsbe.wallet.service.WalletMService;
import org.byesildal.inghubsbe.wallet.util.TokenUtil;
import org.byesildal.inghubsbe.data.model.JsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/wallet")
public class WalletController {
    private final WalletMService walletMService;

    @GetMapping("/asset")
    public ResponseEntity<JsonResponse> listAssets() {
        return ResponseEntity.ok().body(new JsonResponse().data(walletMService.listAssets()));
    }

    @PostMapping("/asset")
    public ResponseEntity<JsonResponse> createAsset(@RequestParam(name = "asset-name") String assetName) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new JsonResponse().data(walletMService.createAsset(assetName)));
    }

    @GetMapping
    public ResponseEntity<JsonResponse> listWallets(@RequestHeader("Authorization") String token, @RequestParam(value = "user-id", required = false) String userId) {
        return ResponseEntity.ok().body(new JsonResponse().data(walletMService.listWallets(TokenUtil.cleanToken(token), userId)));
    }

    @PostMapping
    public ResponseEntity<JsonResponse> create(@RequestHeader("Authorization") String token, @RequestParam(value = "user-id", required = false) String userId, @RequestParam(value = "asset-id") String assetId){
        var created = walletMService.create(assetId, userId, TokenUtil.cleanToken(token));
        return ResponseEntity.status(HttpStatus.CREATED).body(new JsonResponse().data(created));
    }

    @PostMapping(value = "/{wallet-id}/deposit")
    public ResponseEntity<JsonResponse> deposit(@RequestHeader("Authorization") String token, @PathVariable(value = "wallet-id") String walletId, @RequestBody @Valid DepositWithdrawRequest depositWithdrawRequest) throws JsonProcessingException {
        walletMService.deposit(TokenUtil.cleanToken(token), walletId, depositWithdrawRequest);
        return ResponseEntity.ok().body(new JsonResponse().ok("Deposit transaction created"));
    }

    @PostMapping(value = "/{asset-id}/withdrawal")
    public ResponseEntity<JsonResponse> withdrawal(@RequestHeader("Authorization") String token, @PathVariable(value = "asset-id") String walletId, @RequestBody @Valid DepositWithdrawRequest depositWithdrawRequest) throws JsonProcessingException {
        walletMService.withdraw(TokenUtil.cleanToken(token), walletId, depositWithdrawRequest);
        return ResponseEntity.ok().body(new JsonResponse().ok("Withdrawal transaction created"));
    }
}
