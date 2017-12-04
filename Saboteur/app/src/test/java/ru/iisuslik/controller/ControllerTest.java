package ru.iisuslik.controller;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

/**
 * Created by tilac on 04.12.2017.
 */
public class ControllerTest {
    @Test
    public void serialize() throws Exception {
        byte[] array = {1, 2, 3};
        System.out.println(new String(array));
        assertArrayEquals(array, (new String(array)).getBytes());

        Controller controller = new Controller();
        controller.initializeField(2);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        controller.serialize(byteArrayOutputStream);

        byte[] kek = byteArrayOutputStream.toByteArray();
        byte[] res = new byte[kek.length];

        String lol = "";

        for(int i = 0; i < kek.length; i++) {
            lol = lol + (char) kek[i];
        }


        for(int i = 0; i < kek.length; i++) {
            res[i] = (byte) lol.charAt(i);
        }

        assertArrayEquals(kek, res);

    }

}