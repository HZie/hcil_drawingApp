package com.example.drawingapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class DrawingView extends View {

    public static final int MAX_FINGERS = 2;
    private Path[] mFingerPaths = new Path[MAX_FINGERS];
    private Paint mFingerPaint, canvasPaint;
    private ArrayList<Path> mCompletedPaths;
    private RectF mPathBounds = new RectF();


    private int count = 0;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    // sound playing
    MediaPlayer mediaplayer;
    // log file title
    static String title = "init";

    public DrawingView(Context context) {
        super(context);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mCompletedPaths = new ArrayList<Path>();
        mFingerPaint = new Paint();
        mFingerPaint.setAntiAlias(true);
        mFingerPaint.setColor(Color.BLACK);
        mFingerPaint.setStyle(Paint.Style.STROKE);
        mFingerPaint.setStrokeWidth(20);
        mFingerPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
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
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

        for (Path completedPath : mCompletedPaths) {
            canvas.drawPath(completedPath, mFingerPaint);
        }

        for (Path fingerPath : mFingerPaths) {
            if (fingerPath != null) {
                canvas.drawPath(fingerPath, mFingerPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        int cappedPointerCount = pointerCount > MAX_FINGERS ? MAX_FINGERS : pointerCount;
        int actionIndex = event.getActionIndex();
        int action = event.getActionMasked();
        int id = event.getPointerId(actionIndex);

        if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) && id < MAX_FINGERS) {
            playSound(true);
            count++;
            MainActivity.writeLog(getCurrentTime()+" - pointer["+id+"]: ACTION_DOWN");
            mFingerPaths[id] = new Path();
            mFingerPaths[id].moveTo(event.getX(actionIndex), event.getY(actionIndex));
            MainActivity.writeLog(" - (" +event.getX(actionIndex) + ", " + event.getY(actionIndex) +")\n");

        }
        else if ((action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP) && id < MAX_FINGERS) {
            count--;
            MainActivity.writeLog(getCurrentTime()+" - pointer["+id+"]: ACTION_UP");
            mFingerPaths[id].setLastPoint(event.getX(actionIndex), event.getY(actionIndex));
            MainActivity.writeLog(" - (" +event.getX(actionIndex) + ", " + event.getY(actionIndex) +")\n");
            mCompletedPaths.add(mFingerPaths[id]);
            mFingerPaths[id].computeBounds(mPathBounds, true);
            invalidate((int) mPathBounds.left, (int) mPathBounds.top,
                    (int) mPathBounds.right, (int) mPathBounds.bottom);
            mFingerPaths[id] = null;
        }

        if(count == 0)
            playSound(false);

        for(int i = 0; i < cappedPointerCount; i++) {
            if(mFingerPaths[i] != null) {
                int index = event.findPointerIndex(i);
                mFingerPaths[i].lineTo(event.getX(index), event.getY(index));
                mFingerPaths[i].computeBounds(mPathBounds, true);
                invalidate((int) mPathBounds.left, (int) mPathBounds.top,
                        (int) mPathBounds.right, (int) mPathBounds.bottom);
                MainActivity.writeLog("pointer["+i+"]," + getCurrentTime() +" - (" +event.getX(actionIndex) + ", " + event.getY(actionIndex) +")\n");
            }
        }

        return true;
    }

    public void startNew(String title){
        this.title = title;
// reset the canvas
        Paint TransparentPaint = new Paint();
        TransparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        drawCanvas.drawRect(0, 0, drawCanvas.getWidth(), drawCanvas.getHeight(), TransparentPaint);
        MainActivity.writeLog(title);

        mCompletedPaths.clear();

        invalidate();
    }

    public void playSound(boolean play){
        if(play){
            if(mediaplayer == null){
                mediaplayer = MediaPlayer.create(getContext(), R.raw.writing);
                mediaplayer.setLooping(true);
                mediaplayer.start();
            }
        }
        else{
            if(mediaplayer != null){
                mediaplayer.stop();
                mediaplayer = null;
            }
        }
    }
/*
    public void writeLog(String str) {
        String folder = "drawing_log";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/"+folder;
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }

        if(title == null || title.compareTo("init") == 0)
        {
            Toast.makeText(this.getContext(), "failed writing log: No title", Toast.LENGTH_SHORT);
            return;
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
        Log.d("data", "write log success");
    }
*/
    public static String getCurrentTime(){
        String mtime = null;

        Calendar calendar = Calendar.getInstance();
        mtime = String.format("%d:%d:%d:%d", 
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                calendar.get(Calendar.MILLISECOND));

        return mtime;
    }

}

