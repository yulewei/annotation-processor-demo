package com.example.visitor;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementScanner8;
import java.util.Set;

/**
 * 扫描语法树
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class VisitorProcessor extends AbstractProcessor {
    private Trees trees;
    private MyElementScanner elementScanner;
    private MyTreeScanner treeScanner;
    private MyJCTreeScanner jcTreeScanner;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.elementScanner = new MyElementScanner();
        this.trees = Trees.instance(processingEnv);
        this.treeScanner = new MyTreeScanner();
        this.jcTreeScanner = new MyJCTreeScanner();
    }

    @Override
    public boolean process(Set<? extends TypeElement> types, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }

        for (Element element : roundEnv.getRootElements()) {
            // 打印 class 直接声明的 fields, methods, constructors, and member types
            System.out.format("=== %s 类，Element#getEnclosedElements ===\n", element.getSimpleName());
            TypeElement typeElement = (TypeElement) element;
            for (Element e : typeElement.getEnclosedElements()) {
                System.out.println(e.getKind() + ": " + e.getSimpleName());
            }
        }

        for (Element element : roundEnv.getRootElements()) {
            // 使用 ElementScanner 扫描语法树
            System.out.format("=== %s 类，ElementScanner ===\n", element.getSimpleName());
            elementScanner.scan(element);
        }

        for (Element element : roundEnv.getRootElements()) {
            // 使用 TreeScanner 扫描语法树
            System.out.format("=== %s 类，TreeScanner ===\n", element.getSimpleName());
            TreePath path = trees.getPath(element);
            treeScanner.scan(path, null);
        }

        for (Element element : roundEnv.getRootElements()) {
            // 使用 com.sun.tools.javac.tree 包下 TreeScanner 扫描语法树
            System.out.format("=== %s 类，JCTreeScanner ===\n", element.getSimpleName());
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(element);
            jcTreeScanner.scan(classDecl);
        }
        return false;
    }

    public static class MyElementScanner extends ElementScanner8<Void, Void> {

        @Override
        public Void visitType(TypeElement element, Void p) {
            System.out.println("类 " + element.getKind() + ": " + element.getSimpleName());
            return super.visitType(element, p);
        }

        @Override
        public Void visitExecutable(ExecutableElement element, Void p) {
            Symbol.MethodSymbol symbol = (Symbol.MethodSymbol) element;
            System.out.println("方法 " + symbol.getKind() + ": " + symbol.owner.getSimpleName() + "." + symbol.name);
            return super.visitExecutable(element, p);
        }

        @Override
        public Void visitVariable(VariableElement element, Void p) {
            if (element.getEnclosingElement().getKind() == ElementKind.CLASS) {
                System.out.println("字段 " + element.getKind() + ": " + element.getSimpleName());
            }
            return super.visitVariable(element, p);
        }
    }

    public static class MyTreeScanner extends TreePathScanner<Tree, Void> {

        @Override
        public Tree visitClass(ClassTree node, Void p) {
            System.out.println("类 " + node.getKind() + ": " + node.getSimpleName());
            return super.visitClass(node, p);
        }

        @Override
        public Tree visitMethod(MethodTree node, Void p) {
            Symbol owner = ((JCTree.JCMethodDecl) node).sym.owner;
            System.out.println("方法 " + node.getKind() + ": " + owner.getSimpleName() + "." + node.getName());
            return super.visitMethod(node, p);
        }

        @Override
        public Tree visitVariable(VariableTree node, Void p) {
            if (this.getCurrentPath().getParentPath().getLeaf() instanceof ClassTree) {
                System.out.println("字段 " + node.getKind() + ": " + node.getName());
            }
            return super.visitVariable(node, p);
        }
    }

    public static class MyJCTreeScanner extends TreeScanner {
        @Override
        public void visitClassDef(JCTree.JCClassDecl that) {
            System.out.println("类 " + that.getKind() + ": " + that.getSimpleName());
            super.visitClassDef(that);
        }

        @Override
        public void visitMethodDef(JCTree.JCMethodDecl that) {
            System.out.println("方法 " + that.getKind() + ": " + that.sym.owner.getSimpleName() + "." + that.getName());
            super.visitMethodDef(that);
        }

        @Override
        public void visitVarDef(JCTree.JCVariableDecl that) {
            if (that.sym != null && that.sym.owner instanceof Symbol.ClassSymbol) {
                System.out.println("字段 " + that.getKind() + ": " + that.getName());
            }
            super.visitVarDef(that);
        }
    }
}
