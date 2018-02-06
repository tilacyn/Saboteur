package ru.iisuslik.cards;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Destroy extends Card {
    public Destroy(int id, String name, String description, Field field, int playerNumber) {
        super(id, name, description, field, playerNumber);
    }

    public boolean canPlay(int i, int j) {
        Tunnel toCheck = field.field[i][j];
        return !(i == Field.ENTRY_POS_I && j == Field.ENTRY_POS_J) &&
                !field.didCurrentPlayerPlayCard() &&
                toCheck != null && !toCheck.isClosedTunnel()
                && field.controller.isMyTurn();
    }

    public void play(int i, int j) {
        field.currentTD = new TurnData(CARD_TYPE.DESTROY, ownerPlayerNumber,
                field.players[ownerPlayerNumber].getCardNumber(this),
                i, j, -1);
        field.field[i][j] = null;
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();
    }
}


