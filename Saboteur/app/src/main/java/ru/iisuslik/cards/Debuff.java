package ru.iisuslik.cards;

import org.json.JSONArray;
import org.json.JSONException;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Debuff extends Card {

    private boolean breakingLamp, breakingTrolley, breakingPick;

    public Debuff(JSONArray action, Field field) throws JSONException {
        super(action.getInt(4), field, -1);
        this.breakingLamp = action.getInt(1) == 1;
        this.breakingPick = action.getInt(2) == 1;
        this.breakingTrolley = action.getInt(3) == 1;
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
