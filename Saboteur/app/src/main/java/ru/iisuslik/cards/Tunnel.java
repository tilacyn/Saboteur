package ru.iisuslik.cards;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Tunnel extends Card {

    public boolean up, down, left, right, centre;
    boolean closedTunnel = false;
    private static final String TAG = "TTTTTTTTT";

    public Tunnel(int[] tunnel, Field field) {
        super(0, field, -1);
        up = tunnel[1] == 1;
        down = tunnel[2] == 1;
        left = tunnel[3] == 1;
        right = tunnel[4] == 1;
        centre = tunnel[5] == 1;
        calculateId();
    }

    private void calculateId() {
        this.id = 0;
        if (centre) {
            this.id++;
        } else {
            this.id += 16;
        }
        if (left) this.id++;
        if (down) this.id += 2;
        if (up) this.id += 4;
        if (right) this.id += 8;
    }

    public boolean canPlay(int i, int j) {
        Log.d(TAG, "play tunnel " + "did play card " +
                field.didCurrentPlayerPlayCard() + " isMyTurn " + field.controller.isMyTurn());
        return !field.didCurrentPlayerPlayCard() && field.players[ownerPlayerNumber].canPutTunnels()
                && field.canPutTunnel(this, i, j) && field.controller.isMyTurn();
    }

    public void play(int i, int j) {
        field.currentTD = new TurnData(CARD_TYPE.TUNNEL, ownerPlayerNumber,
                field.players[ownerPlayerNumber].getCardNumber(this),
                i, j, -1);

        field.putTunnel(this, i, j);
        Log.d(TAG, "put tunnel player " + ownerPlayerNumber + " field i j empty " + (field.field[i][j] == null));
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();
        field.startDfs();
        Log.d(TAG, "finish play tunnel " + field.didCurrentPlayerPlayCard());
    }


    public void spin() {
        boolean temp = up;
        up = down;
        down = temp;
        temp = right;
        right = left;
        left = temp;
        calculateId();
        if (!this.isClosedTunnel())
            field.spins[field.players[ownerPlayerNumber].getCardNumber(this)] ^= true;
    }


    public boolean isClosedTunnel() {
        return closedTunnel;
    }
}
