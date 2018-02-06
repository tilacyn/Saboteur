package ru.iisuslik.field;

import ru.iisuslik.cards.*;
import ru.iisuslik.controller.Controller;
import ru.iisuslik.gameData.Shuffle;
import ru.iisuslik.gameData.TurnData;
import ru.iisuslik.player.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Field implements Serializable {

    private boolean currentPlayerPlayedCard = false;
    public TurnData currentTD;
    public boolean[] spins = new boolean[6];

    public boolean didCurrentPlayerPlayCard() {
        //if(controller.isSinglePlayer())
            return currentPlayerPlayedCard;
        //return currentPlayerPlayedCard || controller.multiPlayer.getMyNumber() != currentPlayer;
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
    public ArrayList<Card> deck = new ArrayList<>();


    public void applyTurnData(TurnData gameData) {
        int playerFrom = gameData.ownerPlayerNumber;
        int cardNumber = gameData.cardNumber;
        ArrayList<Card> hand = players[playerFrom].getHand();
        gameData.apply(hand.get(cardNumber));
        for (int i = 0; i < hand.size(); i++) {
            if (gameData.spins[i]) {
                ((Tunnel) hand.get(i)).spin();
            }
        }

        startNextTurn(false);
    }


    public ArrayList<Card> getCurrentPlayerHand() {
        if(controller.isSinglePlayer())
            return players[currentPlayer].getHand();
        return players[controller.multiPlayer.getMyNumber()].getHand();
    }

    public boolean[] getPlayerDebuffs(int index) {
        return new boolean[]{players[index].isBrokenLamp(),
                players[index].isBrokenPick(),
                players[index].isBrokenTrolley()};
    }

    public boolean isCurrentPlayerSaboteur() {
        if(!controller.isSinglePlayer())
            return players[controller.multiPlayer.getMyNumber()].getPersonality() == Player.SABOTEUR;
        return players[currentPlayer].getPersonality() == Player.SABOTEUR;
    }

    public void startNextTurn(boolean needToSend) {
        if (deck.size() != 0) {
            Card next = deck.remove(deck.size() - 1);
            next.setPlayerNumber(currentPlayer);
            players[currentPlayer].addCard(next);
        }
        if (players[currentPlayer].getHand().size() == 0) {
            players[currentPlayer].concede();
            playingCount--;
        }
        if (playingCount == 0) {
            theEnd = true;
            return;
        }
        currentPlayer++;
        currentPlayer %= players.length;

        while (!players[currentPlayer].isPlaying()) {
            currentPlayer++;
            currentPlayer %= players.length;
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
        field[ENTRY_POS_I][ENTRY_POS_J] = new Tunnel("ENTRY",
                "First tunnel in the way",
                this, Card.NO_PLAYER,
                true, true, true, true, true);
        int[] perestanovka = shuffle.closedTunnelsShuffle;
        ClosedTunnel[] tunnels = new ClosedTunnel[3];
        tunnels[0] = new ClosedTunnel("Closed Tunnel", "Probably has gold", this, -1,
                true, true, true, true, true, true);
        tunnels[1] = new ClosedTunnel("Closed Tunnel", "Probably has gold", this, -1,
                true, false, false, true, true, false);
        tunnels[2] = new ClosedTunnel("Closed Tunnel", "Probably has gold", this, -1,
                true, false, true, false, true, false);
        int firstTunnelI = ENTRY_POS_I - 8;
        int firstTunnelJ = ENTRY_POS_J - 2;
        for (int k = 0; k < 3; k++) {
            field[firstTunnelI][firstTunnelJ + k * 2] = tunnels[perestanovka[k]];
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
                    theEnd = true;
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

    private void addTunnel(int count, int up, int down, int left, int right, int centre, int id) {
        for (int i = 0; i < count; i++) {
            Tunnel tunnel = new Tunnel("Tunnel", "Just a tunnel", this, -1,
                    up == 1, down == 1, left == 1, right == 1, centre == 1);
            deck.add(tunnel);
        }
    }

    private void addHealingCards() {
        for (int i = 0; i < 2; i++) {
            deck.add(new Heal(32, "Heal lamp", "Heals lamp (choose player)", this, -1,
                    true, false, false));
            deck.add(new Heal(33, "Heal trolley", "Heals trolley (choose player)", this, -1,
                    false, true, false));
            deck.add(new Heal(34, "Heal pick", "Heals pick (choose player)", this, -1,
                    false, false, true));
        }
        deck.add(new Heal(35, "Heal pick and trolley", "Heals pick and trolley (choose player)", this, -1,
                false, true, true));
        deck.add(new Heal(36, "Heal pick and lamp", "Heals pick and lamp (choose player)", this, -1,
                true, false, true));
        deck.add(new Heal(37, "Heal lamp and trolley", "Heals lamp and trolley (choose player)", this, -1,
                true, true, false));
    }

    private void addDebuffCards() {
        for (int i = 0; i < 3; i++) {
            deck.add(new Debuff(38, "Break lamp", "Breaks lamp (choose player)", this, -1,
                    true, false, false));
            deck.add(new Debuff(39, "Break trolley", "Breaks trolley (choose player)", this, -1,
                    false, true, false));
            deck.add(new Debuff(40, "Break pick", "Breaks pick (choose player)", this, -1,
                    false, false, true));
        }
    }


    private void addWatchCards() {
        for (int i = 0; i < 6; i++) {
            deck.add(new Watch(41, "Watch closed cards", "Check if closed card has gold inside", this, -1));
        }
    }


    private void addDestroyCards() {
        for (int i = 0; i < 6; i++) {
            deck.add(new Destroy(42, "Destroy tunnel", "Destroy a tunnel, except entry and closed cards", this, -1));
        }
    }

    private void initializeDeck() {
        addTunnel(5, 1, 0, 1, 1, 1, 0);
        addTunnel(4, 0, 0, 1, 1, 1, 1);
        addTunnel(5, 1, 1, 0, 1, 1, 2);
        addTunnel(5, 1, 1, 1, 1, 1, 3);
        addTunnel(3, 1, 1, 0, 0, 1, 4);
        addTunnel(4, 0, 1, 1, 0, 1, 5);
        addTunnel(5, 1, 1, 0, 1, 1, 6);
        addTunnel(1, 0, 0, 1, 0, 1, 7);
        addTunnel(1, 1, 1, 1, 0, 0, 8);
        addTunnel(1, 1, 0, 0, 0, 1, 9);
        addTunnel(1, 1, 1, 1, 1, 0, 10);
        addTunnel(1, 0, 1, 1, 1, 0, 11);
        addTunnel(1, 1, 0, 1, 0, 0, 12);
        addTunnel(1, 0, 1, 1, 0, 0, 13);
        addTunnel(1, 0, 0, 1, 1, 0, 14);
        addTunnel(1, 1, 1, 0, 0, 0, 15);
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
        Card [] shuffled = new Card[deck.size()];
        for (int i = 0; i < deck.size(); i++) {
            shuffled[shuffle.deckShuffle[i]]= deck.get(i);
        }
        deck.clear();
        deck.addAll(Arrays.asList(shuffled));
        giveCards();
    }





}
