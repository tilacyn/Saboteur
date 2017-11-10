package ru.iisuslik.field;

import ru.iisuslik.cards.*;
import ru.iisuslik.player.*;

import java.util.ArrayList;
import java.util.Random;

public class Field {

    public static final int WIDTH = 19;
    public static final int HEIGHT = 50;

    public static final int ENTRY_POS_I = 40;
    public static final int ENTRY_POS_J = 10;

    private int playingCount;
    private int currentPlayer = 0;
    private boolean theEnd = false;
    public Tunnel[][] field = new Tunnel[WIDTH][HEIGHT];
    public Player[] players;
    public ArrayList<Card> deck;


    public ArrayList<Card> getCurrentPlayerHand() {
        return players[currentPlayer].getHand();
    }

    public boolean[] getCurrentPlayerDebuffs() {
        return new boolean[]{players[currentPlayer].isBrokenLamp(),
                players[currentPlayer].isBrokenLamp(),
                players[currentPlayer].isBrokenLamp()};
    }

    public void startNextTurn() {
        if (deck.size() != 0)
            players[currentPlayer].addCard(deck.remove(deck.size() - 1));
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
    }

    private void randomShuffle(int arr[]) {
        Random rnd = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
    }

    private <T> void randomShuffle(ArrayList<T> arr) {
        Random rnd = new Random();
        for (int i = arr.size() - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            T a = arr.get(index);
            arr.set(index, arr.get(i));
            arr.set(i, a);
        }
    }

    private int getSaboteurCount(int playersCount) {
        return 1;//TODO
    }

    public boolean canPutTunnel(Tunnel tunnel, int i, int j) {
        if (field[i][j] != null) {
            return false;
        }
        if (i < HEIGHT - 1 && field[i + 1][j] != null && !field[i + 1][j].up)
            return false;
        if (i > 0 && field[i - 1][j] != null && !field[i - 1][j].down)
            return false;
        if (j < WIDTH - 1 && field[i][j + 1] != null && !field[i][j + 1].left)
            return false;
        if (j > 0 && field[i][j + 1] != null && !field[i][j + 1].right)
            return false;
        return true;
    }

    public void putTunnel(Tunnel tunnel, int i, int j) {
        field[i][j] = tunnel;
    }

    private void initializePlayers() {
        int[] badOrGood = new int[playingCount];
        int saboteurCount = getSaboteurCount(playingCount);
        for (int i = 0; i < playingCount; i++) {
            if (i < saboteurCount) {
                badOrGood[i] = Player.SABOTEUR;
            } else {
                badOrGood[i] = Player.GNOME;
            }
        }
        randomShuffle(badOrGood);
        for (int i = 0; i < playingCount; i++) {
            players[i] = new Player(badOrGood[i]);
        }
    }

    private void initializeField() {
        field[ENTRY_POS_I][ENTRY_POS_J] = new Tunnel(43, "ENTRY", //TODO id
                "First tunnel in the way",
                this, Card.NO_PLAYER,
                true, true, true, true, true);
        int[] perestanovka = {0, 1, 2};
        randomShuffle(perestanovka);
        ClosedTunnel[] tunnels = new ClosedTunnel[3];
        tunnels[0] = new ClosedTunnel(43, "Closed Tunnel", "Probably has gold", this, -1,
                true, true, true, true, true, true);
        tunnels[1] = new ClosedTunnel(43, "Closed Tunnel", "Probably has gold", this, -1,
                true, false, false, true, true, false);
        tunnels[2] = new ClosedTunnel(43, "Closed Tunnel", "Probably has gold", this, -1,
                true, false, true, false, true, false);
        int firstTunnelI = ENTRY_POS_I - 8;
        int firstTunnelJ = ENTRY_POS_J - 2;
        for (int k = 0; k < 3; k++) {
            field[firstTunnelI][firstTunnelJ + k * 2] = tunnels[perestanovka[k]];
        }
    }

    private void giveCards() {
        for (Player player : players) {
            for (int i = 0; i < 6; i++)
                player.addCard(deck.remove(deck.size() - 1));
        }
    }

    private void addTunnel(int count, int up, int down, int left, int right, int centre) {
        for (int i = 0; i < count; i++)
            deck.add(new Tunnel(43, "Tunnel", "Just a tunnel", this, -1,
                    up == 1, down == 1, left == 1, right == 1, centre == 1));
    }

    private void addHealingCards() {
        for (int i = 0; i < 2; i++) {
            deck.add(new Heal(43, "Heal lamp", "Heals lamp (choose player)", this, -1,
                    true, false, false));
            deck.add(new Heal(43, "Heal trolley", "Heals trolley (choose player)", this, -1,
                    false, true, false));
            deck.add(new Heal(43, "Heal pick", "Heals pick (choose player)", this, -1,
                    false, false, true));
        }
        deck.add(new Heal(43, "Heal pick and trolley", "Heals pick and trolley (choose player)", this, -1,
                false, true, true));
        deck.add(new Heal(43, "Heal pick and lamp", "Heals pick and lamp (choose player)", this, -1,
                true, false, true));
        deck.add(new Heal(43, "Heal lamp and trolley", "Heals lamp and trolley (choose player)", this, -1,
                true, true, false));
    }

    private void addDebuffCards() {
        for (int i = 0; i < 3; i++) {
            deck.add(new Debuff(43, "Break lamp", "Breaks lamp (choose player)", this, -1,
                    true, false, false));
            deck.add(new Debuff(43, "Break trolley", "Breaks trolley (choose player)", this, -1,
                    false, true, false));
            deck.add(new Debuff(43, "Break pick", "Breaks pick (choose player)", this, -1,
                    false, false, true));
        }
    }


    private void addWatchCards() {
        for (int i = 0; i < 6; i++) {
            deck.add(new Watch(43, "Watch closed cards", "Check if closed card has gold inside", this, -1));
        }
    }


    private void addDestroyCards() {
        for (int i = 0; i < 6; i++) {
            deck.add(new Destroy(43, "Destroy tunnel", "Destroy a tunnel, except entry and closed cards", this, -1));
        }
    }

    private void initializeDeck() {
        addTunnel(5, 1, 0, 1, 1, 1);
        addTunnel(4, 0, 0, 1, 1, 1);
        addTunnel(5, 1, 1, 0, 1, 1);
        addTunnel(5, 0, 1, 1, 1, 1);
        addTunnel(3, 1, 1, 0, 0, 1);
        addTunnel(4, 0, 1, 1, 0, 1);
        addTunnel(5, 1, 1, 0, 1, 1);
        addTunnel(1, 0, 0, 1, 0, 1);
        addTunnel(1, 1, 1, 1, 0, 0);
        addTunnel(1, 1, 0, 0, 0, 1);
        addTunnel(1, 1, 1, 1, 1, 0);
        addTunnel(1, 0, 1, 1, 1, 0);
        addTunnel(1, 1, 0, 1, 0, 0);
        addTunnel(1, 0, 1, 1, 0, 0);
        addTunnel(1, 0, 0, 1, 1, 0);
        addTunnel(1, 1, 1, 0, 0, 0);
        addHealingCards();
        addDebuffCards();
        addWatchCards();
        addDestroyCards();
    }


    public Field(int playersCount) {
        playingCount = playersCount;
        players = new Player[playingCount];
        initializeField();
        initializePlayers();
        initializeDeck();
        //TODO
    }
}
