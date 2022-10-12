package com.example.filer;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@code @Builder} 注解处理器。使用 javapoet 库生成 XxxBuilder 类
 *
 * @author yulewei
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.example.filer.Builder")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BuilderProcessor extends AbstractProcessor {
    private static final Logger log = LoggerFactory.getLogger(BuilderProcessor.class);

    private Filer filer;
    private Elements elements;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Builder.class)) {
            if (!(element instanceof TypeElement)) {
                continue;
            }

            log.debug("@Builder, process class: {}", element.getSimpleName().toString());

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
                        .addStatement(String.format("obj.%s = %s;", field, field))
                        .addStatement("return this")
                        .build();
            }).collect(Collectors.toList());
            TypeSpec.Builder builderClass = TypeSpec.classBuilder(builderClassName)
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
        return false;
    }
}
