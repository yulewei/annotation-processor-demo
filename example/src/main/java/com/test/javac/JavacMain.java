package com.test.javac;

import com.sun.tools.javac.main.Main;

/**
 * @author yulewei on 17/4/19.
 */
public class JavacMain {
    public static void main(String[] args) {
        Main compiler = new Main("javac");
//        compiler.compile(new String[]{"example/src/main/java/com/test/javac/Hello.java", "-d", "example/target/classes"});
        compiler.compile(new String[]{"example/src/main/resources/Hello.java", "-d", "example/target/classes"});
    }
}
