package com.kumarsaket.encyptedcloud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kumarsaket.encyptedcloud.Adapter.CloudRetrieveImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class CloudRetrievalActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCloud;
    private CloudRetrieveImageAdapter adapter;
    private DatabaseReference databaseReference;
    private List<Upload> mUploads;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_retrieval);
        initialize();
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.recyclerViewCloudProgress);
        recyclerViewCloud = findViewById(R.id.recyclerViewCloud);
        recyclerViewCloud.setHasFixedSize(true);
        recyclerViewCloud.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL,
                false));
        mUploads = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads/" + mAuth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }
                adapter = new CloudRetrieveImageAdapter(CloudRetrievalActivity.this, mUploads);
                recyclerViewCloud.setAdapter(adapter);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}