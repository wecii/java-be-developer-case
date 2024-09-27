package org.byesildal.inghubsbe.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JsonResponse {
    public JsonResponse ok(String message){
        this.status = true;
        this.message = message;
        return this;
    }

    public JsonResponse fail(String message){
        this.message = message;
        return this;
    }

    public JsonResponse data(Object data){
        this.status = true;
        this.data = data;
        return this;
    }

    public JsonResponse error(Object error){
        this.error = error;
        return this;
    }

    public JsonResponse messageErr(String message, Object error){
        this.message = message;
        this.error = error;
        return this;
    }

    public JsonResponse messageData(String message, Object data){
        this.status = true;
        this.message = message;
        this.data = data;
        return this;
    }

    private String message;
    private boolean status = false;
    private Object data;
    private Object error;
}