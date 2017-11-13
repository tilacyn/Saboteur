package ru.iisuslik.cards;

import ru.iisuslik.field.Field;

public class ClosedTunnel extends Tunnel {

    private boolean closed = true;
    private boolean gold;

    public ClosedTunnel(String name, String description, Field field, int playerNumber,
                        boolean up, boolean down, boolean left, boolean right, boolean centre,
                        boolean gold) {
        super(name, description, field, playerNumber, up, down, left, right, centre);
        this.gold = gold;
        this.closed = true;
        closedTunnel = true;
    }

    public void open(){
        closed = false;
    }

    public boolean isGold() {
        return gold;
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void printFirst() {
        printSmth();
    }//TODO if !closed

    @Override
    public void printSecond() {
        System.out.print('%');
        System.out.print(isGold() ? '1' : '0');
        System.out.print("%   ");
    }

    @Override
    public void printThird() {
        printSmth();
    }
}