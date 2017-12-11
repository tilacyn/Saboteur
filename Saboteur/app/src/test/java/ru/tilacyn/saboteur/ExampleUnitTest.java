package ru.tilacyn.saboteur;

import org.junit.Test;
import org.junit.runner.manipulation.Filter;

import ru.iisuslik.controller.Controller;
import ru.iisuslik.field.Field;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        Controller c = new Controller();
        Field field = new Field(3, c);
    }
}