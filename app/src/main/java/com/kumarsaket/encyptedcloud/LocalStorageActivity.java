package com.kumarsaket.encyptedcloud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kumarsaket.encyptedcloud.RubiksCubeAlgo.Decryption;
import com.kumarsaket.encyptedcloud.RubiksCubeAlgo.Encryption;
import com.snatik.storage.Storage;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class LocalStorageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LocalStorageActivity";
    Button filePicker, doEncrypt;
    ImageView imagefile;
    Uri fileUri;
    EditText filepickerNAME;
    private FirebaseAuth mAuth;
    private int PICK_IMAGE_REQUEST = 101;
    boolean uploadInprogress;
    Handler handler = new Handler();
    private LinearLayout llUpper, llLower;
    private FrameLayout flMiddile;
    NumberProgressBar uploadingProgress;
    private Point screenSize;
    private int screenWidth, screenHeight;
    Bitmap encryptedbitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_storage);

        Display display = getWindowManager().getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);
        screenWidth = screenSize.x;
        screenHeight = screenSize.y;

        initialize();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (uploadInprogress) {
                    Toast.makeText(this, "Encryption in Progress", Toast.LENGTH_LONG).show();
                } else {
                    this.finish();
                }
                break;
        }
        return true;
    }


    private void initialize() {
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        uploadingProgress = findViewById(R.id.LSAuploadingprogress);
        llUpper = findViewById(R.id.LSAtopLL);
        flMiddile = findViewById(R.id.LSAframe);
        llLower = findViewById(R.id.LSAllprogress);
        filepickerNAME = findViewById(R.id.LSAfilename);
        filePicker = findViewById(R.id.LSAfilePicker);
        filePicker.setOnClickListener(this);
        imagefile = findViewById(R.id.LSAimagefile);
        doEncrypt = findViewById(R.id.LSAdoEncrypt);
        doEncrypt.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        uploadInprogress = false;
        setLayoutParams(llUpper, flMiddile, doEncrypt, llLower);
    }

    private void setLayoutParams(LinearLayout llUpper, FrameLayout flMiddile, Button doEncrypt, LinearLayout llLower) {
        float layout_one_marginT = (float) 0.02,
                layout_one_marginB = (float) 0.01,
                layout_one_H = (float) 0.1;        //  0.13

        float layout_two_marginB = (float) 0.02,
                layout_two_H = (float) 0.4;         //  0.42

        float layout_three_H = (float) 0.08,
                layout_three_W = (float) 0.3,
                layout_three_marginT = (float) 0.02;    //  0.22

        float layout_four_H = (float) 0.3;

        ConstraintLayout.LayoutParams lp1 = (ConstraintLayout.LayoutParams) llUpper.getLayoutParams();
        lp1.topMargin = (int) (screenHeight * layout_one_marginT);
        lp1.bottomMargin = (int) (screenHeight * layout_one_marginB);
        lp1.height = (int) (screenHeight * layout_one_H);
        llUpper.setLayoutParams(lp1);

        ConstraintLayout.LayoutParams lp2 = (ConstraintLayout.LayoutParams) flMiddile.getLayoutParams();
        lp2.height = (int) (screenHeight * layout_two_H);
        lp2.bottomMargin = (int) (screenHeight * layout_two_marginB);
        flMiddile.setLayoutParams(lp2);

        ConstraintLayout.LayoutParams lp3 = (ConstraintLayout.LayoutParams) doEncrypt.getLayoutParams();
        lp3.height = (int) (screenHeight * layout_three_H);
        lp3.topMargin = (int) (screenHeight * layout_three_marginT);
        lp3.width = (int) (screenWidth * layout_three_W);
        doEncrypt.setLayoutParams(lp3);

        ConstraintLayout.LayoutParams lp4 = (ConstraintLayout.LayoutParams) llLower.getLayoutParams();
        lp4.height = (int) (screenHeight * layout_four_H);
        llLower.setLayoutParams(lp4);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LSAfilePicker:
                if (uploadInprogress) {
                    Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    fileIntent.setType("image/*");
                    startActivityForResult(fileIntent, PICK_IMAGE_REQUEST);
                }
                break;
            case R.id.LSAdoEncrypt:
                if (uploadInprogress) {
                    Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    if (fileUri != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        uploadInprogress = true;
                                        doEncrypt.setVisibility(View.GONE);
                                        llLower.setVisibility(View.VISIBLE);
                                        flMiddile.getChildAt(1).setVisibility(View.VISIBLE);
                                        flMiddile.getChildAt(2).setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }).start();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final Bitmap originalbitmap = ((BitmapDrawable) imagefile.getDrawable()).getBitmap();
                                final int[][] OriginalPixelMatrix = extract2DpixelArray(originalbitmap);
                                final Encryption encryption = new Encryption(OriginalPixelMatrix, originalbitmap.getWidth(), originalbitmap.getHeight());
                                encryption.doEnc();
                                final int[][] encryptedPixelMatrix = encryption.PixelMatrix();

                                encryptedbitmap = Bitmap.createBitmap(originalbitmap.getWidth(), originalbitmap.getHeight(), Bitmap.Config.ARGB_8888);
                                for (int i = 0; i < encryptedbitmap.getHeight(); i++) {
                                    for (int j = 0; j < encryptedbitmap.getWidth(); j++) {
                                        encryptedbitmap.setPixel(j, i, encryptedPixelMatrix[i][j]);
                                    }
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LinearLayout container = (LinearLayout) llLower.getChildAt(0);
                                        container.getChildAt(1).setVisibility(View.GONE);
                                        container.getChildAt(2).setVisibility(View.VISIBLE);
                                        TextView tv = (TextView) container.getChildAt(0);
                                        tv.setText("Encrypted");
                                    }
                                });

//                                getting extention and naming files :- Encrypted Image and Encryption Key
                                ContentResolver cR = getContentResolver();
                                MimeTypeMap mime = MimeTypeMap.getSingleton();
                                String fileExtention = mime.getExtensionFromMimeType(cR.getType(fileUri));
                                final String IMGfileName = System.currentTimeMillis() + "." + fileExtention;
                                String KEYfileName = System.currentTimeMillis() + ".txt";


//                                uploading image and store keys
                                SaveKeys(encryption.getKc(), encryption.getKr(), KEYfileName, IMGfileName, encryptedbitmap);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        uploadingProgress.setReachedBarColor(Color.parseColor("#70A800"));
                                        uploadingProgress.setProgressTextColor(Color.parseColor("#70A800"));
                                        uploadingProgress.setProgress(100);
                                        LinearLayout container = (LinearLayout) llLower.getChildAt(1);
                                        TextView tv = (TextView) container.getChildAt(0);
                                        tv.setText("Uploaded");
                                        flMiddile.getChildAt(1).setVisibility(View.INVISIBLE);
                                        flMiddile.getChildAt(2).setVisibility(View.INVISIBLE);
                                        imagefile.setImageBitmap(encryptedbitmap);
                                        originalbitmap.recycle();
                                        llLower.getChildAt(2).setVisibility(View.VISIBLE);
                                        Toast.makeText(getApplicationContext(), "Successfully Encrypted"
                                                , Toast.LENGTH_SHORT).show();
                                        uploadInprogress = false;
                                    }
                                }, 2000);
                                Log.d(TAG, "onSuccess: decryption done");
                            }
                        }).start();
                    } else {
                        Toast.makeText(this, "NO file Selected", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            fileUri = data.getData();
            Picasso.get().
                    load(fileUri).
                    into(imagefile);
            filepickerNAME.setText(new File(fileUri.toString()).getName());
            resetUI();
        } else {
            Log.d("EROOR", "onActivityResult: FILE picker ERROR");
        }
    }

    private int[][] extract2DpixelArray(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        int[][] result = new int[height][width];

        int[] pixels = new int[height * width];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        final int pixelLength = 1;
        for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
            result[row][col] = pixels[pixel];
//            Log.i(TAG, "extract2DpixelArray: " + Integer.toBinaryString(pixels[pixel]));
            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }
        return result;
    }

    private void SaveKeys(int[] kc, int[] kr, String keyfileName, String fileName, Bitmap b) {
        Storage storage = new Storage(getApplication());
        try {
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
            IMGPath.append(File.separator);
            IMGPath.append(new File(fileName));

            StringBuilder KEYPath = new StringBuilder();
            KEYPath.append(dir);
            KEYPath.append(File.separator);
            KEYPath.append("keys");
            if (!new File(KEYPath.toString()).exists()) {
                new File(KEYPath.toString()).mkdir();
            }
            KEYPath.append(File.separator);
            KEYPath.append(new File(keyfileName));

            StringBuilder keys = new StringBuilder();
            for (int value : kc) {
                keys.append(value);
                keys.append(":");
            }
            for (int value : kr) {
                keys.append(":");
                keys.append(value);
            }

            storage.createFile(IMGPath.toString(), b);
            storage.createFile(KEYPath.toString(), keys.toString());

        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplication(), "Error while fetching Keys : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        Log.d(TAG, "SaveKeys: DONE");
    }

    @Override
    public void onBackPressed() {
        if (uploadInprogress) {
            Toast.makeText(this, "Encryption in Progress", Toast.LENGTH_LONG).show();
        } else {
            super.onBackPressed();
        }
    }

    private void resetUI() {
        doEncrypt.setVisibility(View.VISIBLE);
        llLower.setVisibility(View.GONE);
        flMiddile.getChildAt(1).setVisibility(View.INVISIBLE);
        flMiddile.getChildAt(2).setVisibility(View.INVISIBLE);
        LinearLayout container = (LinearLayout) llLower.getChildAt(0);
        container.getChildAt(1).setVisibility(View.VISIBLE);
        container.getChildAt(2).setVisibility(View.GONE);
        TextView tv = (TextView) container.getChildAt(0);
        tv.setText("Encrypting...");
        uploadingProgress.setReachedBarColor(Color.parseColor("#3498DB"));
        uploadingProgress.setProgressTextColor(Color.parseColor("#3498DB"));
        uploadingProgress.setProgress(0);
        LinearLayout container2 = (LinearLayout) llLower.getChildAt(1);
        TextView tv2 = (TextView) container2.getChildAt(0);
        tv2.setText("Uploading...");
        flMiddile.getChildAt(1).setVisibility(View.INVISIBLE);
        flMiddile.getChildAt(2).setVisibility(View.INVISIBLE);
        llLower.getChildAt(2).setVisibility(View.INVISIBLE);
        if (encryptedbitmap != null)
            encryptedbitmap.recycle();
    }
}