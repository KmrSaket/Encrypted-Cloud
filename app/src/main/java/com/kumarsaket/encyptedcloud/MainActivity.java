package com.kumarsaket.encyptedcloud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button cloudStoreBtn,cloudRetrieveBtn,localStoreBtn,localRetrieveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();

        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        sb.append(File.separator);
        sb.append(getResources().getString(R.string.app_name));
        sb.append(File.separator);
        checkAndCreateFolder(sb.toString());
    }

    private void checkAndCreateFolder(String s) {
        if (!new File(s).exists() ) {
            new File(s).mkdir();
        }
        if (!new File(s + "Keys").exists()){
            new File(s + "Keys").mkdir();
        }
        if (!new File(s + "local").exists()){
            new File(s + "local").mkdir();
        }
    }

    private void initialize() {
        cloudStoreBtn = findViewById(R.id.cloudStoreBtn);
        cloudStoreBtn.setOnClickListener(this);
        cloudRetrieveBtn = findViewById(R.id.cloudRetrieveBtn);
        cloudRetrieveBtn.setOnClickListener(this);
        localStoreBtn = findViewById(R.id.localStoreBtn);
        localStoreBtn.setOnClickListener(this);
        localRetrieveBtn = findViewById(R.id.localRetrieveBtn);
        localRetrieveBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cloudStoreBtn:
                startActivity(new Intent(this, CloudStorageActivity.class));
                break;
            case R.id.cloudRetrieveBtn:
                startActivity(new Intent(this, CloudRetrievalActivity.class));
                break;
            case R.id.localStoreBtn:
                startActivity(new Intent(this, LocalStorageActivity.class));
                break;
            case R.id.localRetrieveBtn:
                startActivity(new Intent(this, LocalRetrievalActivity.class));
                break;
        }

    }
}