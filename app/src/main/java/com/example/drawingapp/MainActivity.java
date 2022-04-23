package com.example.drawingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // change paint color
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;

    // choosing brush
    private float smallBrush, mediumBrush, largeBrush;

    public static Context context;

    private DrawingView drawView;

    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // interaction
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        drawView = (DrawingView)findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        // creating new canvas
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        // saving canvas
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);


    }

    public void paintClicked(View view){

        if(view!=currPaint){
//update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);

            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;

        }
    }


    @Override
    public void onClick(View view){
//respond to clicks
        if(view.getId()==R.id.new_btn){
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.newfile_dialog);
            dialog.show();

            EditText pid = dialog.findViewById(R.id.pid_condition);
            RadioGroup rg = dialog.findViewById(R.id.rbtn_group);

            Button nextBtn = dialog.findViewById(R.id.btn_next);
            nextBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    RadioButton rb = dialog.findViewById(rg.getCheckedRadioButtonId());
                    title = pid.getText().toString() + "-"+rb.getText().toString();
                    drawView.startNew(title);
                    dialog.dismiss();
                }
            });

            Button cancelBtn = dialog.findViewById(R.id.btn_cancel);
            cancelBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    dialog.dismiss();
                }
            });

            //new button
            /*
            View newfileDialog = getLayoutInflater().inflate(R.layout.newfile_dialog, null);
            final EditText pidCond = (EditText)newfileDialog.findViewById(R.id.pid_condition);
            final RadioGroup shapeGroup = (RadioGroup) newfileDialog.findViewById(R.id.rbtn_group);

            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setView(R.layout.newfile_dialog);
            newDialog.setTitle("New drawing");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    System.out.println(pidCond.getText().toString());
                    title = pidCond.getText().toString() + shapeGroup.getCheckedRadioButtonId();

                    drawView.startNew(title);
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();*/
        }
        else if(view.getId()==R.id.save_btn){
            //save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Saved");
            saveDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    //save drawing
                }
            });
            saveDialog.show();
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
    }
}