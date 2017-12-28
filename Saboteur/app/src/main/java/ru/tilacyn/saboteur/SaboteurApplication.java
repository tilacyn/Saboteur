package ru.tilacyn.saboteur;

import ru.iisuslik.controller.Controller;

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