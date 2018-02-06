package ru.iisuslik.gameData;

import java.io.*;

import ru.iisuslik.cards.Card;

public class TurnData implements Serializable {
    public Card.CARD_TYPE type;
    public int ownerPlayerNumber;
    public int targetPlayerNumber;
    public int cardNumber;
    public int i, j;
    public boolean[] spins = new boolean[6];
    public boolean isDiscard;
    public boolean lamp, trolley, pick;

    public TurnData(Card.CARD_TYPE type, int ownerPlayerNumber, int cardNumber, int i, int j, int targetPlayerNumber, boolean isDiscard) {
        this(type, ownerPlayerNumber, cardNumber, i, j, targetPlayerNumber);
        this.isDiscard = isDiscard;
    }

    public TurnData(Card.CARD_TYPE type, int ownerPlayerNumber, int cardNumber, int i, int j, int targetPlayerNumber) {
        this.ownerPlayerNumber = ownerPlayerNumber;
        this.targetPlayerNumber = targetPlayerNumber;
        this.cardNumber = cardNumber;
        this.i = i;
        this.j = j;
        this.type = type;
    }

    public TurnData(Card.CARD_TYPE type, int ownerPlayerNumber, int cardNumber, int i, int j, int targetPlayerNumber,
                    boolean lamp, boolean trolley, boolean pick) {
        this(type, ownerPlayerNumber, cardNumber, i, j, targetPlayerNumber);
        this.lamp = lamp;
        this.pick = pick;
        this.trolley = trolley;
    }


}
