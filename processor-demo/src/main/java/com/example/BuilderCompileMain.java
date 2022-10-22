package com.example;

import com.example.filer.BuilderProcessor;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class BuilderCompileMain {

    public static void main(String[] args) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        BuilderProcessor processor = new BuilderProcessor();

        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);
        File file = new File("processor-demo/src/main/java/com/example/Dog.java");
        Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles(Arrays.asList(file));

        CompilationTask task = compiler.getTask(null, manager, diagnostics, Arrays.asList("-d", "processor-demo/target/classes"), null, sources);
        task.setProcessors(Arrays.asList(processor));
        task.call();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.format("%s:%d\n%s\n", diagnostic.getSource().getName(), diagnostic.getLineNumber(),
                    diagnostic.getMessage(null));
        }
        manager.close();
    }
}
