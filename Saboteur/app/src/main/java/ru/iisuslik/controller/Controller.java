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
import java.util.Arrays;

public class Controller implements Serializable {
    private static final String TAG = "CCCCCCCC";
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
        //update();
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
        //update();
        field.startNextTurn();
    }

    public Tunnel[][] getField() {
        //update();
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

    public void update() {
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
            Log.d(TAG, "sendData() problem with serialize " + e.getMessage());
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
        Log.d(TAG, "applyGameData(), how many turns " + gameData.turns.size() + ", shuffle null? " + (gameData.shuffle == null));
        Log.d(TAG, "applyGameData() current player " + field.getCurrentPlayer() + " me " + multiPlayer.getMyNumber());
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
            Log.d(TAG, "applyData() data null");
            multiPlayer.onInitiateMatch(multiPlayer.curMatch);
            return;
        }
        Log.d(TAG, "applyData() byte[] data = " + Arrays.toString(data));
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            //Log.d(TAG, "applyData() null" + (data == null));
            GameData gd = GameData.deserialize(in);
            Log.d(TAG, "applyData() null? " + (gd == null));
            applyGameData(gd);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "applyData() problem with deserialize " + e.getMessage());
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
