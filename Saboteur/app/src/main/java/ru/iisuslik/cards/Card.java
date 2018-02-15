package ru.iisuslik.cards;

import android.util.Log;

import java.io.Serializable;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Card implements Serializable {
    public enum CARD_TYPE {TUNNEL, HEAL, DESTROY, WATCH, DEBUFF, UNKNOWN}

    int ownerPlayerNumber;
    protected Field field;
    protected int id;

    public void setPlayerNumber(int playerNumber) {
        this.ownerPlayerNumber = playerNumber;
    }

    public Card(int id, Field field, int ownerPlayerNumber) {
        this.id = id;
        this.field = field;
        this.ownerPlayerNumber = ownerPlayerNumber;
    }

    public int getId() {
        return id;
    }

    public boolean canDiscard() {
        return !field.didCurrentPlayerPlayCard() && field.controller.isMyTurn();
    }


    public void discard() {
        field.currentTD = new TurnData(CARD_TYPE.UNKNOWN, ownerPlayerNumber,
                field.players[ownerPlayerNumber].getCardNumber(this),
                -1, -1, -1);
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();
    }

}


