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
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@code @PlusOne} 注解处理器。修改 @PlusOne 标注的方法的内部实现，改造为 `x + 1`
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.example.maker.PlusOne")
public class PlusProcessor extends AbstractProcessor {
    private JavacTrees trees;
    private TreeMaker maker;
    private Names names;
    private Messager messager;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.trees = JavacTrees.instance(context);
        this.maker = TreeMaker.instance(context);
        this.names = Names.instance(context);
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> types, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(PlusOne.class)) {
            if (element.getKind().equals(ElementKind.METHOD)) {
                JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) trees.getTree(element);
                modifyToPlusOneMethod(methodDecl);
            }
        }
        return false;
    }

    /**
     * 修改方法内部实现，改为加 1
     *
     * <pre>
     * return x + 1;
     * </pre>
     */
    private void modifyToPlusOneMethod(JCTree.JCMethodDecl methodDecl) {
        JCTree.JCVariableDecl param = methodDecl.params.head;
        if (!(param.vartype instanceof JCTree.JCPrimitiveTypeTree)) {
            messager.printMessage(ERROR, "方法参数必须为 int 类型", methodDecl.sym);
            return;
        }
        JCTree.JCPrimitiveTypeTree vartype = (JCTree.JCPrimitiveTypeTree) param.vartype;
        if (!vartype.getPrimitiveTypeKind().equals(TypeKind.INT)) {
            messager.printMessage(ERROR, "方法参数必须为 int 类型", methodDecl.sym);
            return;
        }

        // x + 1
        JCTree.JCBinary binary = maker.Binary(JCTree.Tag.PLUS, maker.Ident(param.name), maker.Literal(TypeTag.INT, 1));
        JCTree.JCReturn ret = maker.Return(binary);
        // 修改方法内部实现
        methodDecl.body.stats = List.of(ret);
        // methodDecl.restype = maker.TypeIdent(TypeTag.INT);
        System.out.println(methodDecl);
    }
}
