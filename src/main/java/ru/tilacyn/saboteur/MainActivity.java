package ru.tilacyn.saboteur;


//import ru.iisuslik.*;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat.*;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*
 <ImageView
        android:layout_height="495dp"
        android:layout_width="0dp"
        android:id="@+id/icon"
        android:src="@drawable/dwarf"
        android:scaleType="fitEnd"
        tools:layout_editor_absoluteY="36dp"
        tools:layout_editor_absoluteX="96dp" />
*/
        LinearLayout l = new LinearLayout(this);


        TextView name = new TextView(this);
        name.setText("Saboteur");
        name.setTextSize(48);
        name.setGravity(Gravity.CENTER);


        ImageView dwarf = new ImageView(this);
        dwarf.setImageResource(R.drawable.dwarf);
        dwarf.setScaleType(ImageView.ScaleType.FIT_END);


        dwarf.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        dwarf.setAdjustViewBounds(true);
        dwarf.setMaxHeight(250);
        dwarf.setMaxWidth(250);

        Button play = new Button(this);
        play.setText("PLAY");
        play.setTextSize(10);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });



        Button help = new Button(this);
        help.setText("HELP");
        help.setTextSize(10);


        Button settings = new Button(this);
        settings.setText("SETTINGS");
        settings.setTextSize(10);

        l.setOrientation(LinearLayout.VERTICAL);
        l.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        l.setGravity(Gravity.CENTER);
        l.addView(name);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.addView(help);
        l.addView(play);
        l.addView(settings);
        l.setOrientation(LinearLayout.VERTICAL);
        l.addView(dwarf);
        setContentView(l); // все готово, можно подключать компоновку

    }
}
