package com.example;

/**
 * @author yulewei
 */
@Data
public class Person {

    @Getter
    @Setter
    private String name;

    private int age;

    public int getAge() {
        return this.age;
    }
}
