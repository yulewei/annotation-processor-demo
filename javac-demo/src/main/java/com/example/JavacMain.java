package com.example;

import com.sun.tools.javac.main.Main;

/**
 * @author yulewei
 */
public class JavacMain {

    public static void main(String[] args) {
        Main compiler = new Main("javac");
        compiler.compile(new String[]{"javac-demo/src/main/resources/Greeting1.java", "-d",
                "javac-demo/target/classes"});
    }
}
