package ru.iisuslik.cards;

import ru.iisuslik.field.Field;

public class Tunnel extends Card {

    public boolean up, down, left, right, centre;
    protected boolean closedTunnel = false;

    public Tunnel(String name, String description, Field field, int playerNumber,
                  boolean up, boolean down, boolean left, boolean right, boolean centre) {
        super(TUNNEL, 0, name, description, field, playerNumber);
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
        this.centre = centre;
        if (centre) {
            this.id++;
            if (left) this.id++;
            if (down) this.id += 2;
            if (up) this.id += 4;
            if (right) this.id += 8;
        }
    }

    public boolean canPlay(int i, int j) {
        return !field.didCurrentPlayerPlayCard() && field.players[playerNumber].canPutTunnels() && field.canPutTunnel(this, i, j);
    }

    public void play(int i, int j) {
        field.putTunnel(this, i, j);
        field.players[playerNumber].playCard(this);
        field.iPlayedCard();
        field.startDfs();
    }

    public void spin() {
        boolean temp = up;
        up = down;
        down = temp;
        temp = right;
        right = left;
        left = temp;
        this.id = 0;
        if (centre) {
            this.id++;
            if (left) this.id++;
            if (down) this.id += 2;
            if (up) this.id += 4;
            if (right) this.id += 8;
        }
    }

    public boolean isClosedTunnel() {
        return closedTunnel;
    }

    private void printBoolean(boolean b) {
        if (b) System.out.print('+');
        else System.out.print(' ');
    }

    @Override
    public void printFirst() {
        System.out.print(' ');
        printBoolean(up);
        System.out.print("   ");
    }

    @Override
    public void printSecond() {
        printBoolean(left);
        if (centre) System.out.print('@');
        else System.out.print(' ');
        printBoolean(right);
        System.out.print("  ");
    }

    @Override
    public void printThird() {
        System.out.print(' ');
        printBoolean(down);
        System.out.print("   ");
    }
}
