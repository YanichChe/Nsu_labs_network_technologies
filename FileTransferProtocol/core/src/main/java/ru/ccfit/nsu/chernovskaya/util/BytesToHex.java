package ru.ccfit.nsu.chernovskaya.util;

import lombok.experimental.UtilityClass;
import java.security.MessageDigest;

@UtilityClass
public class BytesToHex {
    public static String convertToString(MessageDigest md) {
        var result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
