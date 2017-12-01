package ru.iisuslik.cards;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.GameData;
import ru.iisuslik.player.Player;

public class Debuff extends Action {

    private boolean breakingLamp, breakingTrolley, breakingPick;
    public Debuff(int id, String name, String description, Field field, int playerNumber,
                boolean breakingLamp, boolean breakingTrolley, boolean breakingPick) {
        super(id, name, description, field, playerNumber, DEBUFF);
        this.breakingLamp = breakingLamp;
        this.breakingPick = breakingPick;
        this.breakingTrolley = breakingTrolley;
    }
    public boolean canPlay(int playerNumber) {
        return !field.didCurrentPlayerPlayCard() && !field.didCurrentPlayerPlayCard() && field.players[playerNumber].canBreak(this);
    }

    public void play(final int playerNumber, boolean needToSend) {
        field.players[playerNumber].breakIt(this);
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();
        if (needToSend) {
            field.currentGD = new GameData(ownerPlayerNumber,
                    field.players[ownerPlayerNumber].getCardNumber(this),
                    -1, -1, playerNumber, this) {
                @Override
                public void apply(Card card) {
                    ((Debuff)card).play(targetPlayerNumber, false);
                }
            };
        }
    }

    public void play(int playerNumber){
        play(playerNumber, true);
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
