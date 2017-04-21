package com.test.proc.data;

import com.test.processor.DataAnnotationProcessor;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * @author yulewei on 17/4/18.
 */
public class ProcessorMain {

    public static void main(String[] args) throws URISyntaxException, IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        DataAnnotationProcessor processor = new DataAnnotationProcessor();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        File file = new File("example/src/main/java/com/test/javac/Person.java");
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics,
                Arrays.asList("-verbose", "-d", "example/target/classes"), null, compilationUnits);
        task.setProcessors(Arrays.asList(processor));
        task.call();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.format("Error on line %d in %s\n%s\n",
                    diagnostic.getLineNumber(), diagnostic.getSource().toUri(), diagnostic.getMessage(null));
        }

        fileManager.close();
    }
}
