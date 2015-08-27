package com.example.Movement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by ivan on 12.07.2015.
 */
public class MainActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(new MovementView(this));
        setContentView(R.layout.main);
    }

    public void start(View view) {
        Intent game = new Intent(this, Game.class);
        startActivity(game);
    }
}
