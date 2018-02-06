package ru.iisuslik.cards;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Watch extends Card {
    public Watch(int id, String name, String description, Field field, int playerNumber) {
        super(id, name, description, field, playerNumber);
    }

    public boolean canPlay(int i, int j) {
        Tunnel toCheck = field.field[i][j];
        return !field.didCurrentPlayerPlayCard() && toCheck != null && toCheck.isClosedTunnel()
                && field.controller.isMyTurn();
    }

    public boolean play(int i, int j) {
        field.currentTD = new TurnData(CARD_TYPE.WATCH, ownerPlayerNumber,
                field.players[ownerPlayerNumber].getCardNumber(this),
                i, j, -1);
        ClosedTunnel ct = (ClosedTunnel) field.field[i][j];
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();
        return ct.isGold();
    }
}
