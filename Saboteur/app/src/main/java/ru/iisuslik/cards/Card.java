package ru.iisuslik.cards;

import java.io.Serializable;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Card implements Serializable {

    public static final int TUNNEL = 1;
    public static final int ACTION = 2;

    public static final int NO_PLAYER = -1;

    protected int ownerPlayerNumber;
    protected Field field;
    protected int type;
    protected int id;
    protected String name;
    protected String description;

    public void setPlayerNumber(int playerNumber) {
        this.ownerPlayerNumber = playerNumber;
    }

    public Card(int type, int id, String name, String description, Field field, int ownerPlayerNumber) {
        this.type = type;
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

    public int getType() {
        return type;
    }

    public boolean canDiscard() {
        return !field.didCurrentPlayerPlayCard();
    }

    public void discard() {
        discard(true);
    }


    public void discard(boolean needToSend) {
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();

        if (needToSend) {
            field.currentTD = new TurnData(ownerPlayerNumber,
                    field.players[ownerPlayerNumber].getCardNumber(this),
                    -1, -1, -1, this) {
                @Override
                public void apply(Card card) {
                    card.discard(false);
                }
            };
        }
    }


    protected void printSmth() {
        System.out.print("%%%   ");
    }

    public void printFirst() {
        printSmth();
    }

    public void printSecond() {
        printSmth();
    }

    public void printThird() {
        printSmth();
    }

}


