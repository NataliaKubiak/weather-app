package org.example.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Validator {

    public static boolean isSamePassword(String password, String repeatPassword) {
        return password != null && password.equals(repeatPassword);
    }
}
