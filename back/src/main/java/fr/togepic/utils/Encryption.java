package fr.togepic.utils;

import org.mindrot.jbcrypt.BCrypt;

public class Encryption {

    public static String encrypt(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    public static boolean verify(String in, String out) {
        return BCrypt.checkpw(in, out);
    }
}
