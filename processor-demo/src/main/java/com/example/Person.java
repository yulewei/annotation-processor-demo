package com.example;

import com.example.Data;
import com.example.Builder;

/**
 * @author yulewei
 */
@Data
@Builder
public class Person {

    private String name;

    private int age;

    public String getName() {
        return this.name;
    }
}
