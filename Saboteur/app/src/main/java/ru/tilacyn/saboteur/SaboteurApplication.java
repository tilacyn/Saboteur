package ru.tilacyn.saboteur;

import ru.iisuslik.controller.Controller;

/**
 * Created by tilac on 19.11.2017.
 */

public class SaboteurApplication {
    private static SaboteurApplication example;
    private Controller controller;

    private SaboteurApplication(){
        controller = new Controller();
    }

    public static SaboteurApplication getInstance(){
        if (null == example){
            example = new SaboteurApplication();
        }
        return example;
    }

    public Controller getController() {
        return controller;
    }
}