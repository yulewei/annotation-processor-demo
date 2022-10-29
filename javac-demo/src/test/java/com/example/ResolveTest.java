package com.example;

import com.sun.tools.javac.api.ClientCodeWrapper;
import com.sun.tools.javac.comp.Resolve;
import com.sun.tools.javac.util.JCDiagnostic;
import org.junit.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author yulewei
 * @see Resolve
 */
public class ResolveTest {

    /**
     * 编译错误：找不到符号（cannot find symbol）。编译错误的提示内容：
     *
     * <pre>
     * 找不到符号
     *   符号:   变量 bar
     *   位置: 类 CantResolve
     * </pre>
     *
     * @see com.sun.tools.javac.comp.Resolve.SymbolNotFoundError
     */
    @Test
    public void testCantResolve() throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        File file = new File(ResolveTest.class.getResource("/CantResolve.java").toURI());
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));

        compiler.getTask(null, fileManager, diagnostics, Arrays.asList("-d", "target/classes"), null, compilationUnits).call();

        assertEquals(diagnostics.getDiagnostics().size(), 1);

        Diagnostic<? extends JavaFileObject> diagnostic = diagnostics.getDiagnostics().get(0);
        System.out.format("%s:%d\n%s\n", diagnostic.getSource().getName(), diagnostic.getLineNumber(),
                diagnostic.getMessage(null));

        assertEquals(diagnostic.getKind(), Diagnostic.Kind.ERROR);
        assertEquals(diagnostic.getLineNumber(), 2);

        JCDiagnostic jcDiagnostic = ((ClientCodeWrapper.DiagnosticSourceUnwrapper) diagnostic).d;
        assertEquals(jcDiagnostic.getDiagnosticPosition().getTree().toString(), "bar");
        fileManager.close();
    }
}
