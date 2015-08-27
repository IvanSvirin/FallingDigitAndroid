package com.example.Movement;

import android.graphics.Bitmap;

import java.util.Random;

/**
 * Created by ivan on 13.07.2015.
 */
public class Digit {
    Bitmap bitmap;
    int value;
    int x;
    int y;

    public Digit(int x, int y) {
        this.value = (new Random().nextInt(9) + 1);
        this.bitmap = MovementView.bitmaps[value];
        this.x = x;
        this.y = y;
    }

}
