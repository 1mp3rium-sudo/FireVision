package com.example.textrecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView mImageView;
    Button cameraBtn;
    Button detectBtn;
    Bitmap imageBitmap;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.mImageView);
        cameraBtn = findViewById(R.id.cameraButton);
        detectBtn = findViewById(R.id.detectButton);
        textView = findViewById(R.id.textView);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doNotificationDemo();
            }
        });
    }

    public void processData(String text){

    }

    private void detectImg() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        //textView.setText("hello");
        FirebaseVisionTextRecognizer textRecognizer =
                FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processTxt(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void runAlarmDemo(){
        String query = "Set an alarm for 6 am";
        testParser parse = new testParser(query);

        switch (parse.parseStr()){
            case 0:
                //do nothing
                break;
            case 1:
                doNotificationDemo();
                break;
            case 2:
                setAlarm(0, 0);
                break;
            case 3:
                //todo: Implement text to speech AI
                break;
            case 4:
                runTextDemo();
                break;
        }
    }

    private void runAlarm(String data){
        testParser parser = new testParser(data);

        if (parser.parseStr() == 2){
            setAlarm(0, 0);
        }
    }

    private void processNotification(String data){
        testParser parser = new testParser(data);
        if (parser.parseStr() == 1){
            postNotification("Reminder: ", data);
        }
    }

    private void processTextFile(String data){
        testParser parser = new testParser(data);
        if (parser.parseStr() == 4){
            writeToFile(data, getApplicationContext());
            Toast.makeText(getApplicationContext(), "File Saved Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void doNotificationDemo(){
        postNotification("Reminder: ", "Todo Friday Take Home Test");
    }

    private void postNotification(String title, String content){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "personal_notifications")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.notif_icon)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(001, builder.build());
    }



    private void runTextDemo(){
        String query = "-Do the laundry\n\n-Take out the trash\n\n-Study for physics final";

        writeToFile(query, getApplicationContext());
        String data = readFromFile(getApplicationContext());
        textView.setText(data);
        Toast.makeText(getApplicationContext(), "File Saved Successfully", Toast.LENGTH_SHORT).show();
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("input.txt", Context.MODE_PRIVATE));
            List<String> input = Arrays.asList(data.split("\n\n"));
            for (String in : input){
                outputStreamWriter.write(in);
            }
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("input.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void setAlarm(double hours, double minutes){
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hours);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes);

        if (hours <= 24 && minutes <= 60){
            startActivity(intent);
        }
    }

    public void runSystems(String data){
        processTextFile(data);
        runAlarm(data);
        processNotification(data);
    }

    private void processTxt(FirebaseVisionText text) {
        Log.w("MainActivity", text.getText());
        textView.setText(text.getText());
        runSystems(text.getText());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}