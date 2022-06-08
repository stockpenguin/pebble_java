package com.stockpenguin.pebble.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stockpenguin.pebble.R;
import com.stockpenguin.pebble.activities.MessagingActivity;
import com.stockpenguin.pebble.utils.SearchDialogDataHolder;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {
    public static SearchDialogDataHolder dataHolder;

    public SearchUserAdapter(SearchDialogDataHolder dataHolder) {
        this.dataHolder = dataHolder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_user_adapter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setTextView(dataHolder.getUsername(position));
        holder.setImageViewSrc(dataHolder.getPhotoUrl(position));
    }

    @Override
    public int getItemCount() {
        return dataHolder.getPhotoUrls().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final CircleImageView imageView;
        private final TextView textView;
        private Intent i;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.searchUserAdapterImageView);
            textView = itemView.findViewById(R.id.searchUserAdapterUsername);
            i = new Intent(itemView.getContext(), MessagingActivity.class);

            itemView.setOnClickListener(this);
        }

        public CircleImageView getImageView() {
            return imageView;
        }

        public TextView getTextView() {
            return textView;
        }

        public void setImageViewSrc(String url) {
            Glide.with(itemView)
                    .load(url)
                    .placeholder(R.color.pastel_blue)
                    .into(imageView);
        }

        public void setTextView(String s) {
            textView.setText(s);
        }

        @Override
        public void onClick(View view) {
            i.putExtra("data", dataHolder);
            System.out.println(dataHolder);
            view.getContext().startActivity(i);
        }
    }
}
