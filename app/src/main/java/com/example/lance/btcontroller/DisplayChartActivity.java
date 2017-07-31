package com.example.lance.btcontroller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DisplayChartActivity extends AppCompatActivity {

    private File file;
    private FileInputStream in;
    private LineChart chart;
    private MenuItem itemReset;
    private final String TAG = "DisplayChartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_chart);
        Intent intent = getIntent();
        String filename = intent.getStringExtra("filename");
        Log.e(TAG, "Filename : "+filename );
        file = new File(filename);
        chart = (LineChart) findViewById(R.id.line_chart);
        ArrayList<Entry> entries = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            in = new FileInputStream(file);
            while(true) {
                len = in.read(buffer);
                if (len == -1)
                    break;
                for (int i = 0; i < len; i++) {
                    entries.add(new Entry(buffer[i], entries.size()));
                    xVals.add("" + entries.size());
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "通道1");
        lineDataSet.setColor(Color.RED);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setLineWidth(2f);
        ArrayList<LineDataSet> allLines = new ArrayList<LineDataSet>();
        allLines.add(lineDataSet);

        LineData lineData = new LineData(xVals, allLines);
        chart.setData(lineData);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setScaleYEnabled(false);
        chart.setDescription(file.getName().toString()+"预览");
        chart.animateY(1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chart_menu, menu);
        itemReset = menu.findItem(R.id.menu_reset_item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        chart.getViewPortHandler().refresh(new Matrix(), chart, true);
        return true;
    }
}
