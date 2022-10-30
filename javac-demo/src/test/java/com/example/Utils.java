package com.example;

/**
 * @author yulewei
 */
public class Utils {

    public static Class<?> loadClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
        }
        return null;
    }
}
