package com.example;

import com.example.filer.GreetingProcessor;
import com.sun.tools.javac.api.JavacTool;
import org.junit.Test;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author yulewei
 */
public class GreetingProcessorTest {

    @Test
    public void testGreetingProcessor() throws Exception {
        String generatedClassName = "GeneratedGreeting";
        new File(String.format("target/classes/%s.class", generatedClassName)).delete();
        Class<?> clazz = Utils.loadClassForName(generatedClassName);
        assertNull(clazz);

        JavaCompiler compiler = JavacTool.create();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        GreetingProcessor processor = new GreetingProcessor();

        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);
        File file1 = new File(this.getClass().getResource("/Greeting1.java").toURI());
        File file2 = new File(this.getClass().getResource("/Greeting2.java").toURI());
        Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles(Arrays.asList(file1, file2));

        List<String> options = Arrays.asList("-d", "target/classes", String.format("-Agreeting.className=%s", generatedClassName));
        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, sources);
        task.setProcessors(Arrays.asList(processor));
        task.call();

        clazz = Utils.loadClassForName(generatedClassName);
        assertNotNull(clazz);

        Method method = clazz.getDeclaredMethod("main", String[].class);
        assertNotNull(method);
    }
}
