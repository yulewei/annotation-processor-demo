package com.example;

import com.sun.tools.javac.api.JavacTool;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * @author yulewei
 */
public class Jsr199Main {

    public static void main(String[] args) throws URISyntaxException, IOException {
        File file;
        if (args.length >= 1) {
            file = new File(args[0]);
        } else {
            file = new File(Jsr199Main.class.getResource("/Greeting2.java").toURI());
        }

        System.out.println("开始编译文件 " + file.getAbsolutePath());

        JavaCompiler compiler = JavacTool.create();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
        List<String> options = Arrays.asList("-d", "javac-demo/target/classes");

        compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits).call();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.format("%s:%d\n%s\n", diagnostic.getSource().getName(), diagnostic.getLineNumber(),
                    diagnostic.getMessage(null));
        }

        fileManager.close();
    }
}
