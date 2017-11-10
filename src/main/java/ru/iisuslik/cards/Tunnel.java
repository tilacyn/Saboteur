package ru.iisuslik.cards;

import ru.iisuslik.field.Field;

public class Tunnel extends Card {

    public boolean up, down, left, right, centre;
    protected boolean closedTunnel = false;

    public Tunnel(int id, String name, String description, Field field, int playerNumber,
                  boolean up, boolean down, boolean left, boolean right, boolean centre) {
        super(TUNNEL, id, name, description, field, playerNumber);
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
        this.centre = centre;
    }

    public boolean canPlay(int i, int j) {
        return field.players[playerNumber].canPutTunnels() && field.canPutTunnel(this,i,j);
    }

    public void play(int i, int j) {
        field.putTunnel(this, i, j);
        field.players[playerNumber].playCard(this);
        field.startNextTurn();
    }

    public void spin(){ //TODO
        boolean temp = up;
        up = down;
        down = temp;
        temp = right;
        right = left;
        left = temp;
    }

    public boolean isClosedTunnel() {
        return closedTunnel;
    }
}
