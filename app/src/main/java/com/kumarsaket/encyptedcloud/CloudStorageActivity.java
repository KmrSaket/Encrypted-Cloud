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
import com.kumarsaket.encyptedcloud.CustomClass.Upload;
import com.kumarsaket.encyptedcloud.RubiksCubeAlgo.Encryption;
import com.snatik.storage.Storage;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class CloudStorageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CloudStorageActivity";
    Button filePicker, doEncrypt;
    ImageView imagefile;
    EditText mfilename;
    Uri fileUri;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private int PICK_IMAGE_REQUEST = 101;

    Button test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_storage);

        initialize();
    }

    private void initialize() {
        test = findViewById(R.id.test);
        test.setOnClickListener(this);

        filePicker = findViewById(R.id.filePicker);
        filePicker.setOnClickListener(this);
        imagefile = findViewById(R.id.imagefile);
        doEncrypt = findViewById(R.id.doEncrypt);
        doEncrypt.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads/" + mAuth.getCurrentUser().getUid());
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads/" + mAuth.getCurrentUser().getUid());
        mfilename = findViewById(R.id.filename);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: started");
                        Bitmap originalbitmap = ((BitmapDrawable) imagefile.getDrawable()).getBitmap();
                        int[][] OriginalPixelMatrix = extract2DpixelArray(originalbitmap);
                        final Bitmap encryptedbitmap = Bitmap.createBitmap(originalbitmap.getWidth(), originalbitmap.getHeight(), Bitmap.Config.ARGB_8888);
                        for (int i = 0; i < originalbitmap.getHeight(); i++) {
                            for (int j = 0; j < originalbitmap.getWidth(); j++) {
                                if (i > j) {
                                    encryptedbitmap.setPixel(j, i, OriginalPixelMatrix[i][j]);
                                } else {
                                    encryptedbitmap.setPixel(j, i, 0);
                                }

                            }
                        }
                        imagefile.post(new Runnable() {
                            @Override
                            public void run() {
                                imagefile.setImageBitmap(encryptedbitmap);
                            }
                        });
                        Log.d(TAG, "run: stop");
                    }
                }).start();
                break;
            case R.id.filePicker:

                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("image/*");
                startActivityForResult(fileIntent, PICK_IMAGE_REQUEST);
                break;
            case R.id.doEncrypt:
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    if (fileUri != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final Bitmap originalbitmap = ((BitmapDrawable) imagefile.getDrawable()).getBitmap();
                                int[][] OriginalPixelMatrix = extract2DpixelArray(originalbitmap);
                                final Encryption encryption = new Encryption(OriginalPixelMatrix, originalbitmap.getWidth(), originalbitmap.getHeight());
                                encryption.doEnc();
                                int[][] encryptedPixelMatrix = encryption.PixelMatrix();

                                final Bitmap encryptedbitmap = Bitmap.createBitmap(originalbitmap.getWidth(), originalbitmap.getHeight(), Bitmap.Config.ARGB_8888);
                                for (int i = 0; i < originalbitmap.getHeight(); i++) {
                                    for (int j = 0; j < originalbitmap.getWidth(); j++) {
                                        encryptedbitmap.setPixel(j, i, encryptedPixelMatrix[i][j]);
                                    }
                                }


//                                Decryption decryption = new Decryption(encryptedPixelMatrix, originalbitmap.getWidth(), originalbitmap.getHeight(), encryption.getKr(), encryption.getKr());
//                                decryption.doDecrypt();
//                                int[][] decMATRIX = decryption.PixelMatrix();


//                                for (int i=0; i< originalbitmap.getHeight(); i++) {
//                                    for (int j=0; j< originalbitmap.getWidth(); j++) {
//                                        if (decMATRIX[i][j]!=OriginalPixelMatrix[i][j]) {
//                                            Log.d(TAG, "run: NOT");
//                                            break;
//                                        }
//                                    }
//                                }
//                                Log.d(TAG, "run: EQUAL");


//                        uploading image
                                ContentResolver cR = getContentResolver();
                                MimeTypeMap mime = MimeTypeMap.getSingleton();
                                String fileExtention = mime.getExtensionFromMimeType(cR.getType(fileUri));
                                final String fileName = System.currentTimeMillis() + "." + fileExtention;


                                StorageReference fileReference = mStorageRef.child(fileName);

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                encryptedbitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                byte[] data = baos.toByteArray();


                                mUploadTask = fileReference.putBytes(data).
                                        addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Handle unsuccessful uploads
                                                Toast.makeText(CloudStorageActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "onFailure: failed to upload file to storage");
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(CloudStorageActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "onSuccess: uploaded file to storage");
                                            }
                                        }, 500);
                                        mStorageRef.child(fileName).getDownloadUrl().
                                                addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "onFailure: " + e.toString());
                                                    }
                                                }).
                                                addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Upload upload = new Upload(mfilename.getText().toString().trim(),
                                                                uri.toString(), fileName);
                                                        String uploadId = mDatabaseRef.push().getKey();
                                                        mDatabaseRef.child(uploadId).setValue(upload);
                                                        SaveKeys(encryption.getKc(), encryption.getKr(), fileName, encryptedbitmap);
                                                    }
                                                });
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                        double progress = (double) snapshot.getBytesTransferred() / snapshot.getTotalByteCount() * 100.0;
                                        Log.d(TAG, "onProgress: uploading....  " + progress);
                                    }
                                });
//                store keys

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
            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }
        return result;
    }


    private void SaveKeys(int[] kc, int[] kr, String fileName, Bitmap b) {
        Storage storage = new Storage(getApplication());
        StringBuilder filePath = null;
        try {
            StringBuilder keyPath = new StringBuilder();
            keyPath.append(Environment.getExternalStorageDirectory());
            keyPath.append(File.separator);
            keyPath.append(getApplication().getString(R.string.app_name));
            keyPath.append(File.separator);
            keyPath.append(getApplication().getString(R.string.keyFolder));

            if (!new File(keyPath.toString()).exists()) {
                new File(keyPath.toString()).mkdir();
            }

            filePath = new StringBuilder();
            filePath.append(keyPath);
            filePath.append(File.separator);
            filePath.append(new File(fileName + ".txt"));

            StringBuilder keys = new StringBuilder();
            for (int value : kc) {
                keys.append(value);
                keys.append(":");
            }
            for (int value : kr) {
                keys.append(":");
                keys.append(value);
            }

            storage.createFile(filePath.toString().substring(0, filePath.lastIndexOf(".")), b);
            storage.createFile(filePath.toString(), keys.toString());

        } catch (Exception e) {
//            Toast.makeText(getApplication(), "Error Ocurred", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "SaveKeys: Error Ocurred" + e);
        }
        Log.d(TAG, "SaveKeys: DONE");
    }
}