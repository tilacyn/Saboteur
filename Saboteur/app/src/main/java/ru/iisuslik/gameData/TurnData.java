package ru.iisuslik.gameData;

import java.io.*;

import ru.iisuslik.cards.Card;

public abstract class TurnData implements Serializable {
    public abstract void apply(Card card);

    public int ownerPlayerNumber;
    public int targetPlayerNumber;
    public int cardNumber;
    public int i, j;
    public boolean [] spins = new boolean[6];
    public Card card;
    public boolean isDiscard;

    public TurnData(int ownerPlayerNumber, int cardNumber, int i, int j, int targetPlayerNumber, boolean isDiscard, Card card) {
        this.ownerPlayerNumber = ownerPlayerNumber;
        this.targetPlayerNumber = targetPlayerNumber;
        this.cardNumber = cardNumber;
        this.i = i;
        this.j = j;
        this.card = card;
        this.isDiscard = isDiscard;
    }

    public TurnData(int ownerPlayerNumber, int cardNumber, int i, int j, int targetPlayerNumber, Card card) {
        this.ownerPlayerNumber = ownerPlayerNumber;
        this.targetPlayerNumber = targetPlayerNumber;
        this.cardNumber = cardNumber;
        this.i = i;
        this.j = j;
        this.card = card;
        this.isDiscard = false;
    }


}
