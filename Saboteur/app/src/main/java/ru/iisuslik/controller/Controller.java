package ru.iisuslik.controller;

import ru.iisuslik.cards.Card;
import ru.iisuslik.cards.Debuff;
import ru.iisuslik.cards.Tunnel;
import ru.iisuslik.field.Field;
import ru.iisuslik.field.Shuffle;
import ru.iisuslik.gameData.GameData;
import ru.iisuslik.multiplayer.MultiPlayer;

import java.io.*;
import java.util.ArrayList;

public class Controller implements Serializable {
    private Field field;
    private MultiPlayer multiPlayer = null;

    public ArrayList<GameData> log = new ArrayList<>();

    public void initializeField(int playerCount) {
        initializeField(playerCount, null);
    }

    public void initializeField(int playerCount, Shuffle shuffle) {
        field = new Field(playerCount, this, shuffle);
    }

    public void initializeField(Shuffle shuffle) {
        initializeField(shuffle.whoAreSaboteur.length, shuffle);
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

    public boolean isCurrentPlayerSaboteur() {
        return field.isCurrentPlayerSaboteur();
    }

    public boolean isThisTheEnd() {
        return field.isThisTheEnd();
    }

    public int currentPlayerNumber() {
        return field.getCurrentPlayer();
    }


    public void printField() {
        field.print();
    }


    public void sendData(GameData gameData) {
        if (isSinglePlayer() || gameData == null)
            return;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        gameData.serialize(out);
        byte[] data = out.toByteArray();
        multiPlayer.sendData(data);
    }

    public boolean isSinglePlayer() {
        return multiPlayer == null;
    }

    public void applyGameData(GameData gameData) {
        field.applyGameData(gameData);
    }

    public void serialize(OutputStream out) {
        try {
            ObjectOutputStream objectOut = new ObjectOutputStream(out);
            objectOut.writeObject(this);
            objectOut.flush();
            objectOut.close();
        } catch (Exception ignored) {
        }
    }

    public static Controller deserialize(InputStream in) {
        Controller res = null;
        try {
            ObjectInputStream objectIn = new ObjectInputStream(in);
            res = (Controller) objectIn.readObject();
            objectIn.close();
        } catch (Exception ignored) {
        }
        return res;
    }
}
