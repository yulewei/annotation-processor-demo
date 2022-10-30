package com.example;

import com.example.DogBuilder;
import com.example.filer.BuilderProcessor;
import com.example.filer.GreetingProcessor;
import com.sun.tools.javac.api.JavacTool;
import org.junit.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author yulewei
 */
public class BuilderProcessorTest {

    @Test
    public void testBuilder() {
        Dog dog = new DogBuilder().name("旺财").age(3).build();
        assertNotNull(dog);
        assertEquals(dog.name, "旺财");
        assertEquals(dog.age, 3);
    }

    @Test
    public void testBuilderProcessor() throws Exception {
        String generatedClassName = "com.example.DogBuilder";
        File generatedFile = new File("target/classes/com/example/DogBuilder.class");
        generatedFile.delete();
        assertFalse(generatedFile.exists());

        JavaCompiler compiler = JavacTool.create();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        BuilderProcessor processor = new BuilderProcessor();

        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);
        File file = new File("src/main/java/com/example/Dog.java");
        Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles(Arrays.asList(file));

        List<String> options = Arrays.asList("-d", "target/classes");
        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, sources);
        task.setProcessors(Arrays.asList(processor));
        task.call();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.format("%s:%d\n%s\n", diagnostic.getSource().getName(), diagnostic.getLineNumber(),
                    diagnostic.getMessage(null));
        }

        assertTrue(generatedFile.exists());
        Class<?> clazz = Utils.loadClassForName(generatedClassName);
        assertNotNull(clazz);

        Method method = clazz.getDeclaredMethod("build");
        assertNotNull(method);
    }
}
