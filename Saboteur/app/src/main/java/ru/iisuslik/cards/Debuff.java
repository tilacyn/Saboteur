package ru.iisuslik.cards;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Debuff extends Card {

    private boolean breakingLamp, breakingTrolley, breakingPick;

    public Debuff(int id, String name, String description, Field field, int playerNumber,
                  boolean breakingLamp, boolean breakingTrolley, boolean breakingPick) {
        super(id, name, description, field, playerNumber);
        this.breakingLamp = breakingLamp;
        this.breakingPick = breakingPick;
        this.breakingTrolley = breakingTrolley;
    }

    public boolean canPlay(int playerNumber) {
        return !field.didCurrentPlayerPlayCard() && field.players[playerNumber].canBreak(this)
                && field.controller.isMyTurn();
    }

    public void play(final int playerNumber) {
        field.currentTD = new TurnData(CARD_TYPE.DEBUFF, ownerPlayerNumber,
                field.players[ownerPlayerNumber].getCardNumber(this),
                -1, -1, playerNumber,
                isBreakingLamp(), isBreakingTrolley(), isBreakingPick());
        field.players[playerNumber].breakIt(this);
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();

    }


    public boolean isBreakingLamp() {
        return breakingLamp;
    }

    public boolean isBreakingPick() {
        return breakingPick;
    }

    public boolean isBreakingTrolley() {
        return breakingTrolley;
    }
}
