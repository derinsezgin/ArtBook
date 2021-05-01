package com.derinsezgin.artbook.ui.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.derinsezgin.artbook.R;
import com.derinsezgin.artbook.data.Books;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {

    private List<Books> list;
    private OnItemClick listener;
    private Context context;

    public BooksAdapter(Context applicationContext, List<Books> list, OnItemClick listener) {
        this.context = applicationContext;
        this.list = list;
        this.listener=listener;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_books, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.title.setText(list.get(i).getTitle());
        viewHolder.writerName.setText(list.get(i).getWriterName());

        if(!list.get(i).getImage().isEmpty()){
            Picasso.get().load(list.get(i).getImage()).into(viewHolder.imageView);
        }
        else {
            viewHolder.imageView.setBackgroundResource(R.drawable.place_holder);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView writerName;
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            title =  view.findViewById(R.id.tvTitle);
            writerName =  view.findViewById(R.id.tvWriterName);
            imageView =  view.findViewById(R.id.imgIcon);

            view.setOnClickListener(v -> {
                final int pos = getAdapterPosition();

                if (pos != RecyclerView.NO_POSITION) {
                    Books clickedDataItem = list.get(pos);
                    listener.onClick(clickedDataItem);
                }
            });
        }
    }
}