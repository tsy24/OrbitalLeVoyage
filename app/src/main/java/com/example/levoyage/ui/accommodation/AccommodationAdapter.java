package com.example.levoyage.ui.accommodation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.levoyage.R;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * AccommodationAdapter class is an adapter for
 * recycler views used in the accommodation fragment.
 */
public class AccommodationAdapter extends RecyclerView.Adapter<AccommodationAdapter.AccommodationViewHolder> {

    Context context;

    ArrayList<AccommodationItineraryItem> list;

    public AccommodationAdapter(Context context, ArrayList<AccommodationItineraryItem> list) {
        this.context = context;
        this.list = list;
    }

    public static class AccommodationViewHolder extends RecyclerView.ViewHolder{

        TextView name, address, rating, price;
        ImageView image;
        ConstraintLayout layout;

        public AccommodationViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.location);
            address = itemView.findViewById(R.id.itemAddress);
            rating = itemView.findViewById(R.id.itemRating);
            price = itemView.findViewById(R.id.itemPrice);
            image = itemView.findViewById(R.id.itemImage);
            layout = itemView.findViewById(R.id.itemLayout);
        }
    }

    @NonNull
    @NotNull
    @Override
    public AccommodationViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.search_result, parent,false);
        return new AccommodationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AccommodationViewHolder holder, int position) {
        AccommodationItineraryItem item = list.get(position);
        holder.name.setText(item.getLocation());
        holder.address.setVisibility(View.GONE);
        holder.rating.setText(item.getRating());
        holder.price.setText(item.getPrice());
        Picasso.get().load(item.getImageURL())
                .placeholder(R.drawable.error_placeholder_small).fit().into(holder.image);

        holder.layout.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("Accommodation", item);
            bundle.putBoolean("Saved", false);
            Navigation.findNavController(v).navigate(
                    R.id.action_nav_accommodation_to_accommodationDetailFragment, bundle);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
