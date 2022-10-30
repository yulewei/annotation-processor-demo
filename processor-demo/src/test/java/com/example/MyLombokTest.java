package com.example;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author yulewei
 */
@Slf4j
public class MyLombokTest {

    @Test
    public void testMyLombok() {
        Person person = new Person();
        person.setName("Bill");
        person.setAge(42);
        assertNotNull(person);
        assertEquals(person.getName(), "Bill");
        assertEquals(person.getAge(), 42);
        log.info("person: {}, {}", person.getName(), person.getAge());
    }
}
