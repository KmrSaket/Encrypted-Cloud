package com.kumarsaket.encyptedcloud.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kumarsaket.encyptedcloud.CustomClass.LocalRetrieveTask;
import com.kumarsaket.encyptedcloud.R;
import com.kumarsaket.encyptedcloud.RubiksCubeAlgo.Decryption;
import com.kumarsaket.encyptedcloud.CustomClass.LocalFiles;
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
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
        LocalRetrieveTask localRetrieveTask = new LocalRetrieveTask(holder.imageView, mContext,
                holder.textViewName, holder.progressBar);
        localRetrieveTask.execute(localFiles.get(position));
    }

    @Override
    public int getItemCount() {
        return localFiles.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imageView;
        public ProgressBar progressBar;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.localimage_item_name);
            imageView = itemView.findViewById(R.id.localimage_item_bitmap);
            progressBar = itemView.findViewById(R.id.LSAcloudLoading);
        }
    }
}
