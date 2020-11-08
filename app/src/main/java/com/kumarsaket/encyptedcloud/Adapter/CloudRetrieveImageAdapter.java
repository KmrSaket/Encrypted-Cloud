package com.kumarsaket.encyptedcloud.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
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
import com.kumarsaket.encyptedcloud.CustomClass.Upload;
import com.snatik.storage.Storage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class CloudRetrieveImageAdapter extends RecyclerView.Adapter<CloudRetrieveImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;
    private static final String TAG = "CloudRetrieveImageAdapt";

    public CloudRetrieveImageAdapter(Context mContext, List<Upload> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cloud_retrieval_image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
        final Upload uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getmName());

//        holder.imageView.setImageURI(Uri.parse(uploadCurrent.getmImageUrl()));

        Picasso.get()
                .load(uploadCurrent.getmImageUrl())
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Storage storage = new Storage(mContext);
                        StringBuilder keyPath = new StringBuilder();
                        keyPath.append(Environment.getExternalStorageDirectory())
                                .append(File.separator)
                                .append(mContext.getResources().getString(R.string.app_name))
                                .append(File.separator)
                                .append(mContext.getResources().getString(R.string.keyFolder))
                                .append(File.separator)
                                .append(uploadCurrent.getmKeyName());
                        String content = storage.readTextFile(keyPath.toString());

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
                            Log.d("bitmap", "onSuccess: " + originalbitmap.getWidth());
                            Log.d("bitmap", "onSuccess: " + originalbitmap.getHeight());
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
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

    }

    private int[][] extract2DpixelArrayss(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final boolean hasAlphaChannel = bitmap.hasAlpha();
        int[][] result = new int[height][width];
        /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        final byte[] pixels = stream.toByteArray();

        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel + 3 < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel + 2 < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }*/

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result[i][j] = bitmap.getPixel(j, i);
            }
        }
        return result;
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
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.image_item_name);
            imageView = itemView.findViewById(R.id.image_item_bitmap);
        }
    }

}
