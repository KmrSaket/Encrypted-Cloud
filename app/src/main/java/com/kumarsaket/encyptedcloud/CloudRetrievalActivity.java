package com.kumarsaket.encyptedcloud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kumarsaket.encyptedcloud.Adapter.CloudRetrieveImageAdapter;
import com.kumarsaket.encyptedcloud.CustomClass.Upload;

import java.util.ArrayList;
import java.util.List;

public class CloudRetrievalActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCloud;
    private CloudRetrieveImageAdapter adapter;
    private List<Upload> mUploads;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_retrieval);
        initialize();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void initialize() {
        if (!isNetworkAvailable())
            Toast.makeText(getApplicationContext(), R.string.noNetwork, Toast.LENGTH_SHORT).show();
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.recyclerViewCloudProgress);
        recyclerViewCloud = findViewById(R.id.recyclerViewCloud);
        linearLayout = findViewById(R.id.no_image);
        recyclerViewCloud.setHasFixedSize(true);
        recyclerViewCloud.setItemViewCacheSize(20);
        recyclerViewCloud.setDrawingCacheEnabled(true);
        recyclerViewCloud.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewCloud.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL,
                false));
        mUploads = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploads/" + mAuth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }
                adapter = new CloudRetrieveImageAdapter(CloudRetrievalActivity.this, mUploads);
                recyclerViewCloud.setAdapter(adapter);
                progressBar.setVisibility(View.INVISIBLE);
                if (adapter.getItemCount() == 0) {
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}