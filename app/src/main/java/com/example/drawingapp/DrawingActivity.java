package com.example.drawingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

// log type : date, timestamp, PID, condition, status, event, pointerId, x, y
public class DrawingActivity extends AppCompatActivity{

    // change paint color
    private ImageButton homeBtn, saveBtn, noidBtn;

    public static Context context;

    private DrawingView drawView;

    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // interaction
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        context = this;

        title = MainActivity.title;
        drawView = (DrawingView)findViewById(R.id.drawingCanvas);

        // just new canvas without id
        noidBtn = (ImageButton)findViewById(R.id.noid_btn);
        noidBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                String warningMsg = "Reset Canvas?";
                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context)
                        .setTitle(warningMsg)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                drawView.startNew(title);
                                MainActivity.writeLog(MainActivity.makeLogString("newDrawingCanvas", "null",-1,-1,-1));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                msgBuilder.show();

            }
        });

        // creating new canvas
        homeBtn = (ImageButton)findViewById(R.id.home_btn);
        homeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                String warningMsg = "Go Home?";
                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context)
                        .setTitle(warningMsg)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(context, MainActivity.class);
                                MainActivity.writeLog(MainActivity.makeLogString("DrawingOver", "null",-1,-1,-1));
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                msgBuilder.show();
            }
        });

        // saving canvas
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener(){
            //save drawing
            @Override
            public void onClick(View view){
                String warningMsg = "Save into Gallery?";
                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context)
                        .setTitle(warningMsg)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                drawView.setDrawingCacheEnabled(true);
                                String imgSaved = MediaStore.Images.Media.insertImage(
                                        getContentResolver(), drawView.getDrawingCache(),
                                        title, "drawing");
                                if(imgSaved!=null){
                                    Toast savedToast = Toast.makeText(getApplicationContext(),
                                            "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                                    savedToast.show();
                                }
                                else{
                                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                            "Image could not be saved.", Toast.LENGTH_SHORT);
                                    unsavedToast.show();
                                }
                                drawView.destroyDrawingCache();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                msgBuilder.show();
            }
        });



    }




}