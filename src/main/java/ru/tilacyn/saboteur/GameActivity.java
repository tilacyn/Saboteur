

package ru.tilacyn.saboteur;


import ru.iisuslik.controller.*;
import ru.iisuslik.player.*;
import ru.iisuslik.field.*;
import ru.iisuslik.cards.*;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.support.annotation.IntegerRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.x;
import static android.R.attr.y;

public class GameActivity extends AppCompatActivity {

    private int[] getCardImageById = new int[100];

    private void fillCardIdArray() {
        getCardImageById[0] = R.drawable.tunnel_pattern_0;
        getCardImageById[1] = R.drawable.tunnel_pattern_0;
        getCardImageById[2] = R.drawable.tunnel_pattern_2;
        getCardImageById[3] = R.drawable.tunnel_pattern_3;
        getCardImageById[4] = R.drawable.tunnel_pattern_4;
        getCardImageById[5] = R.drawable.tunnel_pattern_5;
        getCardImageById[7] = R.drawable.tunnel_pattern_7;
        getCardImageById[9] = R.drawable.tunnel_pattern_9;
        getCardImageById[11] = R.drawable.tunnel_pattern_11;
        getCardImageById[13] = R.drawable.tunnel_pattern_13;
        getCardImageById[6] = R.drawable.tunnel_pattern_6;
        getCardImageById[14] = R.drawable.tunnel_pattern_14;
        getCardImageById[16] = R.drawable.small_tunnel_pattern;
        getCardImageById[15] = R.drawable.tunnel_pattern_15;
        getCardImageById[10] = R.drawable.tunnel_pattern_10;
        getCardImageById[12] = R.drawable.tunnel_pattern_12;
        getCardImageById[8] = R.drawable.tunnel_pattern_8;
        getCardImageById[32] = R.drawable.card_32;
        getCardImageById[33] = R.drawable.card_33;
        getCardImageById[34] = R.drawable.card_34;
        getCardImageById[35] = R.drawable.card_35;
        getCardImageById[36] = R.drawable.card_36;
        getCardImageById[37] = R.drawable.card_37;
        getCardImageById[38] = R.drawable.card_38;
        getCardImageById[39] = R.drawable.card_39;
        getCardImageById[40] = R.drawable.card_40;
        getCardImageById[41] = R.drawable.card_41;
        getCardImageById[42] = R.drawable.card_42;
    }

    private int playerCount;
    private Controller controller = new Controller();
    private HashMap<ImageView, Pair<Integer, Integer>> map = new HashMap();
    private int[] xPos = {0};
    private int[] yPos = {0};
    private int fieldHeight;
    private int fieldWidth;

    private boolean isCardChosen = false;
    private Card chosenCard;


    public void drawTable(final TableLayout table, final TableRow.LayoutParams params) {
        table.removeAllViews();

        TableRow arkenstoneTableRow0 = new TableRow(this);
        arkenstoneTableRow0.setLayoutParams(params);
        arkenstoneTableRow0.setGravity(Gravity.CENTER_HORIZONTAL);


        for(int i = 0; i < fieldWidth + 2; i++) {
            ImageView empty_tunnel = new ImageView(this);
            empty_tunnel.setImageResource(R.drawable.arkenstone);
            arkenstoneTableRow0.addView(empty_tunnel);
        }

        table.addView(arkenstoneTableRow0);




        TableRow[] field = new TableRow[fieldHeight];

        for (int i = 0; i < fieldHeight; i++) {
            field[i] = new TableRow(this);
            field[i].setLayoutParams(params);
            field[i].setGravity(Gravity.CENTER_HORIZONTAL);
        }

        for (int i = 0; i < fieldHeight; i++) {
            ImageView arkenstone1 = new ImageView(this);
            arkenstone1.setImageResource(R.drawable.arkenstone);
            field[i].addView(arkenstone1);

            for (int j = 0; j < fieldWidth; j++) {
                final ImageView tunnel = new ImageView(this);
                Tunnel real = controller.getField()[i][j];
                if(real == null) {
                    tunnel.setImageResource(R.drawable.empty_tunnel);
                }
                else {
                    if(real.isClosedTunnel()) {
                        tunnel.setImageResource(R.drawable.hidden_tunnel);
                    }
                    else {
                        tunnel.setImageResource(getCardImageById[real.getId()]);
                    }
                }
                //tunnel.setImageResource(R.drawable.small_tunnel_pattern);
                //tunnel.setScaleX(0.3F);
                //tunnel.setScaleY(0.3F);

                field[i].addView(tunnel);

                tunnel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int x = (int) map.get(tunnel).first;
                        int y = (int) map.get(tunnel).second;
                        //System.out.println(x);
                        //System.out.println(y);
                        if(chosenCard instanceof Tunnel) {
                            //tunnel.setImageResource(getCardImageById[chosenCard.getId()]);
                            if(((Tunnel) chosenCard).canPlay(x, y)) {
                                ((Tunnel) chosenCard).play(x, y);
                                drawTable(table, params);
                            } else {
                                Toast.makeText(getApplicationContext(), "This field " + x + ' ' + y + " is unavailable", Toast.LENGTH_SHORT).show();

                            }
                        }
                        if(chosenCard instanceof Destroy) {
                            //tunnel.setImageResource(getCardImageById[chosenCard.getId()]);
                            if(((Destroy) chosenCard).canPlay(x, y)) {
                                ((Destroy) chosenCard).play(x, y);
                                drawTable(table, params);
                            }
                        }

                        if(chosenCard instanceof Watch) {
                            //tunnel.setImageResource(getCardImageById[chosenCard.getId()]);
                            if(((Watch) chosenCard).canPlay(x, y)) {
                                ((Watch) chosenCard).play(x, y);
                                drawTable(table, params);
                            }
                        }

                    }
                });


                map.put((ImageView) field[i].getVirtualChildAt(j + 1), new Pair<Integer, Integer>(i, j));
            }

            ImageView arkenstone2 = new ImageView(this);
            arkenstone2.setImageResource(R.drawable.arkenstone);
            field[i].addView(arkenstone2);


        }

        for (int i = 0; i < fieldHeight; i++) {
            table.addView(field[i]);
        }


        TableRow arkenstoneTableRow1 = new TableRow(this);
        arkenstoneTableRow1.setLayoutParams(params);
        arkenstoneTableRow1.setGravity(Gravity.CENTER_HORIZONTAL);

        TableRow emptyTableRow = new TableRow(this);
        emptyTableRow.setLayoutParams(params);
        emptyTableRow.setGravity(Gravity.CENTER_HORIZONTAL);


        for(int i = 0; i < fieldWidth + 2; i++) {
            ImageView arkenstone = new ImageView(this);
            arkenstone.setImageResource(R.drawable.arkenstone);
            arkenstoneTableRow1.addView(arkenstone);
        }

        for(int i = 0; i < fieldWidth + 2; i++) {
            ImageView empty_tunnel = new ImageView(this);
            empty_tunnel.setImageResource(R.drawable.nothing);
            emptyTableRow.addView(empty_tunnel);
        }

        table.addView(arkenstoneTableRow1);
        table.addView(emptyTableRow);

    }

    Context we() {
        return this;
    }


    void closeCards(TableRow cardsRow) {
        cardsRow.removeAllViews();
        for(int i = 0; i < 6; i++) {
            ImageButton empty = new ImageButton(this);
            empty.setImageResource(R.drawable.dark_side_of_the_moon);
            cardsRow.addView(empty);
        }
    }


    void makeYou(final LinearLayout overTables, final ImageView dwarf, Button you) {

        overTables.removeView(dwarf);
        overTables.removeView(you);

        you.setText("You: ");
        you.setTextSize(20);
        you.setWidth(30);
        you.setHeight(30);

        TextView yourNumber = new TextView(this);
        yourNumber.setText("Player " + ((Integer) (controller.currentPlayerNumber() + 1)).toString());
        yourNumber.setTextSize(30);
        yourNumber.setGravity(Gravity.CENTER_HORIZONTAL);
        yourNumber.setTextColor(Color.BLUE);


        you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSaboteur = controller.isCurrentPlayerSaboteur();

                if (!isSaboteur) {
                    dwarf.setImageResource(R.drawable.bread_winner);
                } else {
                    dwarf.setImageResource(R.drawable.saboteur);
                }
            }
        });

        dwarf.setImageResource(R.drawable.dark_side_of_the_moon);

        overTables.addView(you, 1);
        overTables.addView(yourNumber, 2);
        overTables.addView(dwarf, 3);

    }

    private static final int PLAYER_CHOOSE = 43;

    private int playerChoose = -1;


    int getPlayerChoose() {
        return playerChoose;
    }


    void choosePlayer() {
        Intent intent = new Intent(this, PlayerChooseActivity.class);
        intent.putExtra("playerCount", 2);
        startActivityForResult(intent, PLAYER_CHOOSE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLAYER_CHOOSE) {
            if(resultCode == RESULT_OK) {
                int playerChoose = data.getIntExtra("player", 0);//get player number
                Toast.makeText(getApplicationContext(), "Player " + Integer.toString(playerChoose),
                        Toast.LENGTH_SHORT).show();
                if(chosenCard instanceof Debuff) {
                    if(((Debuff) chosenCard).canPlay(playerChoose)) {
                        ((Debuff) chosenCard).play(playerChoose);
                    }
                }

                if(chosenCard instanceof Heal) {
                    if(((Heal) chosenCard).canPlay(playerChoose)) {
                        ((Heal) chosenCard).play(playerChoose);
                    }
                }
            }
            else {
                playerChoose = -1;
                Toast.makeText(getApplicationContext(), "Choose please ",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playerCount = getIntent().getIntExtra("playerCount", 2);

        controller.initializeField(playerCount);

        fillCardIdArray();

        fieldHeight = controller.getHeight();
        fieldWidth = controller.getWidth();


        // horizontal scroll view

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);

        // scroll view

        ScrollView scrollView = new ScrollView(this);

        scrollView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));

        //linear layout

        final LinearLayout overTables = new LinearLayout(this);

        overTables.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.FILL_PARENT, LinearLayoutCompat.LayoutParams.FILL_PARENT));

        LinearLayout overHorizont = new LinearLayout(this);

        overTables.setOrientation(LinearLayout.VERTICAL);

        /*
        15 x 18
         */

        final TableLayout table = new TableLayout(this);

        table.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        final TableRow.LayoutParams params = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);


        // global add section

        scrollView.addView(overHorizont);
        overHorizont.addView(horizontalScrollView);
        horizontalScrollView.addView(overTables, new HorizontalScrollView.LayoutParams(HorizontalScrollView.LayoutParams.WRAP_CONTENT,
                HorizontalScrollView.LayoutParams.WRAP_CONTENT));
        overTables.addView(table);


        //table.setStretchAllColumns(true);
        //table.setShrinkAllColumns(true);

        drawTable(table, params);


        //you, image


        final Button you = new Button(this);
        final ImageView dwarf = new ImageView(this);
        makeYou(overTables, dwarf, you);

        // Set cardsRow

        final Button cards = new Button(this);
        cards.setText("CARDS");
        cards.setTextSize(10);

        TableLayout cardsTable = new TableLayout(this);

        final TableRow cardsRow = new TableRow(this);

        cardsTable.addView(cards);
        cardsTable.addView(cardsRow);

        cardsRow.setGravity(Gravity.CENTER_HORIZONTAL);
        cardsRow.setLayoutParams(params);

        closeCards(cardsRow);

        // set cards.OnClickListener

        cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ArrayList<Card> playerCards = controller.getCurrentPlayerHand();


                for (int i = 0; i < playerCards.size(); i++) {
                    //final ImageView cardImage = (ImageView) cardsRow.getVirtualChildAt(i);
                    final ImageButton cardImage = (ImageButton) cardsRow.getVirtualChildAt(i);
                    cardImage.setImageResource(getCardImageById[playerCards.get(i).getId()]);
                    cardImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isCardChosen = true;
                            for(int i = 0; i < cardsRow.getVirtualChildCount(); i++) {
                                if(cardsRow.getVirtualChildAt(i).equals(cardImage)) {
                                    chosenCard = playerCards.get(i);
                                    break;
                                }
                            }
                            if(chosenCard instanceof Debuff) {
                                choosePlayer();

                            }

                            if(chosenCard instanceof Heal) {
                                choosePlayer();

                            }
                        }
                    });
                }
            }
        });

        // Cards Row added

        Button info = new Button(this);
        // info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
        // LinearLayout.LayoutParams.WRAP_CONTENT));
        info.setText("INFO");
        info.setTextSize(10);

        // switchPlayer button

        Button switchPlayer = new Button(this);
        switchPlayer.setText("SWITCH");
        switchPlayer.setTextSize(10);

        switchPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!controller.canStartNextTurn()) {
                    Toast.makeText(getApplicationContext(), "Your turn still! Don't fraud!", Toast.LENGTH_SHORT).show();
                    return;
                }
                controller.startNextTurn();
                makeYou(overTables, dwarf, you);
                closeCards(cardsRow);
            }
        });

        overTables.setBackgroundColor(Color.parseColor("#21161141"));

        // overTables add

//        overTables.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.FILL_PARENT, LinearLayoutCompat.LayoutParams.FILL_PARENT));
        //      overTables.setGravity(Gravity.CENTER);

        overTables.addView(cardsTable);
        overTables.addView(info);
        overTables.addView(switchPlayer);

        setContentView(scrollView);

    }

}
