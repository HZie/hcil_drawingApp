package com.example.drawingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Context context;
    TextInputEditText textInput;
    RadioGroup rg;
    Button btn_grid, btn_draw;
    String pid, shape;
    static String title;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        textInput = (TextInputEditText) findViewById(R.id.input_pid);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        btn_grid = (Button) findViewById(R.id.btn_toGrid);
        btn_draw = (Button) findViewById(R.id.btn_toDrawing);


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton checkedBtn = (RadioButton) findViewById(checkedId);
                shape = checkedBtn.getText().toString();
            }
        });

        btn_draw.setOnClickListener(this);
        btn_grid.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        pid = textInput.getText().toString();
        if(pid == null || pid.compareTo("") == 0 || shape == null ){
            Log.d("63 line", "pid: " + pid +", shape: " + shape);
            Toast.makeText(context, "Missing something" , Toast.LENGTH_SHORT).show();
            return;
        }
        title = pid+": "+shape;
        Intent intent = null;
        switch(view.getId()){
            case R.id.btn_toDrawing:
                writeLog(title, getCurrentTime()+ ": Drawing Activity Start");
                intent = new Intent(context, DrawingActivity.class);
                break;
            case R.id.btn_toGrid:
                writeLog(title, getCurrentTime()+ ": Grid Activity Start");
                intent = new Intent(context, GridActivity.class);
                break;
            default:
                break;
        }
        if(intent != null){
            startActivity(intent);
        }
    }

    public static void writeLog(String title, String msg) {
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
                bfw.write(msg);
                bfw.write("\n\n");
                bfw.flush();
                bfw.close();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }
        }
    }

    public static String getCurrentTime(){

        Calendar calendar = Calendar.getInstance();
        String mtime = String.format("%d:%d:%d:%d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                calendar.get(Calendar.MILLISECOND));

        return mtime;
    }

    public boolean onKeyDown(int keycode, KeyEvent event){
        if(keycode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return false;
    }
}
