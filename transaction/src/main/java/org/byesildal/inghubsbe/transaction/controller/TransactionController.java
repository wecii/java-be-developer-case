package org.byesildal.inghubsbe.transaction.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.data.model.JsonResponse;
import org.byesildal.inghubsbe.transaction.service.TransactionMService;
import org.byesildal.inghubsbe.transaction.util.TokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionMService transactionMService;
    @PostMapping(value = "/match")
    public ResponseEntity<JsonResponse> match(@RequestHeader("Authorization") String token) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK).body(new JsonResponse().data(transactionMService.match(TokenUtil.cleanToken(token))));
    }
}
