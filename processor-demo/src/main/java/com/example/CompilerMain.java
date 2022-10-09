package com.example;

import com.example.processor.DemoProcessor;
import com.sun.tools.javac.api.JavacTool;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author yulewei
 */
public class CompilerMain {

    public static void main(String[] args) throws IOException {
        JavaCompiler compiler = JavacTool.create();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        DemoProcessor processor = new DemoProcessor();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        File file1 = new File("processor-demo/src/main/java/com/example/Person.java");
        File file2 = new File("processor-demo/src/main/java/com/example/ProcessorMain.java");
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file1));

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics,
                Arrays.asList("-verbose", "-cp", "processor/target/processor-0.0.1-SNAPSHOT.jar",
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
