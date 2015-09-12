package com.example.Movement;

import android.content.Context;
import android.graphics.*;
import android.util.DisplayMetrics;
import android.view.*;

import java.util.ArrayList;

/**
 * Created by ivan on 12.07.2015.
 */
public class MovementView extends SurfaceView implements SurfaceHolder.Callback {
    private int yVel;

    private int width;
    private int height;

    private int xStartPos;
    private int yStartPos;
    private Paint digitPaint;

    static UpdateThread updateThread;
    ArrayList<Digit> digits = new ArrayList();
    Digit currentDigit;
    public static Bitmap[] bitmaps;
    int[][] field;
    int firstX;
    int secondX;
    int targetSum = 10;

    public MovementView(Context context) {
        super(context);
        getHolder().addCallback(this);
        getBitmaps();
        init();
    }


    void init() {
        xStartPos = bitmaps[1].getWidth() * 6;
        yStartPos = 0;
        currentDigit = new Digit(xStartPos, yStartPos);
        digits.add(currentDigit);
        digitPaint = new Paint();
        yVel = bitmaps[1].getWidth();
        field = new int[13][13];
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                field[i][j] = 0;
                if (i == 0 || i == 12 || j == 12) field[i][j] = 20;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GRAY);
        digitPaint.setColor(Color.CYAN);
        canvas.drawRect(bitmaps[1].getWidth(), 0, bitmaps[1].getWidth() * 12, bitmaps[1].getWidth() * 12, digitPaint);
        for (Digit digit : digits) {
            canvas.drawBitmap(digit.bitmap, digit.x, digit.y, digitPaint);
        }
        digitPaint.setColor(Color.BLACK);
        digitPaint.setTextSize(bitmaps[1].getWidth());
//        canvas.drawText("Score", 400, 50, digitPaint);
    }

    public void updatePhysics() {
        if (field[currentDigit.x / bitmaps[1].getWidth()][currentDigit.y / bitmaps[1].getWidth() + 1] == 0) {
            moveDown(currentDigit);
        } else {
            field[currentDigit.x / bitmaps[1].getWidth()][currentDigit.y / bitmaps[1].getWidth()] = currentDigit.value;
            checkSumAndErase();
            currentDigit = new Digit(xStartPos, yStartPos);
            digits.add(currentDigit);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {

        Rect surfaceFrame = holder.getSurfaceFrame();
        width = surfaceFrame.width();
        height = surfaceFrame.height();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        firstX = (int) event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        secondX = (int) event.getX();
                        if (secondX > firstX) moveRight();
                        else moveLeft();
                }
                return true;
            }
        });

        currentDigit.x = xStartPos;
        currentDigit.y = yStartPos;

        updateThread = new UpdateThread(this);
        updateThread.setRunning(true);
        updateThread.start();
    }

    private void moveLeft() {
        if (field[currentDigit.x / bitmaps[1].getWidth() - 1][currentDigit.y / bitmaps[1].getWidth()] == 0) currentDigit.x -= bitmaps[1].getWidth();
    }

    private void moveRight() {

        if (field[currentDigit.x / bitmaps[1].getWidth() + 1][currentDigit.y / bitmaps[1].getWidth()] == 0) currentDigit.x += bitmaps[1].getWidth();
    }

    private void moveDown(Digit digit) {
        digit.y += yVel;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;

        updateThread.setRunning(false);
        while (retry) {
            try {
                updateThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    private void checkSumAndErase() {
        int sum;
        int beginBlock = 0;
        int endBlock = 0;
        for (int i = currentDigit.x / bitmaps[1].getWidth(); i > 0; i--) {
            sum = 0;
            for (int j = i; j < 12; j++) {
                if (field[j][currentDigit.y / bitmaps[1].getWidth()] != 0) {
                    sum += field[j][currentDigit.y / bitmaps[1].getWidth()];
                    if (sum == targetSum) {
                        beginBlock = i;
                        endBlock = j;
                        erase(beginBlock, endBlock);
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        for (int i = currentDigit.x / bitmaps[1].getWidth(); i < 12; i++) {
            sum = 0;
            for (int j = i; j > 0; j--) {
                if (field[j][currentDigit.y / bitmaps[1].getWidth()] != 0) {
                    sum += field[j][currentDigit.y / bitmaps[1].getWidth()];
                    if (sum == targetSum) {
                        beginBlock = j;
                        endBlock = i;
                        erase(beginBlock, endBlock);
                        break;
                    }
                } else {
                    break;
                }
            }
        }

    }

    void erase(int beginBlock, int endBlock) {
        if (beginBlock != 0 && endBlock != 0) {
            for (int i = currentDigit.y / bitmaps[1].getWidth(); i > 0; i--) {
                for (int j = beginBlock; j < endBlock + 1; j++) {
                    field[j][i] = field[j][i - 1];
                }
            }
            for (int i = 0; i < digits.size(); i++) {
                if (digits.get(i).x / bitmaps[1].getWidth() >= beginBlock && digits.get(i).x / bitmaps[1].getWidth() <= endBlock && digits.get(i).y / bitmaps[1].getWidth() == currentDigit.y / bitmaps[1].getWidth()) {
                    digits.remove(i);
                    i--;
                }
            }
            for (Digit digit : digits) {
                if (digit.x / bitmaps[1].getWidth() >= beginBlock && digit.x / bitmaps[1].getWidth() <= endBlock && digit.y / bitmaps[1].getWidth() < currentDigit.y / bitmaps[1].getWidth()) {
                    moveDown(digit);
                }
            }
        }
    }

    private void getBitmaps() {
        bitmaps = new Bitmap[10];
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int widthPixels = displaymetrics.widthPixels;
        int heightPixels = displaymetrics.heightPixels;
//        System.out.println(widthPixels + "   " + heightPixels);
        bitmaps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.one);
        bitmaps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.two);
        bitmaps[3] = BitmapFactory.decodeResource(getResources(), R.drawable.three);
        bitmaps[4] = BitmapFactory.decodeResource(getResources(), R.drawable.four);
        bitmaps[5] = BitmapFactory.decodeResource(getResources(), R.drawable.five);
        bitmaps[6] = BitmapFactory.decodeResource(getResources(), R.drawable.six);
        bitmaps[7] = BitmapFactory.decodeResource(getResources(), R.drawable.seven);
        bitmaps[8] = BitmapFactory.decodeResource(getResources(), R.drawable.eight);
        bitmaps[9] = BitmapFactory.decodeResource(getResources(), R.drawable.nine);
    }
}
