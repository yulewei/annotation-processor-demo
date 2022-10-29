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

/**
 * @author yulewei
 */
public class Jsr199Main {

    public static void main(String[] args) throws URISyntaxException, IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        File file = new File(Jsr199Main.class.getResource("/Hello2.java").toURI());
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));

        compiler.getTask(null, fileManager, diagnostics, Arrays.asList("-d", "javac-demo/target/classes"), null, compilationUnits).call();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.format("%s:%d\n%s\n", diagnostic.getSource().getName(), diagnostic.getLineNumber(),
                    diagnostic.getMessage(null));
        }

        fileManager.close();
    }
}
