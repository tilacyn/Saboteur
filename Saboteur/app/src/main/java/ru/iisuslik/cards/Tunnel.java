package ru.iisuslik.cards;

import android.util.Log;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

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
        field.putTunnel(this, i, j);
        Log.d("AAAAAAAA", "put tunnel player " + ownerPlayerNumber + ' ' + (field.field[i][j] == null));
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();
        field.startDfs();
        field.currentTD = new TurnData(ownerPlayerNumber,
                field.players[ownerPlayerNumber].getCardNumber(this),
                i, j, -1, this) {
            @Override
            public void apply(Card card) {
                ((Tunnel) card).play(i, j);
                card.field.startNextTurn(false);
            }
        };
        Log.d("AAAAAAAA", "finish play tunnel " + field.didCurrentPlayerPlayCard());
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
