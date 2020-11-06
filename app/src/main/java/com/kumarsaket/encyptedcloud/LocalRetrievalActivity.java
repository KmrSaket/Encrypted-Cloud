package com.kumarsaket.encyptedcloud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.kumarsaket.encyptedcloud.Adapter.CloudRetrieveImageAdapter;
import com.kumarsaket.encyptedcloud.Adapter.LocalRetrieveImageAdapter;
import com.kumarsaket.encyptedcloud.RubiksCubeAlgo.LocalFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalRetrievalActivity extends AppCompatActivity {


    private RecyclerView recyclerViewCloud;
    private List<LocalFiles> localFiles;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private LocalRetrieveImageAdapter adapter;
    private static final String TAG = "LocalRetrievalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_retrieval);
        initialize();
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.LSArecyclerViewCloudProgress);
        recyclerViewCloud = findViewById(R.id.LSArecyclerViewCloud);
        recyclerViewCloud.setHasFixedSize(true);
        recyclerViewCloud.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL,
                false));


        localFiles = new ArrayList<>();

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
        if (!new File(IMGPath.toString()).exists()){
            new File(IMGPath.toString()).mkdir();
        }

        StringBuilder KEYPath = new StringBuilder();
        KEYPath.append(dir);
        KEYPath.append(File.separator);
        KEYPath.append("keys");
        if (!new File(KEYPath.toString()).exists()){
            new File(KEYPath.toString()).mkdir();
        }

        File[] f = new File(IMGPath.toString()).listFiles();

        if (f.length==0){

        }else {
            for (File file:f) {

                Log.d(TAG, "initialize: name " + file.getName());
                Log.d(TAG, "initialize: img  " + file.getAbsolutePath());
                Log.d(TAG, "initialize: text  " + KEYPath.toString() + File.separator +
                        file.getName().substring(0, file.getName().lastIndexOf(".")) + ".txt");
                LocalFiles l = new LocalFiles(file.getName());
                l.setImgPath(file.getAbsolutePath());
                l.setKeyPath(KEYPath.toString() + File.separator +
                        file.getName().substring(0, file.getName().lastIndexOf(".")) + ".txt");
                localFiles.add(l);
            }
        }

        adapter = new LocalRetrieveImageAdapter(LocalRetrievalActivity.this, localFiles);
        recyclerViewCloud.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);

    }
}