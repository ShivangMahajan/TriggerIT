package com.sabbey.triggerit;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addBtn;
    private ListView listView;
    private List<GeoObject> list;
    private ArrayAdapter adapter;
    private TextView instruct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        config();
    }

    private void config() {

        addBtn = findViewById(R.id.addbtn);
        instruct = findViewById(R.id.instruct);
        listView = findViewById(R.id.listview);
        listUpdate();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddDetails.class);
                intent.putExtra("state", "new");
                startActivity(intent);
            }
        });
        permissions();
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, AddDetails.class);
                intent.putExtra("pos", position);
                intent.putExtra("state", "edit");
                startActivity(intent);
            }
        });
    }

    void permissions()
    {
        NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(n.isNotificationPolicyAccessGranted()) {
            }else{
                // Ask the user to grant access
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
        AddDetails.checkLocationPermission(MainActivity.this);
    }

    void listUpdate()
    {
        List<String> nameList = new ArrayList<>();
        list = PrefsConfig.readFromPrefs(getApplicationContext());
        if (list != null) {

            if (list.size() == 0)
                instruct.setVisibility(View.VISIBLE);
            else
                instruct.setVisibility(View.GONE);
            for (int i = 0; i < list.size(); i++) {
                nameList.add(list.get(i).name);
            }
        }
        else
        {
            instruct.setVisibility(View.VISIBLE);
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList);
        listView.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        listUpdate();
    }
}
