package com.kumarsaket.encyptedcloud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.view.Menu;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    GridLayout gridLayout;
    private int screenWidth, screenHeight;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        screenWidth = screenSize.x;
        screenHeight = screenSize.y;

        initialize();

        String sb = Environment.getExternalStorageDirectory() +
                File.separator +
                getResources().getString(R.string.app_name) +
                File.separator;
        checkAndCreateFolder(sb);
    }

    private void checkAndCreateFolder(String s) {

        if (!new File(s).exists()) {
            new File(s).mkdir();
        }
        if (!new File(s + "cloud").exists()) {
            new File(s + "cloud").mkdir();
        }
        if (!new File(s + "local").exists()) {
            new File(s + "local").mkdir();
        }
    }

    private void initialize() {
        TextView username = findViewById(R.id.welcomeText);
        String welcomeText = "Hey, " + firebaseAuth.getCurrentUser().getEmail();
        username.setText(welcomeText);

        gridLayout = findViewById(R.id.home_grid);
        setLayoutParams(gridLayout);
        setClickEvent(gridLayout);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.appbar_logo);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }


    private void setLayoutParams(GridLayout gridLayout) {
        /*
        float textH = (float) 0.15,
                textMtop = (float) 0.025,
                textMbottom = (float) 0;        //total TV height = 0.2
        */

        float cardH = (float) 0.7 / 3,
                cardMtop = (float) 0.03;         // total grid height = 0.7 + 0.06 = 0.66

        float imgH = (float) (cardH * 0.5),
                imgMtop = (float) (cardH * 0.05),
                imgMbottom = (float) (cardH * 0.05);

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            final CardView cardView = (CardView) gridLayout.getChildAt(i);
            GridLayout.LayoutParams layoutParams = (GridLayout.LayoutParams) cardView.getLayoutParams();
            layoutParams.height = (int) (screenHeight * cardH);
            layoutParams.width = (int) (screenWidth * 0.41);
            if (i != 0 && i != 1)
                layoutParams.topMargin = (int) (screenHeight * cardMtop);
            else
                layoutParams.topMargin = (int) (screenHeight * cardMtop) * 2;

            if (i % 2 == 0) {
//                left child
                layoutParams.leftMargin = (int) (screenWidth * 0.06);
                layoutParams.rightMargin = (int) (screenWidth * 0.03);
                cardView.setLayoutParams(layoutParams);
            } else {
//                right child
                layoutParams.leftMargin = (int) (screenWidth * 0.03);
                layoutParams.rightMargin = (int) (screenWidth * 0.06);
                cardView.setLayoutParams(layoutParams);
            }

            LinearLayout linearLayout = (LinearLayout) (cardView.getChildAt(0));
            ImageView imageView = (ImageView) linearLayout.getChildAt(0);
            LinearLayout.LayoutParams layoutParamsIMG = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            layoutParamsIMG.height = (int) (screenHeight * imgH);
            layoutParamsIMG.topMargin = (int) (screenHeight * imgMtop);
            layoutParamsIMG.bottomMargin = (int) (screenHeight * imgMbottom);
            imageView.setLayoutParams(layoutParamsIMG);


        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signout) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                }
            });
        }
        return true;
    }

    private void setClickEvent(final GridLayout gridLayout) {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            final CardView cardView = (CardView) gridLayout.getChildAt(i);
            cardView.setOnClickListener(this);
        }
    }

}