package com.example;

import com.example.filer.GreetingProcessor;
import com.example.maker.PlusProcessor;
import com.sun.tools.javac.api.JavacTool;
import org.junit.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author yulewei
 */
public class PlusProcessorTest {

    @Test
    public void testPlusProcessor() throws Exception {
        String className = "PlusExample";
        String resourceName = "/PlusExample.java";
        new File(String.format("target/classes/%s.class", className)).delete();
        Class<?> clazz = Utils.loadClassForName(className);
        assertNull(clazz);

        JavaCompiler compiler = JavacTool.create();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        PlusProcessor processor = new PlusProcessor();

        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);
        File file = new File(this.getClass().getResource(resourceName).toURI());
        Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles(Arrays.asList(file));

        List<String> options = Arrays.asList("-d", "target/classes");
        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, sources);
        task.setProcessors(Arrays.asList(processor));
        task.call();

        clazz = Utils.loadClassForName(className);
        assertNotNull(clazz);

        Method method = clazz.getDeclaredMethod("func", int.class);
        assertNotNull(method);
        int res = (int) method.invoke(clazz.newInstance(), 42);
        assertEquals(res, 43);
    }
}
