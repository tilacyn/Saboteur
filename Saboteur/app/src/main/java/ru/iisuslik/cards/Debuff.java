package ru.iisuslik.cards;

import ru.iisuslik.field.Field;
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

    public void play(int playerNumber) {
        field.players[playerNumber].breakIt(this);
        field.players[playerNumber].playCard(this);
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
