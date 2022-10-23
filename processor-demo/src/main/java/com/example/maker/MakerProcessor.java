package com.example.maker;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.example.maker.PlusOne")
public class MakerProcessor extends AbstractProcessor {
    private JavacTrees trees;
    private TreeMaker maker;
    private Names names;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.trees = JavacTrees.instance(context);
        this.maker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> types, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(PlusOne.class)) {
            if (element.getKind().equals(ElementKind.METHOD)) {
                JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) trees.getTree(element);
                modifyMethod(methodDecl);
            }
        }
        return false;
    }

    /**
     * 修改方法内部实现，改为加 1
     *
     * <pre>
     * int y = x + 1;
     * return y;
     * </pre>
     */
    private void modifyMethod(JCTree.JCMethodDecl methodDecl) {
        Name x = methodDecl.params.head.name;
        Name y = names.fromString("y");
        // x + 1
        JCTree.JCBinary binary = maker.Binary(JCTree.Tag.PLUS, maker.Ident(x), maker.Literal(TypeTag.INT, 1));
        // int y = x + 1
        JCTree.JCVariableDecl decl = maker.VarDef(maker.Modifiers(0), y, maker.TypeIdent(TypeTag.INT), binary);
        // return y
        JCTree.JCReturn ret = maker.Return(maker.Ident(y));
        // 修改方法内部实现
        methodDecl.body.stats = List.of(decl, ret);
        System.out.println(methodDecl);
    }
}
