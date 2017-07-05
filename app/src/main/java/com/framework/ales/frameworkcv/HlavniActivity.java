package com.framework.ales.frameworkcv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import data.ImageAdapter;

public class HlavniActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      /**  super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hlavni);
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("id", position);
                startActivity(intent);

            }
        });**/
    }

}
