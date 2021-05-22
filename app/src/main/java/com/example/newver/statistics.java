package com.example.newver;

import java.time.LocalDateTime;  // Import the LocalDateTime class
import java.time.format.DateTimeFormatter;  // Import the DateTimeFormatter class
import java.util.Date;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.newver.ChartActivity;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class statistics extends AppCompatActivity {

    public static  final String TAG = MainActivity.class.getSimpleName();


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        class thongKe {
            public DatabaseReference cdatabase = FirebaseDatabase.getInstance().getReference();
            TextView textView1;
            TextView textView2;
            TextView textView3;
            public void Thanh(int n,String txt) {
                float[] yAxisData = new float[n];
                ValueEventListener val = new ValueEventListener() {


                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        textView1 = findViewById(R.id.txt1);
                        textView2 = findViewById(R.id.txt2);
                        textView3 = findViewById(R.id.txt3);
                        Object obj = snapshot.getValue(Object.class);
                        String json = new Gson().toJson(obj);
                        try {
                            String input = json;
                            JSONObject jsonObj = new JSONObject(input);
                            Iterator a = jsonObj.keys();
                            JSONArray Arr = new JSONArray();
                            while (a.hasNext()) {
                                String K = (String) a.next();
                                Arr.put(jsonObj.get(K));
                            }
                            float avg=0;
                            float min=Arr.getInt(0)+1;
                            float max=Arr.getInt(0);

                            for (int i = 0; i < Arr.length(); i++) {
                                String lst = (String) Arr.get(i);
                                yAxisData[i] = Float.parseFloat(lst);
                                if(min>yAxisData[i])
                                    min = yAxisData[i];
                                if(max<yAxisData[i])
                                    max = yAxisData[i];
                                avg+=yAxisData[i];
                                if(i==Arr.length() - 1)
                                    avg /=Arr.length();
                            }

                            //Log.d(TAG,"dep trai 1: "+String.valueOf(yAxisData[0]));

                            textView1.setText(String.valueOf((float)Math.round(avg*100)/100));
                            textView2.setText(String.valueOf((float)Math.round(max*100)/100));
                            textView3.setText(String.valueOf((float)Math.round(min*100)/100));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                };
                cdatabase.child("DHT22").child(txt).limitToLast(n).addValueEventListener(val);



            }
    }
    ChartActivity chart = new ChartActivity();
    thongKe tt;
        tt = new thongKe();
        String txt = "Temperature";
        tt.Thanh(24,txt);
        if (chart.flag) {

            Button btn1 = (Button) findViewById(R.id.day);

            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tt.Thanh(24, "Temperature");
                }
            });
            Button btn2 = (Button) findViewById(R.id.week);
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tt.Thanh(24 * 7, "Temperature");
                }
            });
            Button btn3 = (Button) findViewById(R.id.month);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tt.Thanh(24 * 7 * 30, "Temperature");
                }
            });
        }
        else{
            Button btn1 = (Button) findViewById(R.id.day);

            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tt.Thanh(24, "Humidity");
                }
            });
            Button btn2 = (Button) findViewById(R.id.week);
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tt.Thanh(24 * 7, "Humidity");
                }
            });
            Button btn3 = (Button) findViewById(R.id.month);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tt.Thanh(24 * 7 * 30, "Humidity");
                }
            });
        }

    }
}