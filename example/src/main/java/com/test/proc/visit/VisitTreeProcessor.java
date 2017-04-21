package com.test.proc.visit;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("*")
public class VisitTreeProcessor extends AbstractProcessor {
    private Trees trees;
    private MyScanner scanner;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = Trees.instance(processingEnv);
        this.scanner = new MyScanner();
    }

    public boolean process(Set<? extends TypeElement> types, RoundEnvironment environment) {
        if (!environment.processingOver()) {
            for (Element element : environment.getRootElements()) {
                TreePath path = trees.getPath( element );
                scanner.scan(path, null);
            }
        }
        return true;
    }

    public class MyScanner extends TreePathScanner<Tree, Void> {

        public Tree visitClass(ClassTree node, Void p) {
            System.out.println("类 " + node.getKind() + ": " + node.getSimpleName());
            return super.visitClass(node, p);
        }

        public Tree visitMethod(MethodTree node, Void p) {
            System.out.println("方法 " + node.getKind() + ": " + node.getName());
            return super.visitMethod(node, p);
        }

        public Tree visitVariable(VariableTree node, Void p) {
            if (this.getCurrentPath().getParentPath().getLeaf() instanceof ClassTree) {
                System.out.println("字段 " + node.getKind() + ": " + node.getName());
            }
            return super.visitVariable(node, p);
        }
    }
}
