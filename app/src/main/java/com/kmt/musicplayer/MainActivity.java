package com.kmt.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    GridView grvLibrary,grvTools;
    GridviewMainAdapter adapterLibrary,adapterTools;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setAdapterForGridview();
        grvLibrary.setAdapter(adapterLibrary);
        grvTools.setAdapter(adapterTools);
        grvLibrary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position){
                    case 0:
                        intent=new Intent(getBaseContext(),ActivityListSong.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent=new Intent(getBaseContext(),ActivitySongFolder.class);
                        startActivity(intent);
                        break;
                    case 2:
                        break;
                    case 3:
                        break;


                }
            }
        });
        grvTools.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        break;
                }
            }
        });


    }

    private void setAdapterForGridview() {
        String[] arrTittleLibrary=getResources().getStringArray(R.array.arr_title_bibr_item);
        String[] arrTittleTools=getResources().getStringArray(R.array.arr_title_tools_item);
        String[] arrImgLibrary=getResources().getStringArray(R.array.arr_img_libr_item);
        String[] arrImgTools=getResources().getStringArray(R.array.arr_img_tools_item);
        adapterLibrary=new GridviewMainAdapter(this,arrImgLibrary,arrTittleLibrary);
        adapterTools=new GridviewMainAdapter(this,arrImgTools,arrTittleTools);
    }

    private void initView() {
        grvLibrary=findViewById(R.id.grvLibrary);
        grvTools=findViewById(R.id.grvTool);

    }
}