package ru.iisuslik.cards;

import ru.iisuslik.field.Field;

public class Action extends Card {


    public static final int HEAL = 1;
    public static final int DEBUFF = 2;
    public static final int DESTROY = 3;
    public static final int WATCH = 4;

    protected int actionType;

    public Action(int id, String name, String description, Field field, int playerNumber,
                  int actionType) {
        super(ACTION, id, name, description, field, playerNumber);
        this.actionType = actionType;
    }

    @Override
    public void printFirst() {
        printSmth();
    }

    @Override
    public void printSecond() {
        System.out.print('%');
        System.out.print(name.charAt(0));
        System.out.print("%   ");
    }

    @Override
    public void printThird() {
        printSmth();
    }
}
