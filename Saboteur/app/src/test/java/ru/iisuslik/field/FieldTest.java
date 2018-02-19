package ru.iisuslik.field;

import org.junit.Test;


import ru.iisuslik.cards.ClosedTunnel;
import ru.iisuslik.cards.Debuff;
import ru.iisuslik.cards.Destroy;
import ru.iisuslik.cards.Heal;
import ru.iisuslik.cards.Tunnel;
import ru.iisuslik.cards.Watch;
import ru.iisuslik.controller.Controller;
import ru.tilacyn.saboteur.SaboteurApplication;

import static org.junit.Assert.*;

public class FieldTest {

    private Field getNewField() {
        return Field.getFieldForTests(getController());
    }

    private Field field;

    private Controller getController() {
        return SaboteurApplication.getInstance().getController();
    }

    private Tunnel full, right, left, deadEnd;

    private int I = Field.ENTRY_POS_I;
    private int J = Field.ENTRY_POS_J;

    public FieldTest() throws Exception {
        getController();
        SaboteurApplication.getInstance().tunnels = tunnels;
        SaboteurApplication.getInstance().actions = actions;
        field = getNewField();
        initializeTunnels();
    }

    @Test
    public void putTunnel() throws Exception {
        field = getNewField();
        assertFalse(field.canPutTunnel(full, I, J));
        assertFalse(field.canPutTunnel(right, I + 1, J));
        assertTrue(field.canPutTunnel(right, I - 1, J));
        field.putTunnel(right, I - 1, J);
        assertTrue(field.field[I - 1][J] != null);
    }

    @Test
    public void putNotConnectedTunnel() {
        field = getNewField();
        assertFalse(field.canPutTunnel(full, I + 5, J + 5));
        putTunnelWithAssert(right, I - 1, J);
        assertFalse(field.canPutTunnel(left, I - 1, J - 1));
    }

    @Test
    public void putManyTunnels() throws Exception {
        field = getNewField();
        field.putTunnel(right, I - 1, J);
        putTunnelWithAssert(right, I, J - 1);
        putTunnelWithAssert(left, I - 1, J + 1);
        assertFalse(field.canPutTunnel(right, I, J - 1));
        assertFalse(field.canPutTunnel(right, I - 1, J - 1));
        printField(field);
    }

    @Test
    public void goToGold() throws Exception {
        field = getNewField();
        buildTheWayToGold();
        field.startDfs();
        assertTrue(!((ClosedTunnel) field.field[I - 8][J]).isClosed());
        assertTrue(!((ClosedTunnel) field.field[I - 8][J - 2]).isClosed());
        assertTrue(!((ClosedTunnel) field.field[I - 8][J + 2]).isClosed());
        assertTrue(field.isThisTheEnd());
        printField(field);
    }

    @Test
    public void dfsIsUseful() throws Exception {
        field = getNewField();
        buildTheWayToGold();
        field.field[I - 2][J] = null;
        field.startDfs();
        assertFalse(field.isThisTheEnd());
    }

    @Test
    public void dfsAndDeadEnd() throws Exception {
        field = getNewField();
        buildTheWayToGold();
        field.field[I - 2][J] = null;
        putTunnelWithAssert(deadEnd, I - 2, J);
        field.startDfs();
        assertFalse(field.isThisTheEnd());
        printField(field);
    }

    @Test
    public void destroy() throws Exception {
        field = getNewField();
        buildTheWayToGold();
        Destroy d = new Destroy(new int[]{6, 1, 1, 1, 42}, field);
        d.setPlayerNumber(0);
        assertFalse(d.canPlay(I, J));
        assertFalse(d.canPlay(I - 8, J));
        assertTrue(d.canPlay(I - 1, J));
        d.play(I - 1, J);
        assertEquals(null, field.field[I - 1][J]);
        field.startDfs();
        assertFalse(field.isThisTheEnd());
    }

    @Test
    public void watch() throws Exception {
        field = getNewField();
        Watch w = new Watch(new int[]{1, 1, 1, 1, 41}, field);
        w.setPlayerNumber(0);
        assertFalse(w.canPlay(I, J));
        assertTrue(w.canPlay(I - 8, J));
        w.play(I - 8, J);//TODO
    }

    @Test
    public void debuffLamp() throws Exception {
        field = getNewField();
        initializeTunnels();
        Debuff breakLamp = getDebuffLamp();
        assertTrue(breakLamp.canPlay(0));
        breakLamp.play(0);
        assertTrue(field.players[0].isBrokenLamp());
    }

    @Test
    public void debuffPick() throws Exception {
        field = getNewField();
        initializeTunnels();
        Debuff breakPick = getDebuffPick();
        assertTrue(breakPick.canPlay(0));
        breakPick.play(0);
        assertTrue(field.players[0].isBrokenPick());
    }

    @Test
    public void debuffTrolley() throws Exception {
        field = getNewField();
        initializeTunnels();
        Debuff breakTrolley = getDebuffTrolley();
        assertTrue(breakTrolley.canPlay(0));
        breakTrolley.play(0);
        assertTrue(field.players[0].isBrokenTrolley());
    }

    @Test
    public void healLamp() throws Exception {
        field = getNewField();
        initializeTunnels();
        Heal healLamp = new Heal(new int[]{2, 1, 0, 0, 32}, field);
        healLamp.setPlayerNumber(0);
        assertFalse(healLamp.canPlay(0));
        field.players[0].breakIt(getDebuffLamp());
        assertTrue(healLamp.canPlay(0));
        healLamp.play(0);
        assertFalse(field.players[0].isBrokenLamp());
    }

    @Test
    public void healPick() throws Exception {
        field = getNewField();
        initializeTunnels();
        Heal healPick = new Heal(new int[]{2, 0, 1, 0, 32}, field);
        healPick.setPlayerNumber(0);
        assertFalse(healPick.canPlay(0));
        field.players[0].breakIt(getDebuffPick());
        assertTrue(healPick.canPlay(0));
        healPick.play(0);
        assertFalse(field.players[0].isBrokenPick());
    }

    @Test
    public void healTrolley() throws Exception {
        field = getNewField();
        initializeTunnels();
        Heal healTrolley = new Heal(new int[]{2, 0, 0, 1, 32}, field);
        healTrolley.setPlayerNumber(0);
        assertFalse(healTrolley.canPlay(0));
        field.players[0].breakIt(getDebuffTrolley());
        assertTrue(healTrolley.canPlay(0));
        healTrolley.play(0);
        assertFalse(field.players[0].isBrokenTrolley());
    }


    private Debuff getDebuffLamp() {
        Debuff breakLamp = new Debuff(new int[]{3, 1, 0, 0, 38}, field);
        breakLamp.setPlayerNumber(0);
        return breakLamp;
    }

    private Debuff getDebuffPick() {
        Debuff breakPick = new Debuff(new int[]{3, 0, 1, 0, 39}, field);
        breakPick.setPlayerNumber(0);
        return breakPick;
    }

    private Debuff getDebuffTrolley() {
        Debuff breakTrolley = new Debuff(new int[]{3, 0, 0, 1, 40}, field);
        breakTrolley.setPlayerNumber(0);
        return breakTrolley;
    }

    private void buildTheWayToGold() {
        for (int i = 1; i <= 7; i++) {
            putTunnelWithAssert(full, I - i, J);
        }

        putTunnelWithAssert(full, I - 7, J - 1);
        putTunnelWithAssert(full, I - 7, J + 1);
        putTunnelWithAssert(full, I - 7, J - 2);
        putTunnelWithAssert(full, I - 7, J + 2);
    }

    private void putTunnelWithAssert(Tunnel tunnel, int i, int j) {
        assertTrue(field.canPutTunnel(tunnel, i, j));
        field.putTunnel(tunnel, i, j);
    }

    private void printField(Field fieldClass) {
        Tunnel[][] field = fieldClass.field;
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] != null)
                    printTunnelUp(field[i][j]);
                else
                    System.out.print("   ");

            }
            System.out.println();
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] != null)
                    printTunnelMiddle(field[i][j]);
                else
                    System.out.print("   ");

            }
            System.out.println();
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] != null)
                    printTunnelDown(field[i][j]);
                else
                    System.out.print("   ");
            }
            System.out.println();
        }
    }

    private void printTunnelUp(Tunnel t) {
        System.out.print(" " + (t.up ? '#' : ' ') + ' ');
    }

    private void printTunnelDown(Tunnel t) {
        System.out.print(" " + (t.down ? '#' : ' ') + ' ');
    }

    private void printTunnelMiddle(Tunnel t) {
        System.out.print(t.left ? '#' : ' ');
        System.out.print(t.centre ? '@' : ' ');
        System.out.print(t.right ? '#' : ' ');
    }

    private void initializeTunnels() {
        full = new Tunnel(new int[]{1, 1, 1, 1, 1, 1}, field);
        full.setPlayerNumber(0);
        right = new Tunnel(new int[]{1, 0, 1, 0, 1, 1}, field);
        right.setPlayerNumber(0);
        left = new Tunnel(new int[]{1, 0, 1, 1, 0, 1}, field);
        left.setPlayerNumber(0);
        deadEnd = new Tunnel(new int[]{1, 1, 1, 1, 1, 0}, field);
        deadEnd.setPlayerNumber(0);
    }


    private int[][] tunnels =
            {{5, 1, 0, 1, 1, 1},
                    {4, 0, 0, 1, 1, 1},
                    {5, 1, 1, 0, 1, 1},
                    {5, 1, 1, 1, 1, 1},
                    {3, 1, 1, 0, 0, 1},
                    {4, 0, 1, 1, 0, 1},
                    {5, 1, 1, 0, 1, 1},
                    {1, 0, 0, 1, 0, 1},
                    {1, 1, 1, 1, 0, 0},
                    {1, 1, 0, 0, 0, 1},
                    {1, 1, 1, 1, 1, 0},
                    {1, 0, 1, 1, 1, 0},
                    {1, 1, 0, 1, 0, 0},
                    {1, 0, 1, 1, 0, 0},
                    {1, 0, 0, 1, 1, 0},
                    {1, 1, 1, 0, 0, 0},
                    {-1, 1, 1, 1, 1, 1},
                    {0, 1, 0, 0, 1, 1},
                    {0, 1, 0, 1, 0, 1},
                    {1, 1, 1, 1, 1, 1}};

    private int[][] actions =
            {{2, 1, 0, 0, 32},
                    {2, 0, 1, 0, 33},
                    {2, 0, 0, 1, 34},
                    {1, 1, 1, 0, 37},
                    {1, 0, 1, 1, 35},
                    {1, 1, 0, 1, 36},
                    {3, 1, 0, 0, 38},
                    {3, 0, 1, 0, 39},
                    {3, 0, 0, 1, 40},
                    {6, 1, 1, 1, 41},
                    {6, 1, 1, 1, 42}};

}