package com.example;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author yulewei
 */
public class BuilderProcessorTest {

    @Test
    public void testBuilder() {
        Dog dog = new DogBuilder().name("旺财").age(3).build();
        assertNotNull(dog);
        assertEquals(dog.name, "旺财");
        assertEquals(dog.age, 3);
    }

}
