package com.test.processor;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author yulewei on 17/4/18.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DataAnnotationProcessor extends AbstractProcessor {
    private JavacProcessingEnvironment env;
    private Trees trees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = Trees.instance(processingEnv);
        this.env = (JavacProcessingEnvironment) processingEnv;
        this.treeMaker = TreeMaker.instance(env.getContext());
        this.names = Names.instance(env.getContext());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(Data.class)) {
            if (!(element instanceof TypeElement)) continue;

            TypeElement classElement = (TypeElement) element;
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(classElement);

            List<JCTree> methodDecls = List.nil();
            for (JCTree tree : classDecl.defs) {
                if (tree instanceof JCTree.JCVariableDecl) {
                    JCTree.JCMethodDecl methodGetter = this.createGetter((JCTree.JCVariableDecl) tree);
                    JCTree.JCMethodDecl methodSetter = this.createSetter((JCTree.JCVariableDecl) tree);
                    methodDecls = methodDecls.append(methodGetter);
                    methodDecls = methodDecls.append(methodSetter);
                }
            }
            classDecl.defs = classDecl.defs.appendList(methodDecls);
        }
        return true;
    }

    private JCTree.JCMethodDecl createGetter(JCTree.JCVariableDecl field) {
        JCTree.JCStatement returnStatement = treeMaker.Return(treeMaker.Ident(field));
        JCTree.JCBlock methodBody = treeMaker.Block(0, List.of(returnStatement));
        Name methodName = names.fromString("get" + this.toTitleCase(field.getName().toString()));
        JCTree.JCExpression methodType = (JCTree.JCExpression) field.getType();

        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), methodName, methodType,
                List.nil(), List.nil(), List.nil(), methodBody, null);
    }

    private JCTree.JCMethodDecl createSetter(JCTree.JCVariableDecl field) {
        JCTree.JCFieldAccess thisX = treeMaker.Select(treeMaker.Ident(names.fromString("this")), field.name);
        JCTree.JCAssign assign = treeMaker.Assign(thisX, treeMaker.Ident(field.name));

        JCTree.JCBlock methodBody = treeMaker.Block(0, List.of(treeMaker.Exec(assign)));
        Name methodName = names.fromString("set" + this.toTitleCase(field.getName().toString()));
        JCTree.JCVariableDecl param = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), field.name, field.vartype, null);
        JCTree.JCExpression methodType = treeMaker.Type(new Type.JCVoidType());

        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), methodName, methodType,
                List.nil(), List.of(param), List.nil(), methodBody, null);
    }

    public String toTitleCase(String str) {
        char first = str.charAt(0);
        if (first >= 'a' && first <= 'z') {
            first -= 32;
        }
        return first + str.substring(1);
    }

}
