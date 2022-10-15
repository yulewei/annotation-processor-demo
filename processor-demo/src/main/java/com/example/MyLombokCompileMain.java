package com.example;

import com.example.processor.MyLombokProcessor;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author yulewei
 */
public class MyLombokCompileMain {

    public static void main(String[] args) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        MyLombokProcessor processor = new MyLombokProcessor();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        File file = new File("processor-demo/src/main/java/com/example/Person.java");
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics,
                Arrays.asList("-verbose", "-cp", "mylombok/target/mylombok-0.0.1-SNAPSHOT.jar",
                        "-d", "processor-demo/target/classes"), null, compilationUnits);
        task.setProcessors(Arrays.asList(processor));
        task.call();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.format("Error on line %d in %s\n%s\n",
                    diagnostic.getLineNumber(), diagnostic.getSource().toUri(), diagnostic.getMessage(null));
        }

        fileManager.close();
    }
}
