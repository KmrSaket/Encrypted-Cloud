package com.kumarsaket.encyptedcloud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.firebase.storage.UploadTask;
import com.kumarsaket.encyptedcloud.CustomClass.Upload;
import com.kumarsaket.encyptedcloud.RubiksCubeAlgo.Encryption;
import com.snatik.storage.Storage;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class CloudStorageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CloudStorageActivity";
    Button filePicker, doEncrypt;
    ImageView imagefile;
    Uri fileUri;
    EditText filepickerNAME;
    boolean uploadInprogress;
    private int screenWidth, screenHeight;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private int PICK_IMAGE_REQUEST = 101;
    private LinearLayout llLower;
    //    private LinearLayout shareLayot;
    private FrameLayout flMiddile;
    NumberProgressBar uploadingProgress;
    ProgressBar encryptingProgress;
    Bitmap encryptedbitmap;
    private Intent imageIntent, key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_storage);
        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        screenWidth = screenSize.x;
        screenHeight = screenSize.y;


        initialize();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (uploadInprogress) {
                Toast.makeText(this, R.string.enc_under_progress, Toast.LENGTH_LONG).show();
            } else {
                this.finish();
            }
        }
        return true;
    }

    private void initialize() {
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        encryptingProgress = findViewById(R.id.encryptingProgress);
        uploadingProgress = findViewById(R.id.uploadingprogress);
        LinearLayout llUpper = findViewById(R.id.topLL);
        flMiddile = findViewById(R.id.frame);
        llLower = findViewById(R.id.llprogress);
//        shareLayot = findViewById(R.id.shareLayout);
        Button shareIMG = findViewById(R.id.CSshareimg);
        shareIMG.setOnClickListener(this);
        Button shareKEY = findViewById(R.id.CSshareKey);
        shareKEY.setOnClickListener(this);
        filepickerNAME = findViewById(R.id.filename);
        filePicker = findViewById(R.id.filePicker);
        filePicker.setOnClickListener(this);
        imagefile = findViewById(R.id.imagefile);
        doEncrypt = findViewById(R.id.doEncrypt);
        doEncrypt.setOnClickListener(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads/" + mAuth.getCurrentUser().getUid());
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/" + mAuth.getCurrentUser().getUid());
        uploadInprogress = false;
        imageIntent = new Intent(Intent.ACTION_SEND);
        imageIntent.setType("image/png");
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
                layout_three_marginT = (float) 0.02;    //  0.4     (TEMP)

        float layout_four_H = (float) 0.25;              // 0.25

        float share_layout_marginT = (float) 0.02,
                share_layout_H = (float) 0.18;           // 0.2

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

        /*ConstraintLayout.LayoutParams lpSahre = (ConstraintLayout.LayoutParams) shareLayput.getLayoutParams();
        lpSahre.height = (int) (screenHeight * share_layout_H);
        lpSahre.topMargin = (int) (screenHeight * share_layout_marginT);
        shareLayput.setLayoutParams(lpSahre);*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CSshareimg:
                startActivity(imageIntent);
                Toast.makeText(getApplicationContext(), "IMG", Toast.LENGTH_SHORT).show();
                break;
            case R.id.CSshareKey:
                Toast.makeText(getApplicationContext(), "KEY", Toast.LENGTH_SHORT).show();
                break;
            case R.id.filePicker:
                if (uploadInprogress) {
                    Toast.makeText(this, R.string.enc_under_progress, Toast.LENGTH_SHORT).show();
                } else {
                    Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    fileIntent.setType("image/*");
                    startActivityForResult(fileIntent, PICK_IMAGE_REQUEST);
                }
                break;
            case R.id.doEncrypt:
                if (uploadInprogress) {
                    Toast.makeText(this, R.string.enc_under_progress, Toast.LENGTH_SHORT).show();
                } else {
                    if (fileUri != null) {
                        if (isNetworkAvailable()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    uploadInprogress = true;
                                    doEncrypt.setVisibility(View.GONE);
                                    llLower.setVisibility(View.VISIBLE);
                                    flMiddile.getChildAt(1).setVisibility(View.VISIBLE);
                                    flMiddile.getChildAt(2).setVisibility(View.VISIBLE);

                                }
                            });
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final Bitmap originalbitmap = ((BitmapDrawable) imagefile.getDrawable()).getBitmap();
                                    int[][] OriginalPixelMatrix = extract2DpixelArray(originalbitmap);
                                    final Encryption encryption = new Encryption(OriginalPixelMatrix, originalbitmap.getWidth(), originalbitmap.getHeight());
                                    encryption.doEnc();
                                    int[][] encryptedPixelMatrix = encryption.PixelMatrix();

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
                                            tv.setText(R.string.encrypted);
                                        }
                                    });


//                        uploading image
                                    ContentResolver cR = getContentResolver();
                                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                                    String fileExtention = mime.getExtensionFromMimeType(cR.getType(fileUri));
                                    final String IMGfileName = System.currentTimeMillis() + "." + fileExtention;
                                    final String KEYfileName = System.currentTimeMillis() + ".txt";


                                    StorageReference fileReference = mStorageRef.child(IMGfileName);

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    encryptedbitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                    byte[] data = baos.toByteArray();

                                    fileReference.putBytes(data).
                                            addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull final Exception exception) {
                                                    // Handle unsuccessful uploads
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            uploadingProgress.setReachedBarColor(Color.parseColor("#70A800"));
                                                            uploadingProgress.setProgressTextColor(Color.parseColor("#70A800"));
                                                            LinearLayout container = (LinearLayout) llLower.getChildAt(1);
                                                            TextView tv = (TextView) container.getChildAt(0);
                                                            tv.setText(R.string.uploaded);

                                                        }
                                                    });
                                                }
                                            }, 500);
                                            mStorageRef.child(IMGfileName).getDownloadUrl().
                                                    addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(getApplicationContext(), R.string.error_fetching_imageUrl, Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }
                                                    }).
                                                    addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            Upload upload = new Upload(IMGfileName,
                                                                    uri.toString(), KEYfileName);
                                                            String uploadId = mDatabaseRef.push().getKey();
                                                            mDatabaseRef.child(uploadId).setValue(upload);
                                                            SaveKeys(encryption.getKc(), encryption.getKr(), KEYfileName);
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    flMiddile.getChildAt(1).setVisibility(View.INVISIBLE);
                                                                    flMiddile.getChildAt(2).setVisibility(View.INVISIBLE);
                                                                    imagefile.setImageBitmap(encryptedbitmap);
//                                                                    originalbitmap.recycle();
                                                                    llLower.getChildAt(2).setVisibility(View.VISIBLE);
//                                                                    shareLayot.setVisibility(View.VISIBLE);
                                                                    Toast.makeText(getApplicationContext(), R.string.enc_success
                                                                            , Toast.LENGTH_SHORT).show();
                                                                    uploadInprogress = false;
                                                                }
                                                            });
                                                        }
                                                    });
                                        }
                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                            final double progress = (double) snapshot.getBytesTransferred() / snapshot.getTotalByteCount() * 100.0;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    uploadingProgress.setProgress((int) progress);
                                                }
                                            });
                                        }
                                    });

                                }
                            }).start();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.noNetwork, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, R.string.no_file_selected, Toast.LENGTH_SHORT).show();
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

    private void resetUI() {
        doEncrypt.setVisibility(View.VISIBLE);
        llLower.setVisibility(View.GONE);
        flMiddile.getChildAt(1).setVisibility(View.INVISIBLE);
        flMiddile.getChildAt(2).setVisibility(View.INVISIBLE);
        LinearLayout container = (LinearLayout) llLower.getChildAt(0);
        container.getChildAt(1).setVisibility(View.VISIBLE);
        container.getChildAt(2).setVisibility(View.GONE);
        TextView tv = (TextView) container.getChildAt(0);
        tv.setText(R.string.encrypting);
        uploadingProgress.setReachedBarColor(Color.parseColor("#3498DB"));
        uploadingProgress.setProgressTextColor(Color.parseColor("#3498DB"));
        uploadingProgress.setProgress(0);
        LinearLayout container2 = (LinearLayout) llLower.getChildAt(1);
        TextView tv2 = (TextView) container2.getChildAt(0);
        tv2.setText(R.string.uploading);
        flMiddile.getChildAt(1).setVisibility(View.INVISIBLE);
        flMiddile.getChildAt(2).setVisibility(View.INVISIBLE);
        llLower.getChildAt(2).setVisibility(View.INVISIBLE);
//        shareLayot.setVisibility(View.GONE);
//        if (encryptedbitmap != null)
//            encryptedbitmap.recycle();
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
            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }
        return result;
    }


    private void SaveKeys(int[] kc, int[] kr, String fileName) {
        Storage storage = new Storage(getApplication());
        StringBuilder filePath;
        try {
            StringBuilder keyPath = new StringBuilder();
            keyPath.append(Environment.getExternalStorageDirectory());
            keyPath.append(File.separator);
            keyPath.append(getApplication().getString(R.string.app_name));
            keyPath.append(File.separator);
            keyPath.append(getApplication().getString(R.string.CloudFolder));
            keyPath.append(File.separator);
            keyPath.append(getApplication().getString(R.string.keyFolder));

            if (!new File(keyPath.toString()).exists()) {
                new File(keyPath.toString()).mkdir();
            }

            filePath = new StringBuilder();
            filePath.append(keyPath);
            filePath.append(File.separator);
            filePath.append(new File(fileName));

            StringBuilder keys = new StringBuilder();
            for (int value : kc) {
                keys.append(value);
                keys.append(":");
            }
            for (int value : kr) {
                keys.append(":");
                keys.append(value);
            }

            storage.createFile(filePath.toString(), keys.toString());

        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplication(), getString(R.string.error_saving_keys) + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        Log.d(TAG, "SaveKeys: DONE");
    }

    public void onBackPressed() {
        if (uploadInprogress) {
            Toast.makeText(this, R.string.enc_under_progress, Toast.LENGTH_LONG).show();
        } else {
            super.onBackPressed();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}