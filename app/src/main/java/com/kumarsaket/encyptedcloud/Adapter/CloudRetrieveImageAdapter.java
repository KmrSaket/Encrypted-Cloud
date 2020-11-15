package com.kumarsaket.encyptedcloud.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kumarsaket.encyptedcloud.CustomClass.CloudRetrieveTask;
import com.kumarsaket.encyptedcloud.R;
import com.kumarsaket.encyptedcloud.CustomClass.Upload;

import java.util.List;

public class CloudRetrieveImageAdapter extends RecyclerView.Adapter<CloudRetrieveImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;

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

        CloudRetrieveTask cloudRetrieveTask = new CloudRetrieveTask(holder.imageView,
                mContext, holder.textViewName, holder.progressBar);
        cloudRetrieveTask.execute(mUploads.get(position));

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imageView;
        public ProgressBar progressBar;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.image_item_name);
            imageView = itemView.findViewById(R.id.image_item_bitmap);
            progressBar = itemView.findViewById(R.id.cloudLoading);
        }
    }

}
