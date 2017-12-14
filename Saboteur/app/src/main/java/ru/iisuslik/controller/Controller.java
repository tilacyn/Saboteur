package ru.iisuslik.controller;


import ru.iisuslik.cards.Card;
import ru.iisuslik.cards.Tunnel;
import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.GameData;
import ru.iisuslik.gameData.Shuffle;
import ru.iisuslik.gameData.TurnData;
import ru.iisuslik.multiplayer.MultiPlayer;

import java.io.*;
import java.util.ArrayList;

public class Controller implements Serializable {
    private Field field;
    public MultiPlayer multiPlayer = null;

    public GameData gameData = new GameData();

    public void initializeField(int playerCount) {
        if (isSinglePlayer())
            initializeField(playerCount, null);
        initializeField(playerCount, gameData.shuffle);
    }

    public void initializeField(int playerCount, Shuffle shuffle) {
        field = new Field(playerCount, this, shuffle);
    }

    public void initializeMultiplayer() {
        multiPlayer = new MultiPlayer();
    }

    public void initializeField(Shuffle shuffle) {
        initializeField(shuffle.whoAreSaboteur.length, shuffle);
    }

    public boolean isFieldInitialized() {
        return field != null;
    }

    public int getPlayersNumber() {
        return field.players.length;
    }

    public static final int ENTRY_POS_I = Field.ENTRY_POS_I;
    public static final int ENTRY_POS_J = Field.ENTRY_POS_J;
    public static final int DECK_SIZE = 70;


    public int getWidth() {
        return field.WIDTH;
    }

    public int getHeight() {
        return field.HEIGHT;
    }

    public ArrayList<Card> getCurrentPlayerHand() {
        return field.getCurrentPlayerHand();
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


    public void takeTurn(TurnData turn, boolean needToSend) {
        gameData.turns.add(turn);
        if(needToSend)
            sendData(gameData);
    }

    public void update() {
        if (!isSinglePlayer())
            multiPlayer.updateMatch(multiPlayer.curMatch);
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
        if (field == null) {
            initializeField(gameData.shuffle);
        }
        for (int i = this.gameData.turns.size(); i < gameData.turns.size(); i++) {
            field.applyTurnData(gameData.turns.get(i));
        }
        this.gameData = gameData;

    }

    public void applyData(byte[] data) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            applyGameData(GameData.deserialize(in));
        } catch (Exception ignored) {
        }
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
