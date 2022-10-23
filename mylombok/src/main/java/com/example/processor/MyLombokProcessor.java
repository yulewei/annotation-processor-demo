package com.example.processor;

import com.example.Data;
import com.example.Getter;
import com.example.Setter;
import com.example.Slf4j;
import com.google.auto.service.AutoService;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
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
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Set;

import static com.example.processor.MyLombokProcessor.VERBOSE;
import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * @author yulewei
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.example.Getter", "com.example.Setter", "com.example.Data", "com.example.Slf4j"})
@SupportedOptions({VERBOSE})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MyLombokProcessor extends AbstractProcessor {

    protected static final String VERBOSE = "lombok.verbose";

    private JavacTrees trees;
    private TreeMaker maker;
    private Names names;
    private Messager messager;
    private boolean verbose;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.trees = JavacTrees.instance(context);
        this.maker = TreeMaker.instance(context);
        this.names = Names.instance(context);
        this.messager = processingEnv.getMessager();
        this.verbose = Boolean.parseBoolean(processingEnv.getOptions().get(VERBOSE));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }

        // @Getter 注解，代码生成
        for (Element element : roundEnv.getElementsAnnotatedWith(Getter.class)) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                if (verbose) {
                    messager.printMessage(NOTE, "@Getter, process class: " + element.getSimpleName(), element);
                }
                // 代码生成
                JCTree.JCClassDecl classDecl = trees.getTree(typeElement);
                handleGetter(classDecl);
            } else if (element instanceof VariableElement) {
                VariableElement variableElement = (VariableElement) element;
                JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl) trees.getTree(variableElement);
                if (variableDecl.sym != null && variableDecl.sym.owner instanceof Symbol.ClassSymbol) {
                    if (verbose) {
                        messager.printMessage(NOTE, "@Getter, process filed: " + element.getSimpleName(), element);
                    }
                    // 代码生成
                    JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(variableDecl.sym.owner);
                    handleGetter(classDecl, variableDecl);
                }
            }
        }

        // @Setter 注解，代码生成
        for (Element element : roundEnv.getElementsAnnotatedWith(Setter.class)) {
            if (element instanceof TypeElement) {
                if (verbose) {
                    messager.printMessage(NOTE, "@Setter, process class: " + element.getSimpleName(), element);
                }
                // 代码生成
                TypeElement typeElement = (TypeElement) element;
                JCTree.JCClassDecl classDecl = trees.getTree(typeElement);
                handleSetter(classDecl);
            } else if (element instanceof VariableElement) {
                VariableElement variableElement = (VariableElement) element;
                JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl) trees.getTree(variableElement);
                if (variableDecl.sym != null && variableDecl.sym.owner instanceof Symbol.ClassSymbol) {
                    if (verbose) {
                        messager.printMessage(NOTE, "@Setter, process filed: " + element.getSimpleName(), element);
                    }
                    // 代码生成
                    JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(variableDecl.sym.owner);
                    handleSetter(classDecl, variableDecl);
                }
            }
        }

        // @Data 注解，代码生成
        for (Element element : roundEnv.getElementsAnnotatedWith(Data.class)) {
            if (!(element instanceof TypeElement)) {
                continue;
            }
            if (verbose) {
                messager.printMessage(NOTE, "@Data, process class: " + element.getSimpleName(), element);
            }
            // 代码生成
            TypeElement typeElement = (TypeElement) element;
            JCTree.JCClassDecl classDecl = trees.getTree(typeElement);
            handleData(classDecl);
        }

        // @Slf4j 注解，代码生成
        for (Element element : roundEnv.getElementsAnnotatedWith(Slf4j.class)) {
            if (!(element instanceof TypeElement)) {
                continue;
            }
            if (verbose) {
                messager.printMessage(NOTE, "@Slf4j, process class: " + element.getSimpleName(), element);
            }
            // 代码生成
            TypeElement typeElement = (TypeElement) element;
            JCTree.JCClassDecl classDecl = trees.getTree(typeElement);
            handleSlf4jLog(classDecl);
        }
        return true;
    }

    private void handleGetter(JCTree.JCClassDecl classDecl) {
        List<JCTree> methodDecls = List.nil();
        for (JCTree tree : classDecl.defs) {
            if (tree instanceof JCTree.JCVariableDecl) {
                // 创建 getter 方法
                JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl) tree;
                String methodGetterName = Utils.toGetterName(fieldDecl);
                if (!Utils.methodExists(methodGetterName, classDecl)) {
                    JCTree.JCMethodDecl methodGetter = this.createGetter(fieldDecl);
                    methodDecls = methodDecls.append(methodGetter);
                    if (verbose) {
                        messager.printMessage(NOTE, "createGetter: " + methodGetter, fieldDecl.sym);
                    }
                } else {
                    if (verbose) {
                        messager.printMessage(NOTE, "methodExists: " + methodGetterName, fieldDecl.sym);
                    }
                }
            }
        }
        classDecl.defs = classDecl.defs.appendList(methodDecls);
    }

    private void handleGetter(JCTree.JCClassDecl classDecl, JCTree.JCVariableDecl fieldDecl) {
        String methodGetterName = Utils.toGetterName(fieldDecl);
        if (!Utils.methodExists(methodGetterName, classDecl)) {
            JCTree.JCMethodDecl methodGetter = this.createGetter(fieldDecl);
            classDecl.defs = classDecl.defs.append(methodGetter);
            if (verbose) {
                messager.printMessage(NOTE, "createGetter: " + methodGetter, fieldDecl.sym);
            }
        } else {
            if (verbose) {
                messager.printMessage(NOTE, "methodExists: " + methodGetterName, fieldDecl.sym);
            }
        }
    }

    private void handleSetter(JCTree.JCClassDecl classDecl) {
        List<JCTree> methodDecls = List.nil();
        for (JCTree tree : classDecl.defs) {
            if (tree instanceof JCTree.JCVariableDecl) {
                // 创建 setter 方法
                JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl) tree;
                String methodSetterName = Utils.toSetterName(fieldDecl);
                if (!Utils.methodExists(methodSetterName, classDecl)) {
                    JCTree.JCMethodDecl methodSetter = this.createSetter(fieldDecl);
                    methodDecls = methodDecls.append(methodSetter);
                    if (verbose) {
                        messager.printMessage(NOTE, "createSetter: " + methodSetter, fieldDecl.sym);
                    }
                } else {
                    if (verbose) {
                        messager.printMessage(NOTE, "methodExists: " + methodSetterName, fieldDecl.sym);
                    }
                }
            }
        }
        classDecl.defs = classDecl.defs.appendList(methodDecls);
    }

    private void handleSetter(JCTree.JCClassDecl classDecl, JCTree.JCVariableDecl fieldDecl) {
        String methodSetterName = Utils.toSetterName(fieldDecl);
        if (!Utils.methodExists(methodSetterName, classDecl)) {
            JCTree.JCMethodDecl methodSetter = this.createSetter(fieldDecl);
            classDecl.defs = classDecl.defs.append(methodSetter);
            if (verbose) {
                messager.printMessage(NOTE, "createSetter: " + methodSetterName, fieldDecl.sym);
            }
        } else {
            if (verbose) {
                messager.printMessage(NOTE, "methodExists: " + methodSetterName, fieldDecl.sym);
            }
        }
    }

    private void handleData(JCTree.JCClassDecl classDecl) {
        this.handleGetter(classDecl);
        this.handleSetter(classDecl);
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

        if (verbose) {
            messager.printMessage(NOTE, "log field: " + fieldDecl, classDecl.sym);
        }
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
