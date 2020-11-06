package com.kumarsaket.encyptedcloud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.File;

public class LocalStorageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LocalStorageActivity";
    Button filePicker, doEncrypt;
    ImageView imagefile;
    ImageView test;
    ProgressBar progressBar;
    Uri fileUri;
    private FirebaseAuth mAuth;
    private int PICK_IMAGE_REQUEST = 101;
    boolean uploadInprogress;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_storage);
        initialize();
    }

    private void initialize() {
        test = findViewById(R.id.test);

        progressBar = findViewById(R.id.LSAprogress);
        filePicker = findViewById(R.id.LSAfilePicker);
        filePicker.setOnClickListener(this);
        imagefile = findViewById(R.id.LSAimagefile);
        doEncrypt = findViewById(R.id.LSAdoEncrypt);
        doEncrypt.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        uploadInprogress = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LSAfilePicker:
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("image/*");
                startActivityForResult(fileIntent, PICK_IMAGE_REQUEST);
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
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                });
                                final Bitmap originalbitmap = ((BitmapDrawable) imagefile.getDrawable()).getBitmap();
                                Log.d(TAG, "run: originalAPLHA ::: " + originalbitmap.hasAlpha());
                                final int[][] OriginalPixelMatrix = extract2DpixelArray(originalbitmap);
                                final int[][] oPPP = extract2DpixelArray(originalbitmap);
                                final Encryption encryption = new Encryption(OriginalPixelMatrix, originalbitmap.getWidth(), originalbitmap.getHeight());
                                encryption.doEnc();
                                final int[][] encryptedPixelMatrix = encryption.PixelMatrix();

                                final Bitmap encryptedbitmap = Bitmap.createBitmap(originalbitmap.getWidth(), originalbitmap.getHeight(), Bitmap.Config.ARGB_8888);
                                encryptedbitmap.setHasAlpha(originalbitmap.hasAlpha());
                                for (int i = 0; i < originalbitmap.getHeight(); i++) {
                                    for (int j = 0; j < originalbitmap.getWidth(); j++) {
                                        encryptedbitmap.setPixel(j, i, encryptedPixelMatrix[i][j]);
                                    }
                                }
                                Log.d(TAG, "run: encryptedbitmap ::: " + encryptedbitmap.hasAlpha());
//                                getting extention and naming files :- Encrypted Image and Encryption Key
                                ContentResolver cR = getContentResolver();
                                MimeTypeMap mime = MimeTypeMap.getSingleton();
                                String fileExtention = mime.getExtensionFromMimeType(cR.getType(fileUri));
                                final String IMGfileName = System.currentTimeMillis() + "." + fileExtention;
                                String KEYfileName = System.currentTimeMillis() + ".txt";


//                                uploading image and store keys
                                SaveKeys(encryption.getKc(), encryption.getKr(), KEYfileName, IMGfileName, encryptedbitmap);
                                Log.d(TAG, "onSuccess: encryption done");

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        uploadInprogress = false;

                                        test.setImageBitmap(encryptedbitmap);
                                        Bitmap originalbitmap2 = ((BitmapDrawable) test.getDrawable()).getBitmap();

                                        int[][] OriginalPixelMatrix2 = extract2DpixelArray(originalbitmap2);
                                        final Decryption decryption = new Decryption(OriginalPixelMatrix2, originalbitmap2.getWidth(),
                                                originalbitmap2.getHeight(), encryption.getKr(), encryption.getKc());
                                        decryption.doDecrypt();
                                        int[][] decryptedPixelMatrix = decryption.PixelMatrix();

                                        final Bitmap decryptedbitmap = Bitmap.createBitmap(originalbitmap2.getWidth(), originalbitmap2.getHeight(), Bitmap.Config.ARGB_8888);
                                        decryptedbitmap.setHasAlpha(encryptedbitmap.hasAlpha());
                                        for (int i = 0; i < decryptedbitmap.getHeight(); i++) {
                                            for (int j = 0; j < decryptedbitmap.getWidth(); j++) {
                                                decryptedbitmap.setPixel(j, i, decryptedPixelMatrix[i][j]);
                                            }
                                        }

                                        extract2DpixelArray(decryptedbitmap);

                                        Log.d(TAG, "run: decryptedbitmap ::: " + decryptedbitmap.hasAlpha());

                                        imagefile.setImageBitmap(decryptedbitmap);
                                        boolean flag = true;
                                        for (int i = 0; i < originalbitmap.getHeight(); i++) {
                                            for (int j = 0; j < originalbitmap.getWidth(); j++) {
                                                if (oPPP[i][j] !=decryptedPixelMatrix[i][j]){
                                                    flag = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (flag){
                                            Toast.makeText(getApplicationContext(), "EQUAL", Toast.LENGTH_LONG).show();
                                        }else {
                                            Toast.makeText(getApplicationContext(), "NOT EQUAL", Toast.LENGTH_LONG).show();
                                        }

                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });


                                Log.d(TAG, "onSuccess: decryption done");

                            }
                        }).start();
                    } else {
                        Toast.makeText(this, "NO file Selecterd", Toast.LENGTH_SHORT).show();
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
            imagefile.setImageURI(fileUri);

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
            if (!new File(IMGPath.toString()).exists()){
                new File(IMGPath.toString()).mkdir();
            }
            IMGPath.append(File.separator);
            IMGPath.append(new File(fileName));

            StringBuilder KEYPath = new StringBuilder();
            KEYPath.append(dir);
            KEYPath.append(File.separator);
            KEYPath.append("keys");
            if (!new File(KEYPath.toString()).exists()){
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

        } catch (Exception e) {
            Log.d(TAG, "SaveKeys: Error Ocurred" + e);
        }
        Log.d(TAG, "SaveKeys: DONE");
    }

    @Override
    public void onBackPressed() {
        if (uploadInprogress){
            Toast.makeText(this, "Encryption in Progress", Toast.LENGTH_LONG).show();
        }else {
            super.onBackPressed();
        }
    }
}