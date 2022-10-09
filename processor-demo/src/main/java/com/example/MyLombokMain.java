package com.example;

/**
 * @author yulewei
 */
@Slf4j
public class MyLombokMain {

    public static void main(String[] args) {
        Person person = new Person();
        person.setName("Bill Gates");
        person.setAge(42);
        log.info("annotation processor worked!");
    }
}
