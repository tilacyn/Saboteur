package ru.iisuslik.field;

import org.junit.Test;


import ru.iisuslik.cards.Destroy;
import ru.iisuslik.cards.Tunnel;
import ru.iisuslik.controller.Controller;
import ru.tilacyn.saboteur.SaboteurApplication;

import static org.junit.Assert.*;

/**
 * Created by iisus on 11.02.2018.
 */
public class FieldTest {

    private Field getNewField() {
        return Field.getFieldForTests(getController());
    }

    private Field field;

    private Controller getController() {
        return SaboteurApplication.getInstance().getController();
    }

    private Tunnel full, right, left;

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