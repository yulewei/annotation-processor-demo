package com.example;

/**
 * @author yulewei
 */
//@Data
@Builder
public class Person {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int age;

    public int getAge() {
        return this.age;
    }
}
