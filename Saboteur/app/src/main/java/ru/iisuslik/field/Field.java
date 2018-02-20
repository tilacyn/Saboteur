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

    private void playCardWithTurnData(Card cardToPlay, TurnData turnData) {
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
    }


    private void spinCardsInHand(ArrayList<Card> hand, TurnData turnData) {
        if (hand.size() > 6)
            Log.d(TAG, "hand size is too big " + hand.size());
        for (int i = 0; i < hand.size(); i++) {
            if (turnData.spins[i]) {
                ((Tunnel) hand.get(i)).spin();
            }
        }
    }

    public void applyTurnData(TurnData turnData) {
        int playerFrom = turnData.ownerPlayerNumber;
        int cardNumber = turnData.cardNumber;
        ArrayList<Card> hand = players[playerFrom].getHand();
        spinCardsInHand(hand, turnData);
        Card cardToPlay = hand.get(cardNumber);
        playCardWithTurnData(cardToPlay, turnData);
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

    private void giveACardToCurrentPlayer() {
        Card next = deck.remove(deck.size() - 1);
        next.setPlayerNumber(currentPlayer);
        players[currentPlayer].addCard(next);
    }

    private void startNextTurn(boolean needToSend) {
        Log.d(TAG, "startNextTurn(), cur player " + (currentPlayer + 1) + " hand size " + players[currentPlayer].getHand().size());
        if (deck.size() != 0) {
            giveACardToCurrentPlayer();
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

    private CheckNeighborResult checkNeighborTunnel(int i, int j, int iNeighbor, int jNeighbor, Tunnel current) {
        if (!doesTunnelExists(iNeighbor, jNeighbor))
            return CheckNeighborResult.NoConflict;
        Tunnel neighbor = field[iNeighbor][jNeighbor];
        if (neighbor.isClosedTunnel() && ((ClosedTunnel) neighbor).isClosed())
            return CheckNeighborResult.NoConflict;
        boolean currentExit, neighborEntry;
        if (iNeighbor == i + 1) {
            currentExit = current.down;
            neighborEntry = neighbor.up;
        } else if (iNeighbor == i - 1) {
            currentExit = current.up;
            neighborEntry = neighbor.down;
        } else if (jNeighbor == j + 1) {
            currentExit = current.right;
            neighborEntry = neighbor.left;
        } else {
            currentExit = current.left;
            neighborEntry = neighbor.right;
        }
        if (currentExit) {
            if (neighborEntry)
                return CheckNeighborResult.HasContinue;
            else
                return CheckNeighborResult.BadNeighbor;
        } else if (neighborEntry) {
            return CheckNeighborResult.BadNeighbor;
        }
        return CheckNeighborResult.NoConflict;
    }

    private enum CheckNeighborResult {BadNeighbor, NoConflict, HasContinue}

    public boolean canPutTunnel(Tunnel tunnel, int i, int j) {
        if (field[i][j] != null) {
            return false;
        }
        boolean hasContinue = false;
        ////
        CheckNeighborResult result = checkNeighborTunnel(i, j, i + 1, j, tunnel);
        if (result == CheckNeighborResult.BadNeighbor)
            return false;
        else hasContinue |= result == CheckNeighborResult.HasContinue;
        ////
        result = checkNeighborTunnel(i, j, i - 1, j, tunnel);
        if (result == CheckNeighborResult.BadNeighbor)
            return false;
        else hasContinue |= result == CheckNeighborResult.HasContinue;
        ////
        result = checkNeighborTunnel(i, j, i, j + 1, tunnel);
        if (result == CheckNeighborResult.BadNeighbor)
            return false;
        else hasContinue |= result == CheckNeighborResult.HasContinue;
        ////
        result = checkNeighborTunnel(i, j, i, j - 1, tunnel);
        if (result == CheckNeighborResult.BadNeighbor)
            return false;
        else hasContinue |= result == CheckNeighborResult.HasContinue;
        return hasContinue;
    }

    public void putTunnel(Tunnel tunnel, int i, int j) {
        field[i][j] = tunnel;
    }

    private void initializePlayers(Shuffle shuffle) {
        players = new Player[playingCount];
        for (int i = 0; i < playingCount; i++) {
            players[i] = new Player(shuffle.whoAreSaboteur[i]);
        }
    }

    private void initializeField(Shuffle shuffle) {
        int[][] json = SaboteurApplication.getInstance().tunnels;
        field[ENTRY_POS_I][ENTRY_POS_J] = new Tunnel(json[16], this);
        int[] shuffleArray = shuffle.closedTunnelsShuffle;
        ClosedTunnel[] tunnels = new ClosedTunnel[3];
        tunnels[0] = new ClosedTunnel(json[17], this);
        tunnels[1] = new ClosedTunnel(json[18], this);
        tunnels[2] = new ClosedTunnel(json[19], this);
        int firstTunnelI = ENTRY_POS_I - 8;
        int firstTunnelJ = ENTRY_POS_J - 2;
        for (int k = 0; k < 3; k++) {
            field[firstTunnelI][firstTunnelJ + k * 2] = tunnels[shuffleArray[k]];
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

    private boolean dfsCheckTunnel(int i, int j, int iNext, int jNext) {
        Tunnel tunnel = field[i][j];
        if (!doesTunnelExists(iNext, jNext))
            return false;
        if (!used[iNext][jNext] && field[iNext][jNext].centre) {
            Tunnel nextTunnel = field[iNext][jNext];
            if ((i + 1 == iNext && tunnel.down) ||
                    (i - 1 == iNext && tunnel.up) ||
                    (j + 1 == jNext && tunnel.right) ||
                    (j - 1 == jNext && tunnel.left)) {
                if (!nextTunnel.isClosedTunnel() || !((ClosedTunnel) nextTunnel).isClosed()) {
                    return true;
                } else {
                    ClosedTunnel ct = (ClosedTunnel) nextTunnel;
                    ct.open();
                    if (ct.isGold()) {
                        finish();
                        return false;
                    } else {
                        if (i + 1 == iNext && !nextTunnel.up)
                            nextTunnel.spin();
                        if (i - 1 == iNext && !nextTunnel.down)
                            nextTunnel.spin();
                        if (j + 1 == jNext && !nextTunnel.left)
                            nextTunnel.spin();
                        if (j - 1 == jNext && !nextTunnel.right)
                            nextTunnel.spin();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean doesTunnelExists(int i, int j) {
        return i <= HEIGHT - 1 && i >= 0 && j <= WIDTH - 1 && j >= 0 && field[i][j] != null;
    }


    private void finish() {
        if (!controller.isSinglePlayer())
            controller.multiPlayer.finish();
        theEnd = true;
    }

    private void dfs(int i, int j) {
        used[i][j] = true;
        if (!field[i][j].centre)
            return;
        if (dfsCheckTunnel(i, j, i + 1, j))
            dfs(i + 1, j);
        if (dfsCheckTunnel(i, j, i - 1, j))
            dfs(i - 1, j);
        if (dfsCheckTunnel(i, j, i, j + 1))
            dfs(i, j + 1);
        if (dfsCheckTunnel(i, j, i, j - 1))
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
        int[][] actions = SaboteurApplication.getInstance().actions;
        for (int i = 0; i < 6; i++) {
            int[] action = actions[i];
            for (int j = 0; j < action[0]; j++) {
                deck.add(new Heal(action, this));
            }
        }
    }

    private void addDebuffCards() {
        int[][] actions = SaboteurApplication.getInstance().actions;
        for (int i = 6; i < 9; i++) {
            int[] action = actions[i];
            for (int j = 0; j < action[0]; j++) {
                deck.add(new Debuff(action, this));
            }
        }
    }


    private void addWatchCards() {
        int[][] actions = SaboteurApplication.getInstance().actions;
        for (int i = 9; i < 10; i++) {
            int[] action = actions[i];
            for (int j = 0; j < action[0]; j++) {
                deck.add(new Watch(action, this));
            }
        }
    }


    private void addDestroyCards() {
        int[][] actions = SaboteurApplication.getInstance().actions;
        for (int i = 10; i < 11; i++) {
            int[] action = actions[i];
            for (int j = 0; j < action[0]; j++) {
                deck.add(new Destroy(action, this));
            }
        }
    }

    private void addTunnels() {
        int[][] tunnels = SaboteurApplication.getInstance().tunnels;
        for (int i = 0; i < 16; i++) {
            int[] tunnel = tunnels[i];
            for (int j = 0; j < tunnel[0]; j++) {
                deck.add(new Tunnel(tunnel, this));
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
        initializePlayers(shuffle);
        initializeField(shuffle);
        shuffleDeck(shuffle);
        giveCards();
        if (!controller.isSinglePlayer())
            currentPlayer = 0;
    }

    public static Field getFieldForTests(Controller controller) {
        return new Field(controller);
    }

    private Field(Controller controller) {
        initializeField(new Shuffle(1, 0, 0));
        this.controller = controller;
        playingCount = 1;
        initializePlayers(new Shuffle(1, 0, 0));
    }

    private void shuffleDeck(Shuffle shuffle) {
        Card[] shuffled = new Card[deck.size()];
        for (int i = 0; i < deck.size(); i++) {
            shuffled[shuffle.deckShuffle[i]] = deck.get(i);
        }
        deck.clear();
        deck.addAll(Arrays.asList(shuffled));
    }


}
