package com.kumarsaket.encyptedcloud.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kumarsaket.encyptedcloud.R;
import com.kumarsaket.encyptedcloud.RubiksCubeAlgo.Decryption;
import com.kumarsaket.encyptedcloud.RubiksCubeAlgo.LocalFiles;
import com.snatik.storage.Storage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class LocalRetrieveImageAdapter extends RecyclerView.Adapter<LocalRetrieveImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<LocalFiles> localFiles;
    private static final String TAG = "LocalRetrieveImageAdapt";

    public LocalRetrieveImageAdapter(Context mContext, List<LocalFiles> localFiles) {
        this.mContext = mContext;
        this.localFiles = localFiles;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.local_retrieval_image_item, parent, false);
        return new LocalRetrieveImageAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
        final LocalFiles uploadCurrent = localFiles.get(position);
        holder.textViewName.setText(uploadCurrent.getName());

        Picasso.get()
                .load(new File(uploadCurrent.getImgPath()))
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: imageloaded");
                        Log.d(TAG, "onSuccess: started decryption");
                        Storage storage = new Storage(mContext);
                        String content = storage.readTextFile(uploadCurrent.getKeyPath());


//                        extract key
                        final String[] token = content.split("::");
                        final String[] KcString = token[0].split(":");
                        final String[] KrString = token[1].split(":");

                        int[] Kc = new int[KcString.length];
                        int[] Kr = new int[KrString.length];


                        for (int i = 0; i < Kc.length; i++)
                            Kc[i] = Integer.parseInt(KcString[i]);
                        for (int i = 0; i < Kr.length; i++)
                            Kr[i] = Integer.parseInt(KrString[i]);

                        Bitmap originalbitmap = ((BitmapDrawable) holder.imageView.getDrawable()).getBitmap();

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
                        holder.imageView.setImageBitmap(decryptedbitmap);
                        Log.d(TAG, "onSuccess: decryption done");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "onError: " + e);
                    }
                });



    }

    @Override
    public int getItemCount() {
        return localFiles.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.localimage_item_name);
            imageView = itemView.findViewById(R.id.localimage_item_bitmap);
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
