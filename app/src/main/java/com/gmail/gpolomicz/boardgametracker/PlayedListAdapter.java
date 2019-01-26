package com.gmail.gpolomicz.boardgametracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PlayedListAdapter extends ArrayAdapter {
    private static final String TAG = "GPDEB";

    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<PlayedEntry> playedEntry;

    PlayedListAdapter(Context context, int resource, List<PlayedEntry> playedEntry) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.playedEntry = playedEntry;
    }

    @Override
    public int getCount() {
        return playedEntry.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PlayedEntry currentInfo = playedEntry.get(position);

        viewHolder.name.setText(currentInfo.getGame().getName());
        viewHolder.date.setText(currentInfo.getDate());

        if(currentInfo.getGame().getImage() != null) {
            Picasso.get().load("file://" + currentInfo.getGame().getImage()).into(viewHolder.image);
        } else {
            viewHolder.image.setImageResource(R.drawable.no_image);
        }
        return convertView;
    }

    public PlayedEntry getPlayed(int position) {
        return ((playedEntry != null) && (playedEntry.size() != 0) ? playedEntry.get(position) : null);
    }

    private class ViewHolder {
        final ImageView image;
        final TextView name, date;

        ViewHolder(View v) {
            this.image = v.findViewById(R.id.player_image);
            this.name = v.findViewById(R.id.player_name);
            this.date = v.findViewById(R.id.date_played);
        }
    }
}

