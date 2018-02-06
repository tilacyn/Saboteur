package ru.iisuslik.cards;

import java.io.Serializable;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Card implements Serializable {
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
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();

        field.currentTD = new TurnData(ownerPlayerNumber,
                field.players[ownerPlayerNumber].getCardNumber(this),
                -1, -1, -1, true, this) {
            @Override
            public void apply(Card card) {
                card.discard();
                card.field.startNextTurn(false);
            }
        };

    }

}


