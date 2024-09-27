package org.byesildal.inghubsbe.customer.controller;

import org.byesildal.inghubsbe.customer.exception.UserNotExistException;
import org.byesildal.inghubsbe.data.model.JsonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class CustomerControllerAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<JsonResponse> methodArgumentNotValid(MethodArgumentNotValidException e) {
        var msg = "Validation failed";
        var errors = e.getBindingResult().getFieldErrors().stream().map(e1->{
            var map = new HashMap<String, String>();
            map.put(e1.getField(), e1.getDefaultMessage());
            return map;
        }).toList();
        var status = 400;
        return ResponseEntity.status(status).body(new JsonResponse().messageErr(msg, errors));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<JsonResponse> illegalArgument(IllegalArgumentException e) {
        var msg = e.getMessage();
        var status = 400;
        return ResponseEntity.status(status).body(new JsonResponse().fail(msg));
    }

    @ExceptionHandler({UserNotExistException.class})
    public ResponseEntity<JsonResponse> userNotExist(UserNotExistException e) {
        var msg = e.getMessage();
        var status = 404;
        return ResponseEntity.status(status).body(new JsonResponse().fail(msg));
    }


}
