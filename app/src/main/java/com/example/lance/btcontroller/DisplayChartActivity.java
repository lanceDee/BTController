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

        ArrayList<Entry>[] lineList = new ArrayList[5];
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
                    xVals.add("" + (entries.size()-1));
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

        /*************函数显示**************/

        //初始化六条折线数据
        for (byte i = 0; i < 5; i++)
            lineList[i] = new ArrayList<Entry>();

        for (byte i = 0; i < 32; i++){
            double xval =  6.28*i/32;
            lineList[0].add(new Entry((float) Math.sin(xval)*60, i));
            lineList[1].add(new Entry((float) Math.cos(xval)*60, i));
            lineList[2].add(new Entry((float) 120/32*i, i));
            lineList[3].add(new Entry((float) (i*i*i)/100, i));
            lineList[4].add(new Entry((float) Math.log10((i+1)*10), i));
        }

        //新建六个LineDataSet对象，并添加对应数据
        LineDataSet sinDataSet = new LineDataSet(lineList[0], "sin");
        LineDataSet cosDataSet = new LineDataSet(lineList[1], "cos");
        LineDataSet tanDataSet = new LineDataSet(lineList[2], "tan");
        LineDataSet scalbDataSet = new LineDataSet(lineList[3], "scalb");
        LineDataSet log10DataSet = new LineDataSet(lineList[4], "log10");

        //保存到列表

        allLines.add(lineDataSet);
        allLines.add(sinDataSet);
        allLines.add(cosDataSet);
        allLines.add(tanDataSet);
        allLines.add(scalbDataSet);
        allLines.add(log10DataSet);

        //为每个折线设置显示属性
        for (LineDataSet line : allLines) {
            line.setLineWidth(1.5f);
            line.setDrawCircles(false);
            line.setDrawValues(false);
        }
        lineDataSet.setCircleColor(Color.BLUE);
        sinDataSet.setColor(Color.RED);
        cosDataSet.setColor(Color.GREEN);
        tanDataSet.setColor(Color.YELLOW);
        scalbDataSet.setColor(Color.BLACK);
        log10DataSet.setColor(Color.GRAY);

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
