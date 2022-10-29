package com.example.filer;

import org.apache.commons.io.IOUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Set;

import static com.example.filer.GreetingProcessor.CLASSNAME;

/**
 * @author yulewei
 */
@SupportedAnnotationTypes("*")
@SupportedOptions({CLASSNAME})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class GreetingProcessor extends AbstractProcessor {
    protected static final String CLASSNAME = "greeting.className";

    private Filer filer;
    private Messager messager;
    private boolean generated = false;
    private String className = "Greeting";

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
        String optionClassName = processingEnv.getOptions().get(CLASSNAME);
        if (optionClassName != null) {
            this.className = optionClassName;
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (generated) {
            System.out.printf("'%s' class，is generated\n", className);
            return false;
        }
        try {
            generated = generateGreeting();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        System.out.printf("'%s' class，generating is done\n", className);
        return false;
    }

    private boolean generateGreeting() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("Greeting.tpl");
        String greetingTpl = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        String greetingSource = String.format(greetingTpl, LocalDateTime.now(), className);
        JavaFileObject fileObject = filer.createSourceFile(className);
        try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
            writer.println(greetingSource);
        }
        return true;
    }

}
