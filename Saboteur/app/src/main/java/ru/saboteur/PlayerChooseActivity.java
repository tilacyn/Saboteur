package ru.tilacyn.saboteur;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerChooseActivity extends AppCompatActivity {

    private int currentPlayerNumber;
    private int playerCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_choose);
        playerCount = getIntent().getIntExtra("playerCount", 2);
        currentPlayerNumber = getIntent().getIntExtra("currentPlayerNumber", 0);


        ListView lv = (ListView) findViewById(R.id.list);
        final String[] players = new String[playerCount + 1];
        //final int[] realNumbersList = new int[100];
        int realCount = 0;


        Toast.makeText(getApplicationContext(), Integer.toString(currentPlayerNumber),
                Toast.LENGTH_SHORT).show();

        players[0] = "kek";


        for (int i = 0; i < playerCount; i++) {
            players[i + 1] = "Player " + Integer.toString(i + 1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, players);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Intent res = new Intent();
                res.putExtra("player", Character.getNumericValue(players[position].charAt(7)) - 1);
                setResult(RESULT_OK, res);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent res = new Intent();
        res.putExtra("player", -1);
        setResult(RESULT_CANCELED, res);
    }
}