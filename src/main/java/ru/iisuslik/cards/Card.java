package ru.iisuslik.cards;

import ru.iisuslik.field.Field;

public class Card {

    public static final int TUNNEL = 1;
    public static final int ACTION = 2;

    public static final int NO_PLAYER = -1;

    protected int playerNumber;
    protected Field field;
    protected int type;
    protected int id;
    protected String name;
    protected String description;

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public Card(int type, int id, String name, String description, Field field, int playerNumber) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.description = description;
        this.field = field;
        this.playerNumber = playerNumber;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getType() {
        return type;
    }

    public void discard() {
        field.players[playerNumber].playCard(this);
    }
}
