package com.example;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

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
