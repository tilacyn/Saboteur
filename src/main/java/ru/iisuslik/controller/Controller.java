package ru.iisuslik.controller;

import ru.iisuslik.cards.Card;
import ru.iisuslik.cards.Tunnel;
import ru.iisuslik.field.Field;

import java.util.ArrayList;

public class Controller {
    private Field field;
    public void initializeField(int playerCount) {
        field = new Field(playerCount);
    }

    public static final int ENTRY_POS_I = Field.ENTRY_POS_I;
    public static final int ENTRY_POS_J = Field.ENTRY_POS_J;

    public ArrayList<Card> getCurrentPlayerHand() {
        return field.getCurrentPlayerHand();
    }
    public boolean[] getCurrentPlayerDebuffs() {
        return field.getCurrentPlayerDebuffs();//Lamp Pick Trolley
    }
    public void startNextTurn() {
        field.startNextTurn();
    }
    public Tunnel[][] getField() {
        return field.field;
    }

    public boolean canStartNextTurn() {
        return field.didCurrentPlayerPlayCard();
    }

    public boolean isCurrentPlayerSaboteur(){
        return field.isCurrentPlayerSaboteur();
    }



    public void printField() {
        field.print();
    }
}
