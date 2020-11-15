package com.kumarsaket.encyptedcloud.CustomClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kumarsaket.encyptedcloud.R;
import com.kumarsaket.encyptedcloud.RubiksCubeAlgo.Decryption;
import com.snatik.storage.Storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CloudRetrieveTask extends AsyncTask<Upload, Integer, CloudRetrieveTask.Retrievalresult> {

    public static class Retrievalresult {
        Bitmap decryptIMG;
        String fileName;

        public Retrievalresult(Bitmap decryptIMG, String fileName) {
            this.decryptIMG = decryptIMG;
            this.fileName = fileName;
        }
    }

    private ImageView imageView;
    private Context context;
    private TextView textView;
    private ProgressBar progressBar;


    public CloudRetrieveTask(ImageView imageView, Context context, TextView textView, ProgressBar progressBar) {
        this.imageView = imageView;
        this.context = context;
        this.textView = textView;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Retrievalresult doInBackground(Upload... uploads) {
        for (Upload mupload : uploads) {

            Bitmap originalbitmap = null;
            try {
//                extract bitmap
                URL url = new URL(mupload.getmImageUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                originalbitmap = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (originalbitmap == null)
                return new Retrievalresult(null, "Error fetching image");    //  unable to fetch image


//            extract key
            StringBuilder keyPath = new StringBuilder();
            keyPath.append(Environment.getExternalStorageDirectory())
                    .append(File.separator)
                    .append(context.getResources().getString(R.string.app_name))
                    .append(File.separator)
                    .append(context.getResources().getString(R.string.CloudFolder))
                    .append(File.separator)
                    .append(context.getResources().getString(R.string.keyFolder))
                    .append(File.separator)
                    .append(mupload.getmKeyName());

            if (new File(keyPath.toString()).exists()) {        //  if keyFile exists.. extract keys
                Storage storage = new Storage(context);
                String content = storage.readTextFile(keyPath.toString());
                final String[] token = content.split("::");
                final String[] KcString = token[0].split(":");
                final String[] KrString = token[1].split(":");

                int[] Kc = new int[KcString.length];
                int[] Kr = new int[KrString.length];


                for (int i = 0; i < Kc.length; i++)
                    Kc[i] = Integer.parseInt(KcString[i]);
                for (int i = 0; i < Kr.length; i++)
                    Kr[i] = Integer.parseInt(KrString[i]);


                //            decrypt image
                int[][] OriginalPixelMatrix;
                OriginalPixelMatrix = extract2DpixelArray(originalbitmap);
                final Decryption decryption = new Decryption(OriginalPixelMatrix, originalbitmap.getWidth(), originalbitmap.getHeight(), Kr, Kc);
                decryption.doDecrypt();
                int[][] decryptedPixelMatrix = decryption.PixelMatrix();

                Bitmap decryptedbitmap = Bitmap.createBitmap(originalbitmap.getWidth(), originalbitmap.getHeight(), Bitmap.Config.ARGB_8888);
                for (int i = 0; i < decryptedbitmap.getHeight(); i++) {
                    for (int j = 0; j < decryptedbitmap.getWidth(); j++) {
                        decryptedbitmap.setPixel(j, i, decryptedPixelMatrix[i][j]);
                    }
                }
                return new Retrievalresult(decryptedbitmap, mupload.getmName());
            } else {
                return new Retrievalresult(null, "KEY NOT FOUND!");
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Retrievalresult retrievalresult) {
        if (retrievalresult != null) {
            if (retrievalresult.decryptIMG != null) {

                imageView.setImageBitmap(retrievalresult.decryptIMG);
            } else {
                imageView.setImageResource(R.drawable.key_error);
            }
            textView.setText(retrievalresult.fileName);
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
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

}
