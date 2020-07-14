package com.alltrails.android.restaurants.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alltrails.android.restaurants.R;
import com.alltrails.android.restaurants.model.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.alltrails.android.restaurants.Utils.BASE_URL;
import static com.alltrails.android.restaurants.Utils.DOLLAR_SIGN;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    List<Restaurant> dataset;
    OnPageListener onPageListener;
    Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.repo_list_item,viewGroup,false);
        return new ViewHolder(itemView, onPageListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        Restaurant currentRestaurant = dataset.get(position);

        if (currentRestaurant.getName() != null) {
            viewHolder.repoTitle.setText(currentRestaurant.getName());
        }

        if (currentRestaurant.getPhotos().size() > 0) {
            Picasso.with(context).load(constructPhotoReference(currentRestaurant.getPhotos().get(0).getPhotoReference())).resize(300, 300).into(viewHolder.restaurantImage);
        }

        if (currentRestaurant.getRating() != null) {
            viewHolder.ratingBar.setRating(currentRestaurant.getRating());
        }

        if (currentRestaurant.getUserRatingsTotal() !=  null) {
            String rating = "(" + currentRestaurant.getUserRatingsTotal() + ")";
            viewHolder.numberRatings.setText(rating);
        }

        if (currentRestaurant.getPriceLevel() != null) {
            String price = new String(new char[currentRestaurant.getPriceLevel()]).replace("\0", DOLLAR_SIGN);
            viewHolder.otherInfo.setText(price);

            if (currentRestaurant.getOpeningHours() != null) {
                viewHolder.otherInfo.append(" • ");

                String open = currentRestaurant.getOpeningHours().getOpenNow() ? "Open Now" : "Closed";

                viewHolder.otherInfo.append(open);
            }
        } else {
            if (currentRestaurant.getOpeningHours() != null) {
                String open = currentRestaurant.getOpeningHours().getOpenNow() ? "Open Now" : "Closed";

                viewHolder.otherInfo.setText(open);
            }
        }


    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView repoTitle, repoSubTitle, numberRatings, otherInfo;
        ImageView restaurantImage;
        RatingBar ratingBar;
        OnPageListener onPageListener;

        public ViewHolder(View view, OnPageListener onPageListener) {
            super(view);
            repoTitle = view.findViewById(R.id.restaurant_title);
            repoSubTitle = view.findViewById(R.id.reposubtitle);
            restaurantImage = view.findViewById(R.id.restaurant_image);
            ratingBar = view.findViewById(R.id.rating_bar);
            numberRatings = view.findViewById(R.id.number_ratings);
            otherInfo = view.findViewById(R.id.other_info);

            this.onPageListener = onPageListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPageListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnPageListener {
        void onItemClick(int index);
    }

    public void setOnPageListener(OnPageListener onPageListener) {
        this.onPageListener = onPageListener;
    }

    public void setDataset(List<Restaurant> dataset){
        this.dataset = dataset;
        notifyDataSetChanged();
    }

    public void filterDataset(String searchFilter) {
        this.dataset = dataset.stream().filter(p -> p.getName().toLowerCase().contains(searchFilter.toLowerCase())).collect(Collectors.toList());

        notifyDataSetChanged();
    }

    public ListAdapter(Context context) {
        dataset = new ArrayList<>();
        this.context = context;
    }

    private String constructPhotoReference(String photoReference) {

        return BASE_URL + "/maps/api/place/photo?photoreference=" + photoReference
                + "&sensor=false&maxheight=300&maxwidth=300&key=" + context.getString(R.string.google_maps_key);
    }
}
