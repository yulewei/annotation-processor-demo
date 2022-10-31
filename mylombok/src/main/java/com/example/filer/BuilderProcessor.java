package com.example.filer;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * {@code @Builder} 注解处理器。基于 JavaPoet 库生成 XxxBuilder 类
 *
 * @author yulewei
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.example.filer.Builder")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BuilderProcessor extends AbstractProcessor {

    private Filer filer;
    private Elements elements;
    private Messager messager;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
        this.elements = processingEnv.getElementUtils();
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(Builder.class)) {
            if (!(element instanceof TypeElement)) {
                continue;
            }

            messager.printMessage(NOTE, "@Builder, process class: " + element.getSimpleName(), element);

            TypeElement classElement = (TypeElement) element;
            List<VariableElement> fieldElementList = new ArrayList<>();
            for (Element enclosedElement : classElement.getEnclosedElements()) {
                if (enclosedElement instanceof VariableElement) {
                    VariableElement variableElement = (VariableElement) enclosedElement;
                    fieldElementList.add(variableElement);
                }
            }

            String packageName = elements.getPackageOf(classElement).getQualifiedName().toString();
            ClassName className = ClassName.get(packageName, classElement.getSimpleName().toString());
            ClassName builderClassName = ClassName.get(packageName, classElement.getSimpleName() + "Builder");

            FieldSpec fieldSpec = FieldSpec.builder(className, "obj", Modifier.PRIVATE).initializer("new $T()", className).build();
            List<MethodSpec> list = fieldElementList.stream().map(field -> {
                return MethodSpec.methodBuilder(field.getSimpleName().toString())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(builderClassName)
                        .addParameter(TypeName.get(field.asType()), field.getSimpleName().toString())
                        .addStatement("obj.$L = $L", field, field)
                        .addStatement("return this")
                        .build();
            }).collect(Collectors.toList());

            AnnotationSpec annotationSpec = AnnotationSpec.builder(Generated.class)
                    .addMember("value", "$S", "by BuilderProcessor")
                    .addMember("date", "$S", LocalDateTime.now())
                    .build();

            TypeSpec.Builder builderClass = TypeSpec.classBuilder(builderClassName)
                    .addAnnotation(annotationSpec)
                    .addModifiers(Modifier.PUBLIC)
                    .addField(fieldSpec)
                    .addMethods(list)
                    .addMethod(MethodSpec.methodBuilder("build")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(className)
                            .addStatement("return obj")
                            .build());
            try {
                JavaFile.builder(packageName, builderClass.build()).build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
