package com.missionshakti.auth.util;

import java.util.UUID;

public class PasswordUtil {

    public static String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
