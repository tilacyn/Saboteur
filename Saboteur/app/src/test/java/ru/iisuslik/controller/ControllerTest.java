package ru.iisuslik.controller;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

import ru.iisuslik.gameData.GameData;
import ru.iisuslik.gameData.Shuffle;


public class ControllerTest {
    @Test
    public void serialize() throws Exception {
        Controller c = new Controller();
        GameData gd = new GameData();
        gd.shuffle = new Shuffle(2, 70, 1);
        byte [] buf = new byte[2];
    }

}