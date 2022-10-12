package com.example.visit;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementScanner7;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("*")
public class VisitProcessor extends AbstractProcessor {
    private MyScanner scanner;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.scanner = new MyScanner();
    }

    @Override
    public boolean process(Set<? extends TypeElement> types, RoundEnvironment environment) {
        if (!environment.processingOver()) {
            for (Element element : environment.getRootElements()) {
                scanner.scan(element);
            }
        }
        return true;
    }

    public static class MyScanner extends ElementScanner7<Void, Void> {

        public Void visitType(TypeElement element, Void p) {
            System.out.println("类 " + element.getKind() + ": " + element.getSimpleName());
            return super.visitType(element, p);
        }

        public Void visitExecutable(ExecutableElement element, Void p) {
            System.out.println("方法 " + element.getKind() + ": " + element.getSimpleName());
            return super.visitExecutable(element, p);
        }

        public Void visitVariable(VariableElement element, Void p) {
            if (element.getEnclosingElement().getKind() == ElementKind.CLASS) {
                System.out.println("字段 " + element.getKind() + ": " + element.getSimpleName());
            }
            return super.visitVariable(element, p);
        }
    }
}
