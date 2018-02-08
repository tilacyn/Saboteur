package ru.iisuslik.cards;

import android.util.Log;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Tunnel extends Card {

    public boolean up, down, left, right, centre;
    boolean closedTunnel = false;

    public Tunnel(String name, String description, Field field, int playerNumber,
                  boolean up, boolean down, boolean left, boolean right, boolean centre) {
        super(0, name, description, field, playerNumber);
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
        } else {
            this.id += 16;
            if (left) this.id++;
            if (down) this.id += 2;
            if (up) this.id += 4;
            if (right) this.id += 8;
        }
    }

    public boolean canPlay(int i, int j) {
        Log.d("AAAAAAAA", "play tunnel " + "did play card " +
                field.didCurrentPlayerPlayCard() + " isMyTurn " + field.controller.isMyTurn());
        return !field.didCurrentPlayerPlayCard() && field.players[ownerPlayerNumber].canPutTunnels()
                && field.canPutTunnel(this, i, j) && field.controller.isMyTurn();
    }

    public void play(int i, int j) {
        field.currentTD = new TurnData(CARD_TYPE.TUNNEL, ownerPlayerNumber,
                field.players[ownerPlayerNumber].getCardNumber(this),
                i, j, -1);

        field.putTunnel(this, i, j);
        Log.d("TTTTTTTTT", "put tunnel player " + ownerPlayerNumber + " field i j empty " + (field.field[i][j] == null));
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();
        field.startDfs();
        Log.d("TTTTTTTTT", "finish play tunnel " + field.didCurrentPlayerPlayCard());
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
        } else {
            this.id += 16;
            if (left) this.id++;
            if (down) this.id += 2;
            if (up) this.id += 4;
            if (right) this.id += 8;
        }
        if (!this.isClosedTunnel())
            field.spins[field.players[ownerPlayerNumber].getCardNumber(this)] ^= true;
    }

    public boolean isClosedTunnel() {
        return closedTunnel;
    }
}
