package com.example.visit;

import com.sun.tools.javac.api.JavacTool;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class VisitMain {

    public static void main(String[] args) throws IOException, URISyntaxException {
        JavaCompiler compiler = JavacTool.create();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        VisitProcessor processor = new VisitProcessor();

        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);
        File file = new File(VisitMain.class.getResource("/Example.java").toURI());
        Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles(Arrays.asList(file));

        CompilationTask task = compiler.getTask(null, manager, diagnostics, Arrays.asList("-d", "processor-demo/target/classes"), null, sources);
        task.setProcessors(Arrays.asList(processor));
        task.call();

        manager.close();
    }
}
