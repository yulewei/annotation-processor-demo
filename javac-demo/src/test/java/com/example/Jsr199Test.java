package com.example;

import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.main.Main;
import org.junit.Test;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author yulewei
 */
public class Jsr199Test {

    @Test
    public void testJavac() throws Exception {
        new File("target/classes/Greeting1.class").delete();
        Class<?> clazz = loadClassForName("Greeting1");
        assertNull(clazz);

        Main compiler = new Main("javac");
        compiler.compile(new String[]{"src/main/resources/Greeting1.java", "-d", "target/classes"});

        clazz = loadClassForName("Greeting1");
        assertNotNull(clazz);

        Method method = clazz.getDeclaredMethod("main", String[].class);
        assertNotNull(method);
    }

    @Test
    public void testJsr199() throws Exception {
        new File("target/classes/Greeting2.class").delete();
        Class<?> clazz = loadClassForName("Greeting2");
        assertNull(clazz);

        File file = new File(this.getClass().getResource("/Greeting2.java").toURI());
        JavaCompiler compiler = JavacTool.create();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
        compiler.getTask(null, fileManager, diagnostics, Arrays.asList("-d", "target/classes"), null, compilationUnits).call();

        clazz = loadClassForName("Greeting2");
        assertNotNull(clazz);

        Method method = clazz.getDeclaredMethod("main", String[].class);
        assertNotNull(method);
    }

    private Class<?> loadClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
        }
        return null;
    }
}
