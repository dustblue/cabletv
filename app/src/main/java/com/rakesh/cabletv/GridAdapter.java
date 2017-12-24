package com.rakesh.cabletv;

/**
 * Created by Rakesh on 16-12-2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private String[] items;
    private int[] itemImages;


    GridAdapter(String[] items, int[] itemImages) {
        this.items = items;
        this.itemImages = itemImages;
    }

    @Override
    public GridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridAdapter.ViewHolder holder, int position) {
        holder.itemTitle.setText(items[position]);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        if (itemImages != null) {
            holder.itemImage.setImageResource(itemImages[position]);
            holder.itemImage.setColorFilter(generator.getRandomColor());
        }
        else {
            String [] tiles = items[position].split(" ");
            StringBuilder t = new StringBuilder();
            for (String tile: tiles) {
                t.append(tile.charAt(0));
            }
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .bold()
                    .toUpperCase()
                    .width(180)
                    .height(180)
                    .endConfig()
                    .buildRound(t.toString(), generator.getRandomColor());

            holder.itemImage.setImageDrawable(drawable);
        }
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemTitle;
        private ImageView itemImage;

        ViewHolder(View view) {
            super(view);
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemImage = (ImageView) view.findViewById(R.id.item_image);
        }
    }
}
