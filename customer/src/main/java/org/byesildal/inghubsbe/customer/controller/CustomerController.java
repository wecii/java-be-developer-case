package org.byesildal.inghubsbe.customer.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.customer.service.CustomerMService;
import org.byesildal.inghubsbe.data.enums.UserRole;
import org.byesildal.inghubsbe.data.model.JsonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/customer")
public class CustomerController {
    private final CustomerMService customerMService;

    // admin route
    @GetMapping
    public ResponseEntity<JsonResponse> getOrList(@RequestHeader(value = "Authorization") String token, @RequestParam(value = "customer-id", required = false) String customerId) {
        var customer = customerMService.getUserByToken(token.replace("Bearer ",""));
        if (customer.getRole().equals(UserRole.ADMIN)){
            if(customerId != null && !customerId.isEmpty())
                return ResponseEntity.ok().body(new JsonResponse().data(customerMService.getUserByUUID(customerId)));
            else
                return ResponseEntity.ok().body(new JsonResponse().data(customerMService.list()));
        }

       return ResponseEntity.ok().body(new JsonResponse().data(customer));
    }

}
