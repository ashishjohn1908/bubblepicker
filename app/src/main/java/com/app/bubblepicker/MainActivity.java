package com.app.bubblepicker;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private BubblePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        picker = findViewById(R.id.picker);
        addBubbleView(dataList());
    }

    @Override
    protected void onResume() {
        super.onResume();

        picker.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        picker.onPause();
    }

    private ArrayList<String> dataList() {
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < 20; i++)
            list.add("Data_" + (i + 1));

        return list;
    }

    private void addBubbleView(final ArrayList<String> data) {

        picker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return data.size();
            }

            @Override
            public PickerItem getItem(int position) {
                PickerItem item = new PickerItem();
                item.setTitle(data.get(position));
                item.setWithImage(false);

                item.setWithRandomRadius(true);
                item.setScaleFactor(1.1f);

                item.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.white));
                item.setColorSelected(getResources().getColor(R.color.colorAccent));
                item.setColor(getResources().getColor(R.color.colorPrimary));
                item.setSwipeForceX(15);

                return item;
            }
        });

        picker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(PickerItem item) {

            }

            @Override
            public void onBubbleDeselected(PickerItem item) {

            }
        });
        picker.onPause();
        picker.onResume();
    }
}
