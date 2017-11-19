package ru.tilacyn.saboteur;


//import ru.iisuslik.*;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat.*;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int playerCount;

    int buttonStyle;

    TableLayout playerCountTable;
    TableRow playerCountRow;

    boolean settingsOpen;

    ConstraintLayout screen;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        settingsOpen = false;

        playerCount = 2;

        buttonStyle = R.drawable.brown_button;

        screen = (ConstraintLayout) findViewById(R.id.activity_main);

        screen.setBackgroundResource(R.drawable.dwarf_lords);

        playerCountRow = new TableRow(this);
        playerCountTable = (TableLayout) findViewById(R.id.playerCountTable);

        //ImageView vlad = (ImageView) findViewById(R.id.vlad);
        //vlad.setImageResource(R.drawable.vlad);


        TextView name = (TextView) findViewById(R.id.Saboteur);
        name.setText("Saboteur");
        name.setTextSize(60);
        name.setTextColor(Color.YELLOW);
        name.setTypeface(Typeface.createFromAsset(getAssets(),"odessa.ttf"));



        Button play = (Button) findViewById(R.id.play);
        play.setText("PLAY");
        play.setTextSize(15);
        play.setTextColor(Color.WHITE);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("playerCount", playerCount);
                startActivity(intent);
            }
        });
        play.setBackgroundResource(buttonStyle);
        play.setTypeface(Typeface.createFromAsset(getAssets(),"comicbd.ttf"));



        Button help = (Button) findViewById(R.id.help);
        help.setText("HELP");
        help.setTextSize(15);
        help.setTextColor(Color.WHITE);
        help.setBackgroundResource(buttonStyle);
        help.setTypeface(Typeface.createFromAsset(getAssets(),"comicbd.ttf"));


        final Button settings = (Button) findViewById(R.id.settings);
        settings.setText("SETTINGS");
        settings.setTextSize(15);
        settings.setTextColor(Color.WHITE);
        settings.setBackgroundResource(buttonStyle);
        settings.setTypeface(Typeface.createFromAsset(getAssets(),"comicbd.ttf"));


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!settingsOpen) {
                    for(int i = 2; i < 8; i++) {
                        final Button b = new Button(MainActivity.this);
                        b.setWidth(30);
                        b.setBackgroundResource(buttonStyle);
                        b.setTypeface(Typeface.createFromAsset(getAssets(),"comicbd.ttf"));
                        b.setText(((Integer) i).toString());
                        b.setTextSize(15);
                        b.setTextColor(Color.WHITE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                playerCount = Character.getNumericValue(b.getText().charAt(0));
                            }
                        });
                        playerCountRow.addView(b);
                    }
                    playerCountTable.addView(playerCountRow);
                    settingsOpen = true;
                }
                else {
                    playerCountRow.removeAllViews();
                    playerCountTable.removeAllViews();
                    settingsOpen = false;
                }
            }
        });

        TextView lyrics = (TextView) findViewById(R.id.lyrics);
        lyrics.setText("Seven for the Dwarf-lords\nIn their halls of stone");
        lyrics.setTextColor(Color.YELLOW);
        lyrics.setTypeface(Typeface.createFromAsset(getAssets(),"odessa.ttf"));
        lyrics.setTextSize(30);
        lyrics.setGravity(Gravity.CENTER_HORIZONTAL);

    }
}
