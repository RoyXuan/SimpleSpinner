package com.vest.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vest.spinnerlibrary.Channel;
import com.vest.spinnerlibrary.SimpleSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "SimpleSpinner";
    private List<HashMap<String, String>> date = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SimpleSpinner spinner = (SimpleSpinner) findViewById(R.id.spinner);
        setData();
        spinner.attachCustomDataSource(new Channel() {
            @Override
            public int onCount() {
                return date.size();
            }

            @Override
            public String onShow(int position) {
                return date.get(position).get("show");
            }

            @Override
            public String onSelected(int position) {
                if (position == 0) {
                    return date.get(position).get("hide");
                }
                return date.get(position).get("show");
            }

            @Override
            public void onResultPosition(int position) {
                Map<String, String> selected = date.get(position);
                String show = selected.get("show");
                String hide = selected.get("hide");
                Log.i(TAG, "selected show is " + show);
                Log.i(TAG, "selected hide is " + hide);
            }
        });
    }

    public void setData() {
        HashMap<String, String> all = new HashMap<>();
        all.put("show", "全部");
        all.put("hide", "a,b,c");
        date.add(all);
        HashMap<String, String> a = new HashMap<>();
        a.put("show", "a");
        a.put("hide", "A");
        date.add(a);
        HashMap<String, String> b = new HashMap<>();
        b.put("show", "b");
        b.put("hide", "B");
        date.add(b);
        HashMap<String, String> c = new HashMap<>();
        c.put("show", "c");
        c.put("hide", "C");
        date.add(c);
    }
}
