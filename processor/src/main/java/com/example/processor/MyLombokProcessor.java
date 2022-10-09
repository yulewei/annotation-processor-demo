package com.example.processor;

import com.example.Data;
import com.example.Slf4j;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author yulewei
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MyLombokProcessor extends AbstractProcessor {
    private static final Logger log = LoggerFactory.getLogger(MyLombokProcessor.class);

    private JavacProcessingEnvironment env;
    private Trees trees;
    private TreeMaker maker;
    private Names names;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = Trees.instance(processingEnv);
        this.env = (JavacProcessingEnvironment) processingEnv;
        this.maker = TreeMaker.instance(env.getContext());
        this.names = Names.instance(env.getContext());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }

        // @Data 注解，代码生成
        for (Element element : roundEnv.getElementsAnnotatedWith(Data.class)) {
            if (!(element instanceof TypeElement)) {
                continue;
            }

            TypeElement classElement = (TypeElement) element;
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(classElement);
            log.debug("@Data, process class: {}", classDecl.getSimpleName().toString());
            // 代码生成
            handleData(classDecl);
        }

        // @Slf4j 注解，代码生成
        for (Element element : roundEnv.getElementsAnnotatedWith(Slf4j.class)) {
            if (!(element instanceof TypeElement)) {
                continue;
            }

            TypeElement classElement = (TypeElement) element;
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(classElement);
            log.debug("@Slf4j, process class: {}", classDecl.getSimpleName().toString());
            // 代码生成
            handleSlf4jLog(classDecl);
        }
        return true;
    }

    private void handleData(JCTree.JCClassDecl classDecl) {
        List<JCTree> methodDecls = List.nil();
        for (JCTree tree : classDecl.defs) {
            if (tree instanceof JCTree.JCVariableDecl) {
                JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl) tree;
                log.debug("field: {}", fieldDecl);

                // 创建 getter 方法
                String methodGetterName = Utils.toGetterName(fieldDecl);
                if (!Utils.methodExists(methodGetterName, classDecl)) {
                    JCTree.JCMethodDecl methodGetter = this.createGetter(fieldDecl);
                    log.debug("createGetter: {}", methodGetter);
                    methodDecls = methodDecls.append(methodGetter);
                } else {
                    log.debug("methodExists: {}", methodGetterName);
                }

                // 创建 setter 方法
                String methodSetterName = Utils.toSetterName(fieldDecl);
                if (!Utils.methodExists(methodSetterName, classDecl)) {
                    JCTree.JCMethodDecl methodSetter = this.createSetter(fieldDecl);
                    log.debug("createSetter: {}", methodSetter);
                    methodDecls = methodDecls.append(methodSetter);
                } else {
                    log.debug("methodExists: {}", methodSetterName);
                }
            }
        }
        classDecl.defs = classDecl.defs.appendList(methodDecls);
    }

    private void handleSlf4jLog(JCTree.JCClassDecl classDecl) {
        // private static final <loggerType> log = <factoryMethod>(<parameter>);
        JCTree.JCExpression loggerType = Utils.chainDotsString(maker, names, "org.slf4j.Logger");
        JCTree.JCExpression factoryMethod = Utils.chainDotsString(maker, names, "org.slf4j.LoggerFactory.getLogger");

        JCTree.JCExpression loggerName = selfType(classDecl);
        JCTree.JCMethodInvocation factoryMethodCall = maker.Apply(List.nil(), factoryMethod, List.of(loggerName));

        JCTree.JCVariableDecl fieldDecl = maker.VarDef(
                maker.Modifiers(Flags.PRIVATE | Flags.FINAL | Flags.STATIC),
                names.fromString("log"), loggerType, factoryMethodCall);

        log.debug("log field: {}", fieldDecl);

        classDecl.defs = classDecl.defs.prepend(fieldDecl);
    }

    private JCTree.JCMethodDecl createGetter(JCTree.JCVariableDecl field) {
        JCTree.JCStatement returnStatement = maker.Return(maker.Ident(field));
        JCTree.JCBlock methodBody = maker.Block(0, List.of(returnStatement));
        Name methodName = names.fromString(Utils.toGetterName(field));
        JCTree.JCExpression methodType = (JCTree.JCExpression) field.getType();

        return maker.MethodDef(maker.Modifiers(Flags.PUBLIC), methodName, methodType,
                List.nil(), List.nil(), List.nil(), methodBody, null);
    }

    private JCTree.JCMethodDecl createSetter(JCTree.JCVariableDecl field) {
        JCTree.JCFieldAccess thisX = maker.Select(maker.Ident(names.fromString("this")), field.name);
        JCTree.JCAssign assign = maker.Assign(thisX, maker.Ident(field.name));

        JCTree.JCBlock methodBody = maker.Block(0, List.of(maker.Exec(assign)));
        Name methodName = names.fromString(Utils.toSetterName(field));
        JCTree.JCVariableDecl param = maker.VarDef(maker.Modifiers(Flags.PARAMETER), field.name, field.vartype, null);
        JCTree.JCExpression methodType = maker.Type(new Type.JCVoidType());

        return maker.MethodDef(maker.Modifiers(Flags.PUBLIC), methodName, methodType,
                List.nil(), List.of(param), List.nil(), methodBody, null);
    }

    private JCTree.JCFieldAccess selfType(JCTree.JCClassDecl classDecl) {
        Name name = classDecl.name;
        return maker.Select(maker.Ident(name), names.fromString("class"));
    }
}
