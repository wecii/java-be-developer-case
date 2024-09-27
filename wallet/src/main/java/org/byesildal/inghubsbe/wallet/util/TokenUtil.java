package org.byesildal.inghubsbe.wallet.util;

public class TokenUtil {
    public static String cleanToken(String token) {
        return token.replace("Bearer ","");
    }
}
