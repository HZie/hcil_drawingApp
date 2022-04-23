package com.example.drawingapp;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class DrawingView extends View {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    // choosing brush
    private float brushSize;


    // sound playing
    SoundPool soundPool;
    SoundManager soundManager;
    // playing sound
    boolean isSet = false;

    // log file title
    static String title = "init";

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();

    }

    private void setupDrawing(){
        // get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        brushSize = getResources().getInteger(R.integer.medium_size);
        drawPaint.setStrokeWidth(brushSize);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//view given size
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        setSound();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//detect user touch
        float touchX = event.getX();
        float touchY = event.getY();
        String log = getCurrentTime() +" - x: " +touchX + ", y: " + touchY +"\n";
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                soundManager.playSound(0);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                soundManager.stopSound(0);
                break;
            default:
                return false;
        }
        invalidate();
        writeLog(log);
        return true;
    }

    public void setColor(String newColor){
//set color
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void startNew(String title){
        this.title = title;
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        writeLog(title);
        invalidate();
    }

    public void setSound(){
        if(!isSet){
            // sound playing
            soundPool = new SoundPool.Builder().build();
            soundManager = new SoundManager(MainActivity.context, soundPool);
            soundManager.addSound(0,R.raw.writing);
            isSet = true;
        }
    }

    public static void writeLog(String str) {
        String str_Path_Full = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/download/"+title+".txt";
        File file = new File(str_Path_Full);
        if (file.exists() == false) {
            try {
                file.createNewFile();
            } catch (IOException e) {
            }
        }
        if(file.exists()!= false){
            try {
                BufferedWriter bfw = new BufferedWriter(new FileWriter(str_Path_Full,true));
                bfw.write(str);
                bfw.write("\n");
                bfw.flush();
                bfw.close();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }
        }
    }

    public static String getCurrentTime(){
        String mtime = null;

        Calendar calendar = Calendar.getInstance();
        mtime = String.format("%d-%d-%d %d:%d:%d", calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));

        return mtime;
    }

}

