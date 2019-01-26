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

public class SelectPlayersAdapter extends ArrayAdapter {
    private static final String TAG = "GPDEB";

    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<PlayerEntry> playerEntry;

    SelectPlayersAdapter(Context context, int resource, List<PlayerEntry> playerEntry) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.playerEntry = playerEntry;
    }

    @Override
    public int getCount() {
        return playerEntry.size();
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

        PlayerEntry currentInfo = playerEntry.get(position);

        viewHolder.name.setText(currentInfo.getName());

        if(playerEntry.get(position).isChecked()) {
            viewHolder.checked.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checked.setVisibility(View.INVISIBLE);
        }

        if(currentInfo.getImage() != null) {
            Picasso.get().load("file://" + currentInfo.getImage()).into(viewHolder.image);
        }
        return convertView;
    }

    public PlayerEntry getPlayer(int position) {
        return ((playerEntry != null) && (playerEntry.size() != 0) ? playerEntry.get(position) : null);
    }

    private class ViewHolder {
        final ImageView image, checked;
        final TextView name;

        ViewHolder(View v) {
            this.image = v.findViewById(R.id.player_image);
            this.name = v.findViewById(R.id.player_name);
            this.checked = v.findViewById(R.id.checkBox);
        }
    }
}
