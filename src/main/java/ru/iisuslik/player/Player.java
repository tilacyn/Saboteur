package ru.iisuslik.player;

import ru.iisuslik.cards.Card;
import ru.iisuslik.cards.Debuff;
import ru.iisuslik.cards.Heal;

import java.util.ArrayList;


public class Player {

    public static final int SABOTEUR = 1;
    public static final int GNOME = 2;

    private boolean skipMe = false;
    private int personality;
    private ArrayList<Card> hand = new ArrayList<>();
    private boolean brokenLamp = false, brokenPick = false, brokenTrolley = false;

    public boolean isPlaying() {
        return !skipMe;
    }

    public boolean isBrokenLamp() {
        return brokenLamp;
    }

    public boolean isBrokenTrolley() {
        return brokenTrolley;
    }

    public boolean isBrokenPick() {
        return brokenPick;
    }

    public int getPersonality() {
        return personality;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public Player(int personality) {
        this.personality = personality;
    }

    public boolean canPutTunnels() {
        return !brokenLamp && !brokenPick && !brokenTrolley;
    }

    public void concede() {
        skipMe = true;
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public void playCard(Card card) {
        hand.remove(card);
    }

    public boolean needHeal(Heal heal) {
        return heal.isHealLamp() && brokenLamp ||
                heal.isHealPick() && brokenPick ||
                heal.isHealTrolley() && brokenTrolley;
    }

    public void heal(Heal heal) {
        brokenLamp &= !heal.isHealLamp();
        brokenPick &= !heal.isHealPick();
        brokenTrolley &= !heal.isHealTrolley();
    }

    public boolean canBreak(Debuff debuff) {
        return debuff.isBreakingLamp() && !brokenLamp ||
                debuff.isBreakingPick() && !brokenPick ||
                debuff.isBreakingTrolley() && !brokenTrolley;
    }

    public void breakIt(Debuff debuff) {
        brokenLamp |= debuff.isBreakingLamp();
        brokenPick |= debuff.isBreakingPick();
        brokenTrolley |= debuff.isBreakingTrolley();
    }
}
