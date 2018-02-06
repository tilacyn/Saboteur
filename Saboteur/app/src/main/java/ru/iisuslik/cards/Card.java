package ru.iisuslik.cards;

import java.io.Serializable;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Card implements Serializable {
    public enum CARD_TYPE {TUNNEL, HEAL, DESTROY, WATCH, DEBUFF, UNKNOWN}

    public static final int NO_PLAYER = -1;

    protected int ownerPlayerNumber;
    protected Field field;
    protected int id;
    protected String name;
    protected String description;

    public void setPlayerNumber(int playerNumber) {
        this.ownerPlayerNumber = playerNumber;
    }

    public Card(int id, String name, String description, Field field, int ownerPlayerNumber) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.field = field;
        this.ownerPlayerNumber = ownerPlayerNumber;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean canDiscard() {
        return !field.didCurrentPlayerPlayCard() && field.controller.isMyTurn();
    }


    public void discard() {
        field.currentTD = new TurnData(CARD_TYPE.UNKNOWN, ownerPlayerNumber,
                field.players[ownerPlayerNumber].getCardNumber(this),
                -1, -1, -1, true);
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();
    }

}


