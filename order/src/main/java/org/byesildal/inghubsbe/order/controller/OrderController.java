package org.byesildal.inghubsbe.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.data.model.JsonResponse;
import org.byesildal.inghubsbe.order.model.OrderRequest;
import org.byesildal.inghubsbe.order.model.OrderResponse;
import org.byesildal.inghubsbe.order.service.OrderMService;
import org.byesildal.inghubsbe.order.util.TokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderMService orderMService;

    @PostMapping
    public ResponseEntity<JsonResponse> order(@RequestHeader("Authorization") String token,
                                              @RequestBody @Valid OrderRequest orderRequest) throws JsonProcessingException {
        orderMService.manageOrder(TokenUtil.cleanToken(token), orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<JsonResponse> list(@RequestHeader("Authorization") String token){
        return ResponseEntity.ok().body(new JsonResponse().data(orderMService.list(TokenUtil.cleanToken(token)).stream().map(OrderResponse::new).toList()));
    }

    @DeleteMapping(value = "/{order-id}")
    public ResponseEntity<JsonResponse> delete(@RequestHeader("Authorization") String token,
                                               @PathVariable("order-id") String orderId){
        orderMService.sendCancelOrder(TokenUtil.cleanToken(token), orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/match")
    public ResponseEntity<JsonResponse> match(@RequestHeader("Authorization") String token){
        return ResponseEntity.ok().body(new JsonResponse().data(orderMService.match(TokenUtil.cleanToken(token))));
    }
}
