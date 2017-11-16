package ru.iisuslik.cards;

import ru.iisuslik.field.Field;

public class Watch extends Action {
    public Watch(int id, String name, String description, Field field, int playerNumber) {
        super(id, name, description, field, playerNumber, WATCH);
    }

    public boolean canPlay(int i, int j) {
        Tunnel toCheck = field.field[i][j];
        return !field.didCurrentPlayerPlayCard() && toCheck != null && toCheck.isClosedTunnel();
    }

    public boolean play(int i, int j) {
        ClosedTunnel ct = (ClosedTunnel) field.field[i][j];
        field.players[playerNumber].playCard(this);
        field.iPlayedCard();
        return ct.isGold();
    }
}
