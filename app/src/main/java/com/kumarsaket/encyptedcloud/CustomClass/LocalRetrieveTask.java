package com.kumarsaket.encyptedcloud.CustomClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class LocalRetrieveTask extends AsyncTask<LocalFiles, Integer, LocalRetrieveTask.Retrievalresult> {

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

    public LocalRetrieveTask(ImageView imageView, Context context, TextView textView, ProgressBar progressBar) {
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
    protected Retrievalresult doInBackground(LocalFiles... localFiles) {
        for (LocalFiles mlocal : localFiles) {


//            URLConnection connection;
            Bitmap originalbitmap = null;
            try {
//                URI uri = new URI(mlocal.getImgPath());
//                connection = new URL(uri.toString()).openConnection();
//                connection.connect();
//                InputStream inputStream = connection.getInputStream();
                File f = new File(mlocal.getImgPath());
                originalbitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (originalbitmap == null)
                return new LocalRetrieveTask.Retrievalresult(null, "Error fetching image");    //  unable to fetch image

            if (new File(mlocal.getKeyPath()).exists()) {        //  if keyFile exists.. extract keys
//                        extract key
                Storage storage = new Storage(context);
                String content = storage.readTextFile(mlocal.getKeyPath());
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
                int[][] OriginalPixelMatrix = extract2DpixelArray(originalbitmap);
                final Decryption decryption = new Decryption(OriginalPixelMatrix, originalbitmap.getWidth(), originalbitmap.getHeight(), Kr, Kc);
                decryption.doDecrypt();
                int[][] decryptedPixelMatrix = decryption.PixelMatrix();

                Bitmap decryptedbitmap = Bitmap.createBitmap(originalbitmap.getWidth(), originalbitmap.getHeight(), Bitmap.Config.ARGB_8888);
                for (int i = 0; i < decryptedbitmap.getHeight(); i++) {
                    for (int j = 0; j < decryptedbitmap.getWidth(); j++) {
                        decryptedbitmap.setPixel(j, i, decryptedPixelMatrix[i][j]);
                    }
                }


                return new LocalRetrieveTask.Retrievalresult(decryptedbitmap, mlocal.getName());
            } else {
                return new LocalRetrieveTask.Retrievalresult(null, "KEY NOT FOUND!");
            }
        }
        return null;
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
}
