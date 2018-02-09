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

    public Tunnel(JSONArray tunnel, Field field) throws JSONException {
        super(0, field, -1);
        this.up = tunnel.getInt(1) == 1;
        this.down = tunnel.getInt(2) == 1;
        this.left = tunnel.getInt(3) == 1;
        this.right = tunnel.getInt(4) == 1;
        this.centre = tunnel.getInt(5) == 1;
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
        if (!this.isClosedTunnel())
            field.spins[field.players[ownerPlayerNumber].getCardNumber(this)] ^= true;
    }

    public boolean isClosedTunnel() {
        return closedTunnel;
    }
}
