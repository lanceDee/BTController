package com.example.lance.btcontroller;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.lang.Math;


public class DisplayChartActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private File file;
    private FileInputStream in;
    private LineChart chart;
    private MenuItem itemReset;
    private ArrayList<LineDataSet> allLines = new ArrayList<LineDataSet>();
    private CheckBox[] checkBoxes = new CheckBox[6];
    private final String TAG = "DisplayChartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_chart);

        //6个通道的checkbox初始化
        Resources res = getResources();
        for (byte i = 1; i <= 6; i++){
            int id = res.getIdentifier("display_chart_check_" + i, "id", getPackageName());
            CheckBox checkBox = (CheckBox) findViewById(id);
            checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener(this);
        }

        //获取文件名
        Intent intent = getIntent();
        String filename = intent.getStringExtra("filename");
        Log.e(TAG, "Filename : "+filename );

        file = new File(filename);
        chart = (LineChart) findViewById(R.id.line_chart);

        ArrayList lineList = new ArrayList(6);
        ArrayList<Entry> entries = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        //6通道数据容器初始化
        for (int i = 0; i < 6; ++i)
            lineList.add(new ArrayList<Entry>());

        byte[] buffer = new byte[240];
        int len = 0;
        try {
            in = new FileInputStream(file);
            int sum = 0;
            int count = 20;
            while(true) {
                len = in.read(buffer);
                if (len == -1 || len < 240) {
                    break;
                }
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 6; j++) {
                        int start = i*24 + j*4;
                        int data =   (buffer[start]&0xff)<<24
                                    |(buffer[start+1]&0xff)<<16
                                    |(buffer[start+2]&0xff)<<8
                                    | buffer[start+3]&0xff;
                        //if (j == 4)
                            Log.e(TAG, "data Before: "+data);

                        float data_f = ((float) data)/2147483648L*5;
                        //Log.e(TAG, "data Float: "+data_f);

                        ((ArrayList<Entry>) lineList.get(j)).add(new Entry(data_f, sum+i));

                    }

                    xVals.add("" + (sum+i));

                }

                sum += 10;


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

        //LineDataSet lineDataSet = new LineDataSet(entries, "通道1");

        //保存到列表

        for (int i = 0; i < 6; ++i)
            allLines.add(new LineDataSet((ArrayList<Entry>) lineList.get(i), "通道"+(i+1)));

//
        //为每个折线设置显示属性
        for (LineDataSet line : allLines) {
            line.setLineWidth(1.5f);
            line.setDrawCircles(false);
            line.setDrawValues(false);
        }
        allLines.get(0).setCircleColor(Color.BLUE);
        allLines.get(1).setColor(Color.RED);
        allLines.get(2).setColor(Color.GREEN);
        allLines.get(3).setColor(Color.YELLOW);
        allLines.get(4).setColor(Color.BLACK);
        allLines.get(5).setColor(Color.GRAY);

        //最终生成LineData
        LineData lineData = new LineData(xVals, allLines);

        chart.setGridBackgroundColor(0xffffff);
        chart.setData(lineData);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        //chart.setScaleYEnabled(false);
        chart.setDescription(file.getName().toString()+"预览");
        chart.animateY(1000);
        /********************end of 函数显示*********************/


    }

//    private int codeTransfer(int data) {
//        if ((data & 0x80000000) != 0)
//            data = (~(data&0x7fffffff)+1) | 0x80000000;
//        return data;
//    }

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

    //checkbox的响应函数
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int index = buttonView.getId() - R.id.display_chart_check_1;
        allLines.get(index).setVisible(isChecked);
        chart.getViewPortHandler().refresh(new Matrix(), chart, true);
    }
}
