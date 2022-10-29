package com.example.filer;

import com.sun.tools.javac.api.JavacTool;

import javax.annotation.Generated;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

@Generated(value = "", date = "")
public class GreetingMain {

    public static void main(String[] args) throws IOException, URISyntaxException {
        JavaCompiler compiler = JavacTool.create();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        GreetingProcessor processor = new GreetingProcessor();

        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);
        File file1 = new File(GreetingMain.class.getResource("/Greeting1.java").toURI());
        File file2 = new File(GreetingMain.class.getResource("/Greeting2.java").toURI());
        Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles(Arrays.asList(file1, file2));

        CompilationTask task = compiler.getTask(null, manager, diagnostics, Arrays.asList("-d", "processor-demo" +
                "/target/classes", "-Agreeting.className=GeneratedGreeting"), null, sources);
        task.setProcessors(Arrays.asList(processor));
        task.call();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.format("%s:%d\n%s\n", diagnostic.getSource().getName(), diagnostic.getLineNumber(),
                    diagnostic.getMessage(null));
        }
        manager.close();
    }
}
