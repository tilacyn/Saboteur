package ru.tilacyn.saboteur;


import ru.iisuslik.controller.*;
import ru.iisuslik.player.*;
import ru.iisuslik.field.*;
import ru.iisuslik.cards.*;

import android.graphics.Typeface;
import android.media.Image;
import android.support.annotation.IntegerRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class GameActivity extends AppCompatActivity {

    private int[] getCardImageById = new int[20];

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
        getCardImageById[16] = R.drawable.tunnel_pattern;
        getCardImageById[15] = R.drawable.tunnel_pattern_15;
        getCardImageById[10] = R.drawable.tunnel_pattern_10;
        getCardImageById[12] = R.drawable.tunnel_pattern_12;
        getCardImageById[8] = R.drawable.tunnel_pattern_8;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Controller controller = new Controller();

        final int[] xPos = {0};
        final int[] yPos = {0};

        controller.initializeField(2);

        fillCardIdArray();


        // horizontal scroll view

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);

        // scroll view


        ScrollView scrollView = new ScrollView(this);

        scrollView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));



        //linear layout

        LinearLayout ll = new LinearLayout(this);

        LinearLayout llKek = new LinearLayout(this);

        ll.setOrientation(LinearLayout.VERTICAL);

        /*
        15 x 18
         */

        TableLayout table = new TableLayout(this);

        table.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TableRow.LayoutParams params = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);


        // global add section

        scrollView.addView(llKek);
        llKek.addView(horizontalScrollView);
        horizontalScrollView.addView(ll, new HorizontalScrollView.LayoutParams(HorizontalScrollView.LayoutParams.WRAP_CONTENT,
                HorizontalScrollView.LayoutParams.WRAP_CONTENT));
        ll.addView(table);


        //params.span = 6;


        //table.setStretchAllColumns(true);
        //table.setShrinkAllColumns(true);

        int fieldHeight = 10;
        int fieldWidth = 13;


        TableRow[] field = new TableRow[fieldHeight];

        for (int i = 0; i < fieldHeight; i++) {
            field[i] = new TableRow(this);
            field[i].setLayoutParams(params);
            field[i].setGravity(Gravity.CENTER_HORIZONTAL);
        }


        final HashMap<ImageView, Pair<Integer, Integer>> map = new HashMap();

        for (int i = 0; i < fieldHeight; i++) {
            for (int j = 0; j < fieldWidth; j++) {
                final ImageView tunnel = new ImageView(this);
                tunnel.setImageResource(R.drawable.small_tunnel_pattern);
                //tunnel.setScaleX(0.3F);
                //tunnel.setScaleY(0.3F);

                tunnel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        xPos[0] = (int) map.get(tunnel).first;
                        yPos[0] = (int) map.get(tunnel).second;
                    }
                });

                field[i].addView(tunnel);


                map.put((ImageView) field[i].getVirtualChildAt(j), new Pair<Integer, Integer>(j, i));

            }
        }

        for (int i = 0; i < fieldHeight; i++) {
            table.addView(field[i]);
        }


        //Empty Table Row



        TableRow emptyTableRow1 = new TableRow(this);
        TableRow emptyTableRow2 = new TableRow(this);

        for(int i = 0; i < fieldWidth; i++) {
            ImageView empty_tunnel = new ImageView(this);
            empty_tunnel.setImageResource(R.drawable.empty_tunnel);
            emptyTableRow1.addView(empty_tunnel);
        }

        for(int i = 0; i < fieldWidth; i++) {
            ImageView empty_tunnel = new ImageView(this);
            empty_tunnel.setImageResource(R.drawable.empty_tunnel);
            emptyTableRow2.addView(empty_tunnel);
        }

        table.addView(emptyTableRow1);
        table.addView(emptyTableRow2);

        // Button decorations


        Button info = new Button(this);
        Button switchPlayer = new Button(this);
        final Button cards = new Button(this);

        info.setText("INFO");
        switchPlayer.setText("SWITCH");
        cards.setText("CARDS");

        info.setTextSize(10);
        switchPlayer.setTextSize(10);
        cards.setTextSize(10);

        info.setWidth(50);
        info.setHeight(25);

        switchPlayer.setWidth(50);
        switchPlayer.setHeight(25);

        cards.setMaxWidth(50);
        cards.setMaxHeight(25);

        //you, image


        Button you = new Button(this);
        you.setText("You: ");
        you.setTextSize(20);
        you.setWidth(30);
        you.setHeight(30);


        ll.addView(you);

        boolean isSaboteur = controller.isCurrentPlayerSaboteur();

        if (!isSaboteur) {
            ImageView dwarf = new ImageView(this);
            dwarf.setImageResource(R.drawable.bread_winner);
            ll.addView(dwarf);
        } else {
            ImageView dwarf = new ImageView(this);
            dwarf.setImageResource(R.drawable.saboteur);
            ll.addView(dwarf);
        }



        ll.addView(cards);
        ll.addView(info);
        ll.addView(switchPlayer);

        // Set cardsRow

        final TableRow cardsRow = new TableRow(this);

        cardsRow.setGravity(Gravity.CENTER_HORIZONTAL);
        cardsRow.setLayoutParams(params);

        for (int j = 0; j < 6; j++) {
            ImageView tunnel = new ImageView(this);
            tunnel.setImageResource(R.drawable.empty_tunnel);
            cardsRow.addView(tunnel);
        }
        table.addView(cardsRow);


        //set cards.OnClickListener

        cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ArrayList<Card> playerCards = controller.getCurrentPlayerHand();


                for (int i = 0; i < playerCards.size(); i++) {
                    ImageView cardImage = (ImageView) cardsRow.getVirtualChildAt(i);
                    cardImage.setImageResource(getCardImageById[playerCards.get(i).getId()]);
                    cardImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }

            }
        });


        // Cards Row added

        setContentView(scrollView);

    }

}
