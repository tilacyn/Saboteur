package ru.iisuslik.field;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import ru.iisuslik.cards.*;
import ru.iisuslik.controller.Controller;
import ru.iisuslik.gameData.Shuffle;
import ru.iisuslik.gameData.TurnData;
import ru.iisuslik.player.*;
import ru.tilacyn.saboteur.SaboteurApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Field implements Serializable {

    private static final String TAG = "FFFFFFF";
    private boolean currentPlayerPlayedCard = false;
    public TurnData currentTD;
    public boolean[] spins = new boolean[6];

    public boolean didCurrentPlayerPlayCard() {
        return currentPlayerPlayedCard;
    }

    public void iPlayedCard() {
        currentPlayerPlayedCard = true;
    }

    public static final int WIDTH = 15;
    public static final int HEIGHT = 18;

    public static final int ENTRY_POS_I = 12;
    public static final int ENTRY_POS_J = 8;

    private int playingCount;
    private int currentPlayer = 0;
    private boolean theEnd = false;
    public Controller controller;

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isThisTheEnd() {
        return theEnd;
    }

    public Tunnel[][] field = new Tunnel[HEIGHT][WIDTH];
    public Player[] players;
    private ArrayList<Card> deck = new ArrayList<>();


    public void applyTurnData(TurnData turnData) {
        int playerFrom = turnData.ownerPlayerNumber;
        int cardNumber = turnData.cardNumber;
        ArrayList<Card> hand = players[playerFrom].getHand();
        for (int i = 0; i < hand.size(); i++) {
            if (turnData.spins[i]) {
                ((Tunnel) hand.get(i)).spin();
            }
        }
        Log.d(TAG, "applyTurnData()");
        Card cardToPlay = hand.get(cardNumber);
        switch (turnData.type) {
            case UNKNOWN:
                cardToPlay.discard();
                break;
            case HEAL:
                ((Heal) cardToPlay).play(turnData.targetPlayerNumber);
                break;
            case DEBUFF:
                ((Debuff) cardToPlay).play(turnData.targetPlayerNumber);
                break;
            case WATCH:
                ((Watch) cardToPlay).play(turnData.i, turnData.j);
                break;
            case TUNNEL:
                ((Tunnel) cardToPlay).play(turnData.i, turnData.j);
                break;
            case DESTROY:
                ((Destroy) cardToPlay).play(turnData.i, turnData.j);
                break;
        }
        startNextTurn(false);
    }


    public ArrayList<Card> getCurrentPlayerHand() {
        if (controller.isSinglePlayer())
            return players[currentPlayer].getHand();
        return players[controller.multiPlayer.getMyNumber()].getHand();
    }

    public boolean[] getPlayerDebuffs(int index) {
        return new boolean[]{players[index].isBrokenLamp(),
                players[index].isBrokenPick(),
                players[index].isBrokenTrolley()};
    }

    public boolean isCurrentPlayerSaboteur() {
        if (!controller.isSinglePlayer())
            return players[controller.multiPlayer.getMyNumber()].getPersonality() == Player.SABOTEUR;
        return players[currentPlayer].getPersonality() == Player.SABOTEUR;
    }

    private void startNextTurn(boolean needToSend) {
        Log.d(TAG, "startNextTurn()" + "shuffle is null? " + (controller.gameData.shuffle == null));
        if (deck.size() != 0) {
            Card next = deck.remove(deck.size() - 1);
            next.setPlayerNumber(currentPlayer);
            players[currentPlayer].addCard(next);
            Log.d(TAG, "player" + currentPlayer + "take some cards, now we have " + players[currentPlayer].getHand().size());
            Log.d(TAG, "players hands sizes" + players[0].getHand().size() + players[1].getHand().size());
        }
        if (players[currentPlayer].getHand().size() == 0) {
            players[currentPlayer].concede();
            playingCount--;
        }
        if (playingCount == 0) {
            finish();
        } else {
            currentPlayer++;
            currentPlayer %= players.length;

            while (!players[currentPlayer].isPlaying()) {
                currentPlayer++;
                currentPlayer %= players.length;
            }
        }
        currentPlayerPlayedCard = false;
        if (currentTD != null) {
            currentTD.spins = spins;
            controller.takeTurn(currentTD, needToSend);
        }
        currentTD = null;
        spins = new boolean[6];
        Log.d(TAG, "startNextTurn() the end" + "shuffle is null? " + (controller.gameData.shuffle == null));
    }

    public void startNextTurn() {
        startNextTurn(true);
    }

    public static int getSaboteurCount(int playersCount) {
        if (playersCount <= 4)
            return 1;
        if (playersCount <= 6)
            return 2;
        if (playersCount <= 9)
            return 3;
        else
            return 4;
    }

    public boolean canPutTunnel(Tunnel tunnel, int i, int j) {
        if (field[i][j] != null) {
            return false;
        }
        boolean hasContinue = false;
        if (i < HEIGHT - 1 && field[i + 1][j] != null) {
            Tunnel t = field[i + 1][j];
            if (!t.isClosedTunnel() || t.isClosedTunnel() && !((ClosedTunnel) t).isClosed()) {
                if (tunnel.down) {
                    if (t.up) {
                        hasContinue = true;
                    } else {
                        return false;
                    }
                } else if (t.up)
                    return false;
            }
        }
        if (i > 0 && field[i - 1][j] != null) {
            Tunnel t = field[i - 1][j];
            if (!t.isClosedTunnel() || t.isClosedTunnel() && !((ClosedTunnel) t).isClosed()) {
                if (tunnel.up) {
                    if (t.down) {
                        hasContinue = true;
                    } else {
                        return false;
                    }
                } else if (t.down)
                    return false;
            }
        }
        if (i < WIDTH - 1 && field[i][j + 1] != null) {
            Tunnel t = field[i][j + 1];
            if (!t.isClosedTunnel() || t.isClosedTunnel() && !((ClosedTunnel) t).isClosed()) {
                if (tunnel.right) {
                    if (t.left) {
                        hasContinue = true;
                    } else {
                        return false;
                    }
                } else if (t.left)
                    return false;
            }
        }
        if (j > 0 && field[i][j - 1] != null) {
            Tunnel t = field[i][j - 1];
            if (!t.isClosedTunnel() || t.isClosedTunnel() && !((ClosedTunnel) t).isClosed()) {
                if (tunnel.left) {
                    if (t.right) {
                        hasContinue = true;
                    } else {
                        return false;
                    }
                } else if (t.right)
                    return false;
            }
        }
        return hasContinue;
    }

    public void putTunnel(Tunnel tunnel, int i, int j) {
        field[i][j] = tunnel;
    }

    private void initializePlayers(Shuffle shuffle) {
        for (int i = 0; i < playingCount; i++) {
            players[i] = new Player(shuffle.whoAreSaboteur[i]);
        }
    }

    private void initializeField(Shuffle shuffle) {
        try {
            JSONArray json = SaboteurApplication.getInstance().tunnels;
            field[ENTRY_POS_I][ENTRY_POS_J] = new Tunnel(json.getJSONArray(16), this);
            int[] shuffleArray = shuffle.closedTunnelsShuffle;
            ClosedTunnel[] tunnels = new ClosedTunnel[3];
            tunnels[0] = new ClosedTunnel(json.getJSONArray(17), this);
            tunnels[1] = new ClosedTunnel(json.getJSONArray(18), this);
            tunnels[2] = new ClosedTunnel(json.getJSONArray(19), this);
            int firstTunnelI = ENTRY_POS_I - 8;
            int firstTunnelJ = ENTRY_POS_J - 2;
            for (int k = 0; k < 3; k++) {
                field[firstTunnelI][firstTunnelJ + k * 2] = tunnels[shuffleArray[k]];
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean[][] used = new boolean[HEIGHT][WIDTH];

    public void startDfs() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                used[i][j] = false;
            }
        }
        dfs(ENTRY_POS_I, ENTRY_POS_J);
    }

    private boolean checkTunnel(int i, int j, int iNext, int jNext) {
        Tunnel tunnel = field[i][j];
        if (iNext > HEIGHT - 1 || iNext < 0 || jNext > WIDTH - 1 || jNext < 0)
            return false;
        if (!used[iNext][jNext] && field[iNext][jNext] != null && field[iNext][jNext].centre) {
            Tunnel t = field[iNext][jNext];
            boolean wayTo;
            if (i + 1 == iNext && tunnel.down) {
                wayTo = t.up;
            } else if (i - 1 == iNext && tunnel.up) {
                wayTo = t.down;
            } else if (j + 1 == jNext && tunnel.right) {
                wayTo = t.left;
            } else if (j - 1 == jNext && tunnel.left) {
                wayTo = t.right;
            } else {
                return false;
            }
            if (!t.isClosedTunnel() || !((ClosedTunnel) t).isClosed()) {
                return wayTo;
            } else {
                ClosedTunnel ct = (ClosedTunnel) t;
                ct.open();
                if (ct.isGold()) {
                    finish();
                    return false;
                } else {
                    if (!wayTo)
                        ct.spin();
                    return false;
                }
            }
        }
        return false;
    }

    private void finish() {
        if (!controller.isSinglePlayer())
            controller.multiPlayer.finish();
        theEnd = true;
    }

    private void dfs(int i, int j) {
        used[i][j] = true;
        if (checkTunnel(i, j, i + 1, j))
            dfs(i + 1, j);
        if (checkTunnel(i, j, i - 1, j))
            dfs(i - 1, j);
        if (checkTunnel(i, j, i, j + 1))
            dfs(i, j + 1);
        if (checkTunnel(i, j, i, j - 1))
            dfs(i, j - 1);
    }

    private void giveCards() {
        for (int now = 0; now < players.length; now++) {
            for (int i = 0; i < 6; i++) {
                Card next = deck.remove(deck.size() - 1);
                next.setPlayerNumber(now);
                players[now].addCard(next);
            }
        }
    }

    private void addHealingCards() {
        JSONArray actions = SaboteurApplication.getInstance().actions;
        for (int i = 0; i < 6; i++) {
            try {
                JSONArray action = actions.getJSONArray(i);
                for(int j = 0; j < action.getInt(0); j++) {
                    deck.add(new Heal(action, this));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addDebuffCards() {
        JSONArray actions = SaboteurApplication.getInstance().actions;
        for (int i = 6; i < 9; i++) {
            try {
                JSONArray action = actions.getJSONArray(i);
                for(int j = 0; j < action.getInt(0); j++) {
                    deck.add(new Debuff(action, this));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void addWatchCards() {
        JSONArray actions = SaboteurApplication.getInstance().actions;
        for (int i = 9; i < 10; i++) {
            try {
                JSONArray action = actions.getJSONArray(i);
                for(int j = 0; j < action.getInt(0); j++) {
                    deck.add(new Watch(action, this));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void addDestroyCards() {
        JSONArray actions = SaboteurApplication.getInstance().actions;
        for (int i = 10; i < 11; i++) {
            try {
                JSONArray action = actions.getJSONArray(i);
                for(int j = 0; j < action.getInt(0); j++) {
                    deck.add(new Destroy(action, this));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addTunnels() {
        JSONArray tunnels = SaboteurApplication.getInstance().tunnels;
        for (int i = 0; i < 16; i++) {
            try {
                JSONArray tunnel = tunnels.getJSONArray(i);
                for(int j = 0; j < tunnel.getInt(0); j++) {
                    deck.add(new Tunnel(tunnel, this));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeDeck() {
        addTunnels();
        addHealingCards();
        addDebuffCards();
        addWatchCards();
        addDestroyCards();
    }

    public Field(int playersCount, Controller controller) {
        this(playersCount, controller, null);
    }

    public Field(int playersCount, Controller controller, Shuffle shuffle) {

        initializeDeck();
        if (shuffle == null)
            shuffle = new Shuffle(playersCount, deck.size(), getSaboteurCount(playersCount));
        this.controller = controller;
        playingCount = playersCount;
        players = new Player[playingCount];
        initializeField(shuffle);
        initializePlayers(shuffle);
        Card[] shuffled = new Card[deck.size()];
        for (int i = 0; i < deck.size(); i++) {
            shuffled[shuffle.deckShuffle[i]] = deck.get(i);
        }
        deck.clear();
        deck.addAll(Arrays.asList(shuffled));
        giveCards();
        if (!controller.isSinglePlayer())
            currentPlayer = 1;
    }


}
