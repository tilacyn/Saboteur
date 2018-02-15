package ru.iisuslik.controller;


import android.util.Log;
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
    private static final String TAG = "C";
    public Field field;
    public MultiPlayer multiPlayer = null;
    public GameData gameData = new GameData();

    public void initializeField(int playerCount) {
        if (isSinglePlayer())
            initializeField(playerCount, null);
        initializeField(playerCount, gameData.shuffle);
    }

    private void initializeField(int playerCount, Shuffle shuffle) {
        field = new Field(playerCount, this, shuffle);
    }

    public void initializeMultiplayer() {
        multiPlayer = new MultiPlayer(this);
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

    public static final int DECK_SIZE = 70;


    public int getWidth() {
        return Field.WIDTH;
    }

    public int getHeight() {
        return Field.HEIGHT;
    }

    public ArrayList<Card> getCurrentPlayerHand() {
        update();
        return field.getCurrentPlayerHand();
    }


    public boolean[] getPlayerDebuffs(int index) {
        update();
        return field.getPlayerDebuffs(index);//Lamp Pick Trolley
    }


    public void startNextTurn() {
        field.startNextTurn();
    }

    public Tunnel[][] getField() {
        return field.field;
    }

    public boolean canStartNextTurn() {
        update();
        boolean single = field.didCurrentPlayerPlayCard();
        if (isSinglePlayer())
            return single;
        return single && isMyTurn();
    }

    public boolean isMyTurn() {
        update();
        return isSinglePlayer() || multiPlayer.isMyTurn();
    }

    public boolean isCurrentPlayerSaboteur() {
        update();
        return field.isCurrentPlayerSaboteur();
    }

    public boolean isThisTheEnd() {
        update();
        return field.isThisTheEnd();
    }

    public int currentPlayerNumber() {
        update();
        return field.getCurrentPlayer();
    }


    public void takeTurn(TurnData turn, boolean needToSend) {
        gameData.turns.add(turn);
        if (needToSend)
            sendData(gameData);
    }

    private void update() {
        if (!isSinglePlayer()) {
            multiPlayer.update();
        }
    }

    public void sendData(GameData gameData) {
        if (isSinglePlayer() || gameData == null)
            return;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            gameData.serialize(out);
        } catch (IOException e) {
            Log.d(TAG, "sendData(), problems with serialize");
            e.printStackTrace();
        }
        byte[] data = out.toByteArray();
        multiPlayer.sendData(data);
    }

    public boolean isSinglePlayer() {
        return multiPlayer == null;
    }

    private void applyGameData(GameData gameData) {
        if (field == null) {
            initializeField(gameData.shuffle);
            this.gameData = new GameData();
        }
        Log.d(TAG, "applyGameData()");
        for (int i = this.gameData.turns.size(); i < gameData.turns.size(); i++) {
            Log.d(TAG, "applyGameData() apply new turn №" + i + 1);
            field.applyTurnData(gameData.turns.get(i));
        }
        if(this.gameData.shuffle == null) {
            this.gameData.shuffle = gameData.shuffle;
        }
    }

    public void applyData(byte[] data) {
        if (data == null) {
            Log.d(TAG, "applyData(), data = null");
            multiPlayer.onInitiateMatch(multiPlayer.curMatch);
            return;
        }
        Log.d(TAG, "applyData(), data != null");
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            GameData gd = GameData.deserialize(in);
            applyGameData(gd);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "applyData(), problems with deserialize");
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
        try {
            ObjectInputStream objectIn = new ObjectInputStream(in);
            Controller res = (Controller) objectIn.readObject();
            objectIn.close();
            return res;
        } catch (Exception ignored) {
            return null;
        }
    }
}
