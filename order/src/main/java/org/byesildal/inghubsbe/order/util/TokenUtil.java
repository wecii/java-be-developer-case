package org.byesildal.inghubsbe.order.util;

public class TokenUtil {
    public static String cleanToken(String token) {
        return token.replace("Bearer ","");
    }
}
