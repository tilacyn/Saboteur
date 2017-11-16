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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_choose);
        int playerCount = getIntent().getIntExtra("playerCount", 0);
        ListView lv = (ListView) findViewById(R.id.list);
        String[] players = new String[playerCount];
        for(int i = 0; i < playerCount; i++){
            players[i] = "Player " + Integer.toString(i + 1);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, players);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Intent res = new Intent();
                res.putExtra("player", position);
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