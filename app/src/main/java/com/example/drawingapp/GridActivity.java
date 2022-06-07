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

public class GridActivity extends AppCompatActivity{

    // change paint color
    private ImageButton homeBtn, saveBtn, noidBtn;

    public static Context context;

    private GridView gridView;

    static String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // interaction
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        context = this;

        title = MainActivity.title;
        gridView = (GridView)findViewById(R.id.gridCanvas);

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
                                gridView.startNew(title);
                                gridView.writeLog("no id setting and new canvas\n");
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
                                gridView.writeLog(gridView.getCurrentTime() + ": Grid Activity Over");
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
                                gridView.setDrawingCacheEnabled(true);
                                String imgSaved = MediaStore.Images.Media.insertImage(
                                        getContentResolver(), gridView.getDrawingCache(),
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
                                gridView.destroyDrawingCache();
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