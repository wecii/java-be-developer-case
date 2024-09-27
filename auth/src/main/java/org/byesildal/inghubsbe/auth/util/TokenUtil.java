package org.byesildal.inghubsbe.auth.util;

public class TokenUtil {
    public static String cleanToken(String token) {
        return token.replace("Bearer ","");
    }
}
