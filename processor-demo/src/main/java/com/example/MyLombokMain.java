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
import java.util.List;

/**
 * @author yulewei
 */
public class MyLombokMain {

    public static void main(String[] args) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        MyLombokProcessor processor = new MyLombokProcessor();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        File file = new File("processor-demo/src/main/java/com/example/Person.java");
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));

        List<String> options = Arrays.asList("-Alombok.verbose=true", "-d", "processor-demo/target/classes");
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
        task.setProcessors(Arrays.asList(processor));
        task.call();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.format("%s:%d\n%s\n", diagnostic.getSource().getName(), diagnostic.getLineNumber(), diagnostic.getMessage(null));
        }
        fileManager.close();
    }
}
