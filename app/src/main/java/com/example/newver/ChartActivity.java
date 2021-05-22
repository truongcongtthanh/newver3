package com.example.newver;

import java.time.LocalDateTime;  // Import the LocalDateTime class
import java.time.format.DateTimeFormatter;  // Import the DateTimeFormatter class
import java.util.Date;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
public class ChartActivity extends AppCompatActivity {

    LineChartView lineChartView;
    public DatabaseReference cdatabase = FirebaseDatabase.getInstance().getReference();
    public boolean flag = true;
    int n = 10;// số data muốn hiển thị
    public static  final String TAG = MainActivity.class.getSimpleName();
    Date d = new Date();
    int minutes = d.getMinutes();
    int hours = d.getHours();

    ArrayList<String> ArrMins = new ArrayList<String>();
    ArrayList<String> ArrHours = new ArrayList<String>();
    ArrayList<String> ArrTimes = new ArrayList<String>();

    String[] axisData = new String[n];
    float[] yAxisData = new float[n];
    Date date = new Date();
    int s = date.getMinutes();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);

        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = myDateObj.format(myFormatObj);

        ArrMins.add(String.valueOf(minutes));
        ArrHours.add(String.valueOf(hours));
        ArrTimes.add(String.valueOf(hours)+":"+String.valueOf(minutes));

        for(int i = 0; i<n;i++) {
            if (minutes<15) {
                minutes = 60 + minutes - 15;
                ArrMins.add(String.valueOf(minutes));
                hours = hours - 1;
                if(hours==-1)
                    hours = 23;
                ArrHours.add(String.valueOf(hours));
                ArrTimes.add(String.valueOf(hours)+":"+String.valueOf(minutes));


            }
            else {
                minutes = minutes - 15;
                ArrMins.add(String.valueOf(minutes));
                ArrHours.add(String.valueOf(hours));
                ArrTimes.add(String.valueOf(hours)+":"+String.valueOf(minutes));

            }
        }
        for (int i = 0; i<axisData.length;i++){
            axisData[i]=ArrTimes.get(n-1-i);
        }
        ValueEventListener data_y = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String parent = snapshot.getKey();
                Object obj = snapshot.getValue(Object.class);
                String json = new Gson().toJson(obj);
                try{
                    String input = json;
                    JSONObject jsonObj = new JSONObject(input);
                    Iterator a = jsonObj.keys();
                    JSONArray Arr = new JSONArray();
                    while (a.hasNext()){
                        String K = (String) a.next();
                        Arr.put(jsonObj.get(K));
                    }

                    float max =0;
                    float min=100;
                    for(int i= 0; i<Arr.length();i++){
                        String lst = (String) Arr.get(i);
                        yAxisData[i]=Float.parseFloat(lst);
                        if(max<yAxisData[i])
                            max = yAxisData[i];
                        if(i==0) {
                            min = yAxisData[i];
                        }
                        else{
                            if (min > yAxisData[i])
                                min = yAxisData[i];
                        }
                    }

                    lineChartView = findViewById(R.id.line_chart);

                    List yAxisValues = new ArrayList();
                    List axisValues = new ArrayList();


                    Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

                    for (int i = 0; i < axisData.length; i++) {
                        axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
                    }

                    for (int i = 0; i < yAxisData.length; i++) {
                        yAxisValues.add(new PointValue(i, yAxisData[i]));
                    }

                    List lines = new ArrayList();
                    lines.add(line);

                    LineChartData data = new LineChartData();
                    data.setLines(lines);

                    Axis axis = new Axis();
                    axis.setValues(axisValues);
                    axis.setTextSize(16);
                    if(parent == "Temperature") {
                        axis.setName("Biểu đồ nhiệt độ ngày: " + formattedDate);
                    }
                    if(parent == "Humidity") {
                        axis.setName("Biểu đồ độ ẩm ngày: " + formattedDate);
                    }
                    axis.setTextColor(Color.parseColor("#03A9F4"));
                    data.setAxisXBottom(axis);

                    Axis yAxis = new Axis();
                    yAxis.setName(" ");
                    yAxis.setTextColor(Color.parseColor("#FD9A1F"));
                    yAxis.setTextSize(16);



                    data.setAxisYLeft(yAxis);

                    lineChartView.setLineChartData(data);
                    Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
                    viewport.top = max;
                    viewport.bottom=min-1;
                    lineChartView.setMaximumViewport(viewport);
                    lineChartView.setCurrentViewport(viewport);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        cdatabase.child("DHT22").child("Temperature").limitToLast(n).addValueEventListener(data_y);
        Button btn_DoAm = (Button)findViewById(R.id.doAm);
        btn_DoAm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=false;
                cdatabase.child("DHT22").child("Humidity").limitToLast(n).addValueEventListener(data_y);
            }
        });

        Button btn_NhietDo = (Button)findViewById(R.id.nhietDo);
        btn_NhietDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = true;
                cdatabase.child("DHT22").child("Temperature").limitToLast(n).addValueEventListener(data_y);
            }
        });


        Button btn_ThongKe = (Button)findViewById(R.id.thongke);
        btn_ThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.statistics);


//                Intent intent = new Intent(ChartActivity.this,statistics.class);
//                startActivity(intent);

                class thongKe {
                    public DatabaseReference cdatabase = FirebaseDatabase.getInstance().getReference();
                    TextView text1;
                    TextView text2;
                    TextView text3;
                    TextView textView1;
                    TextView textView2;
                    TextView textView3;
                    Button btnX;
                    public void Thanh(int n,String txt,int btn_color) {
                        float[] yAxisData = new float[n];
                        ValueEventListener val = new ValueEventListener() {


                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                text1 = findViewById(R.id.txt1);
                                text2 = findViewById(R.id.txt2);
                                text3 = findViewById(R.id.txt3);
                                textView1 = findViewById(R.id.textView1);
                                textView2 = findViewById(R.id.textView2);
                                textView3 = findViewById(R.id.textView3);
                                if(btn_color == 1) {
                                    btnX = findViewById(R.id.day);
                                }
                                else if(btn_color == 2){
                                    btnX = findViewById(R.id.week);
                                }
                                else{
                                    btnX = findViewById(R.id.month);
                                }
                                findViewById(R.id.day).setBackgroundResource(R.drawable.frame15);
                                findViewById(R.id.week).setBackgroundResource(R.drawable.frame15);
                                findViewById(R.id.month).setBackgroundResource(R.drawable.frame15);

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

                                    for (int i = 0; i < yAxisData.length; i++) {
                                        String lst = (String) Arr.get(i);
                                        yAxisData[i] = Float.parseFloat(lst);
                                        if(min>yAxisData[i])
                                            min = yAxisData[i];
                                        if(max<yAxisData[i])
                                            max = yAxisData[i];
                                        avg+=yAxisData[i];
                                        if(i==yAxisData.length - 1)
                                            avg /=yAxisData.length;
                                    }

                                    //Log.d(TAG,"dep trai 1: "+String.valueOf(yAxisData[0]));

                                    text1.setText(String.valueOf((float)Math.round(avg*100)/100));
                                    text2.setText(String.valueOf((float)Math.round(max*100)/100));
                                    text3.setText(String.valueOf((float)Math.round(min*100)/100));
                                    btnX.setBackgroundColor(Color.RED);
                                    if(txt == "Humidity"){
                                        textView1.setText("Độ ẩm trung bình");
                                        textView2.setText("Độ ẩm cao nhất");
                                        textView3.setText("Độ ẩm thấp nhất");
                                    }
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

                thongKe tt;
                tt = new thongKe();
               // String txt = "Temperature";
                //tt.Thanh(24,txt);
                tt.Thanh(24,"Temperature",1);
                if (flag) {
                    tt.Thanh(24,"Temperature",1);
                    Button btn1 = (Button) findViewById(R.id.day);

                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tt.Thanh(24, "Temperature",1);
                        }
                    });
                    Button btn2 = (Button) findViewById(R.id.week);
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tt.Thanh(50, "Temperature",2);
                        }
                    });
                    Button btn3 = (Button) findViewById(R.id.month);
                    btn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tt.Thanh(100, "Temperature",3);
                        }
                    });
                }
                else{
                    tt.Thanh(24,"Humidity",1);
                    Button btn1 = (Button) findViewById(R.id.day);

                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tt.Thanh(24, "Humidity",1);
                        }
                    });
                    Button btn2 = (Button) findViewById(R.id.week);
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tt.Thanh(50, "Humidity",2);
                        }
                    });
                    Button btn3 = (Button) findViewById(R.id.month);
                    btn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tt.Thanh(100, "Humidity",3);
                        }
                    });
                }
            }
        });
//        ImageButton btn_Back_statistics = (ImageButton) findViewById(R.id.back_statistics);
//        btn_Back_statistics.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intentX = new Intent(ChartActivity.this,ChartActivity.class);
//                startActivity(intentX);
//            }
//        });

    }


}
