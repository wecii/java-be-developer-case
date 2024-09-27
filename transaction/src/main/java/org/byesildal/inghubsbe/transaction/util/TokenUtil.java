package org.byesildal.inghubsbe.transaction.util;

public class TokenUtil {
    public static String cleanToken(String token) {
        return token.replace("Bearer ","");
    }
}
