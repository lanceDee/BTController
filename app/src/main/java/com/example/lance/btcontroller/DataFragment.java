package com.example.lance.btcontroller;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lance on 2017/7/26.
 */

public class DataFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "DataFragment";
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dataFragment = inflater.inflate(R.layout.data_fragment_layout, container, false);
        return dataFragment;
    }

    public class FileAdapter extends ArrayAdapter<File> {

        private int resourceId;
        public FileAdapter(Context context, int resource, List<File> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @NonNull
        @Override
        @TargetApi(24)
        public View getView(int position, View convertView, ViewGroup parent) {
            File file = getItem(position);
            String fileName = file.getName().toString();
            long time = file.lastModified();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String modfiedTime = formatter.format(time);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.file_info);
            textView.setText(fileName+"\n"+modfiedTime);
            if (file.isDirectory())
                ((ImageView) view.findViewById(R.id.file_image)).setImageResource(R.drawable.image_directory);
            return view;
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<File> fileList = new ArrayList<File>();
        File dir = new File(Constants.DATA_DIRECTORY);
        if (!dir.exists())
            dir.mkdirs();
        File[] files = dir.listFiles();
        Collections.addAll(fileList, files);
        if (fileList.isEmpty())
            ((MainActivity) getActivity()).setTabDisplay(4);
        else {
            listView = (ListView) getActivity().findViewById(R.id.datafragment_list_view);
            FileAdapter adapter = new FileAdapter(
                    getActivity(), R.layout.file_list_item, fileList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String fileName = Constants.DATA_DIRECTORY+"/"
                        +((TextView) view.findViewById(R.id.file_info)).getText().toString().split("\n")[0];
                    Intent intent = new Intent(getActivity(), DisplayChartActivity.class);
                    intent.putExtra("filename", fileName);
                    startActivity(intent);
                }
            });
        }
    }



    @Override
    public void onClick(View view) {

    }


}
