package ru.iisuslik.controller;

import ru.iisuslik.cards.Card;
import ru.iisuslik.cards.Tunnel;
import ru.iisuslik.field.Field;

import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Controller implements Serializable {
    private Field field;
    public void initializeField(int playerCount) {
        field = new Field(playerCount);
    }

    public static final int ENTRY_POS_I = Field.ENTRY_POS_I;
    public static final int ENTRY_POS_J = Field.ENTRY_POS_J;


    public int getWidth() {
        return field.WIDTH;
    }

    public int getHeight() {
        return field.HEIGHT;
    }
    public ArrayList<Card> getCurrentPlayerHand() {
        return field.getCurrentPlayerHand();
    }
    public boolean[] getCurrentPlayerDebuffs() {
        return field.getCurrentPlayerDebuffs();//Lamp Pick Trolley
    }
    public boolean[] getPlayerDebuffs(int index) {
        return field.getPlayerDebuffs(index);//Lamp Pick Trolley
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
    public boolean isThisTheEnd(){
        return field.isThisTheEnd();
    }
    public int currentPlayerNumber(){
        return field.getCurrentPlayer();
    }


    public void printField() {
        field.print();
    }

    public void serialize(OutputStream out) {
        try {
            ObjectOutputStream objectOut = new ObjectOutputStream(out);
            objectOut.writeObject(this);
            objectOut.flush();
            objectOut.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public static Controller deserialize(InputStream in) {
       Controller res = null;
        try {
            ObjectInputStream objectIn = new ObjectInputStream(in);
            res = (Controller) objectIn.readObject();
            objectIn.close();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return res;
    }


}
