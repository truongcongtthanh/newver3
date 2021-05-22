package com.example.newver;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.Query;
import com.google.gson.Gson;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView listViewTest;

    //Array Adapter that will hold our ArrayList and display the items on the ListView
    public DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
    public static  final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button clickButton = (Button) findViewById(R.id.button);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ChartActivity.class);
              startActivity(intent);
            }
        });

//        ValueEventListener setVal = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//            Boolean post = snapshot.exists();
//            if(post){
//                mdatabase.child("DHT22").child("Humadity");
//            }
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        };
//        ;mdatabase.child("DHT22").child("Temperature").limitToLast(5).addValueEventListener(setVal);

        ValueEventListener testJson1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                // Last 100 posts, these are automatically the 100 most recent
// due to sorting by push() keys

                Object object = snapshot.getValue(Object.class);
                String json = new Gson().toJson(object);
                Log.d(TAG, "1234"+json);
                findID();
                try {
                    String datainput = json; //readJSON();
                    JSONObject jsonObject = new JSONObject(datainput);
                    //JSONObject DHT22json = jsonObject.getJSONObject("DHT22");
                   // JSONObject Humidity = DHT22json.getJSONObject("Humidity");
                    Iterator x = jsonObject.keys();
                    JSONArray jsonArrayDoAm = new JSONArray();
                    while (x.hasNext()){
                        String key = (String) x.next();
                        jsonArrayDoAm.put(jsonObject.get(key));
                    }
                    ArrayList<String> itemsDoAm = new ArrayList<String>();
                    for(int i=0; i < jsonArrayDoAm.length() ; i++) {
                        String doam= (String) jsonArrayDoAm.get(i);
                        itemsDoAm.add(doam);
                        Log.d(doam,"Output "+itemsDoAm.get(i));
                    }
                    ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_expandable_list_item_1, itemsDoAm);
                    listViewTest.setAdapter(mArrayAdapter);
                } catch ( JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };mdatabase.child("DHT22").child("Temperature").limitToLast(10).addValueEventListener(testJson1);

//        findID();
//        try {
//            String datainput =readJSON();
//            JSONObject jsonObject = new JSONObject(datainput);
//            JSONObject DHT22json = jsonObject.getJSONObject("DHT22");
//            JSONObject Humidity = DHT22json.getJSONObject("Humidity");
//            Iterator x = Humidity.keys();
//            JSONArray jsonArrayDoAm = new JSONArray();
//            while (x.hasNext()){
//                String key = (String) x.next();
//                jsonArrayDoAm.put(Humidity.get(key));
//            }
//            ArrayList<String> itemsDoAm = new ArrayList<String>();
//            for(int i=0; i < jsonArrayDoAm.length() ; i++) {
//                String doam= (String) jsonArrayDoAm.get(i);
//                itemsDoAm.add(doam);
//                Log.d(doam,"Output");
//            }
//            ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this,
//                    android.R.layout.simple_expandable_list_item_1, itemsDoAm);
//            listViewTest.setAdapter(mArrayAdapter);
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }

    }

    private void findID() {
        listViewTest = (ListView) findViewById(R.id.lview);
    }

//    public String readJSON() throws IOException {
//        InputStream is = getResources().openRawResource(R.raw.data);
//        Writer writer = new StringWriter();
//        char[] buffer = new char[1024];
//        try {
//            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            int n;
//            while ((n = reader.read(buffer)) != -1) {
//                writer.write(buffer, 0, n);
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            is.close();
//        }
//        String jsonString = writer.toString();
//        return  jsonString;
//
//    }
}