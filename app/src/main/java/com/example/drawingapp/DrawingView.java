package com.example.drawingapp;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
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
    int max_pointer = 2;


    //drawing path
    private Path drawPath[] = new Path[max_pointer];
    // for multi touch
    int id[] = new int[max_pointer];
    int x[] = new int[max_pointer];
    int y[] = new int[max_pointer];


    // drawing single path
   // private Path drawPath;

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

        // multi touch
        for(int i = 0; i < max_pointer; i++){
            drawPath[i] = new Path();
        }


       // drawPath = new Path();

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


        // multitouch
        for(int i = 0; i < max_pointer; i++){
            canvas.drawPath(drawPath[i], drawPaint);
        }


//        canvas.drawPath(drawPath, drawPaint);

        setSound();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//detect user touch

        int pointer_count = event.getPointerCount();
        // max_pointer 수까지만 처리
        if(pointer_count > max_pointer) pointer_count = max_pointer;

        switch(event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                soundManager.playSound(0);
                for(int i = 0; i < pointer_count; i++){
                    writeLog(getCurrentTime()+" - pointer["+i+"]: ACTION_DOWN");
                    id[i] = event.getPointerId(i);
                    x[i] = (int)(event.getX(i));
                    y[i] = (int)(event.getY(i));
                    writeLog(getCurrentTime() +" - (" +x[i] + ", " + y[i] +")\n");
                    drawPath[i].moveTo(x[i],y[i]);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                for(int i =0; i < pointer_count; i++){
                    id[i] = event.getPointerId(i);
                    x[i] = (int)(event.getX(i));
                    y[i] = (int)(event.getY(i));
                    writeLog(getCurrentTime() +" - (" +x[i] + ", " + y[i] +")\n");
                    drawPath[i].lineTo(x[i],y[i]);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                soundManager.stopSound(0);
                Log.d("sound", soundManager.toString());
                for(int i = 0; i < pointer_count; i++){
                    writeLog(getCurrentTime()+" - pointer["+i+"]: ACTION_UP");
                    drawCanvas.drawPath(drawPath[i], drawPaint);
                    writeLog(getCurrentTime() +" - (" +x[i] + ", " + y[i] +")\n\n");
                    drawPath[i].reset();
                }
                break;
            default:
                return false;
        }


        /*
        // single touch
        float touchX = event.getX();
        float touchY = event.getY();
        String log = getCurrentTime() +" - (" +touchX + ", " + touchY +")\n";
        System.out.println(event.toString());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                soundManager.playSound(0);
                writeLog("ACTION_DOWN: ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("action", "action_move");
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                Log.d("action", "action_up");
                writeLog("ACTION_UP: ");
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                soundManager.stopSound(0);
                break;
            default:
                return false;
        }
                writeLog(log);


         */
        invalidate();
        return true;
    }


    public void startNew(String title){
        this.title = title;
// reset the canvas
        Paint TransparentPaint = new Paint();
        TransparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        drawCanvas.drawRect(0, 0, drawCanvas.getWidth(), drawCanvas.getHeight(), TransparentPaint);
        writeLog(title);
        invalidate();
        for(int i = 0; i < max_pointer; i++){
            drawPath[i].reset();
        }
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
        String folder = "drawing_log";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/"+folder;
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(path+"/"+title+".txt");
        if (file.exists() == false) {
            try {
                file.createNewFile();
            } catch (IOException e) {
            }
        }
        if(file.exists()!= false){
            try {
                BufferedWriter bfw = new BufferedWriter(new FileWriter(path+"/"+title+".txt",true));
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

