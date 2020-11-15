package com.kumarsaket.encyptedcloud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.kumarsaket.encyptedcloud.Adapter.LocalRetrieveImageAdapter;
import com.kumarsaket.encyptedcloud.CustomClass.LocalFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalRetrievalActivity extends AppCompatActivity {


    private static final String TAG = "LocalRetrievalActivity";
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_retrieval);
        initialize();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    private void initialize() {
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ProgressBar progressBar = findViewById(R.id.LSArecyclerViewCloudProgress);
        RecyclerView recyclerViewCloud = findViewById(R.id.LSArecyclerViewCloud);recyclerViewCloud.setHasFixedSize(true);
        recyclerViewCloud.setItemViewCacheSize(20);
        recyclerViewCloud.setDrawingCacheEnabled(true);
        recyclerViewCloud.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewCloud.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL,
                false));

        linearLayout = findViewById(R.id.LSAno_image);
        List<LocalFiles> localFiles = new ArrayList<>();

        StringBuilder dir = new StringBuilder();
        dir.append(Environment.getExternalStorageDirectory());
        dir.append(File.separator);
        dir.append(getApplication().getString(R.string.app_name));
        dir.append(File.separator);
        dir.append(getApplication().getString(R.string.LocalFolder));

        if (!new File(dir.toString()).exists()) {
            new File(dir.toString()).mkdir();
        }

        StringBuilder IMGPath = new StringBuilder();
        IMGPath.append(dir);
        IMGPath.append(File.separator);
        IMGPath.append("image");
        if (!new File(IMGPath.toString()).exists()) {
            new File(IMGPath.toString()).mkdir();
        }

        StringBuilder KEYPath = new StringBuilder();
        KEYPath.append(dir);
        KEYPath.append(File.separator);
        KEYPath.append("keys");
        if (!new File(KEYPath.toString()).exists()) {
            new File(KEYPath.toString()).mkdir();
        }

        File[] f = new File(IMGPath.toString()).listFiles();

        if (f != null) {
            if (f.length == 0) {
                linearLayout.setVisibility(View.VISIBLE);
            } else {
                for (File file : f) {
                    LocalFiles l = new LocalFiles(file.getName());
                    l.setImgPath(file.getAbsolutePath());
                    l.setKeyPath(KEYPath.toString() + File.separator +
                            file.getName().substring(0, file.getName().lastIndexOf(".")) + ".txt");
                    localFiles.add(l);
                }
            }
        }
        LocalRetrieveImageAdapter adapter = new LocalRetrieveImageAdapter(LocalRetrievalActivity.this, localFiles);
        recyclerViewCloud.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
    }
}