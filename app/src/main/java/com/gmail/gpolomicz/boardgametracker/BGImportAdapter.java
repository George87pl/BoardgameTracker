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

public class BGImportAdapter extends ArrayAdapter {
    private static final String TAG = "GPDEB";

    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<BGEntry> bgEntry;

    BGImportAdapter(Context context, int resource, List<BGEntry> bgEntry) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.bgEntry = bgEntry;
    }

    @Override
    public int getCount() {
        return bgEntry.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BGEntry currentInfo = bgEntry.get(position);

        viewHolder.name.setText(currentInfo.getName());
        viewHolder.date.setText(currentInfo.getPubDate());

        Picasso.get().load(currentInfo.getImage()).into(viewHolder.image);

        if(bgEntry.get(position).isChecked()) {
            viewHolder.isChecked.setImageResource(R.drawable.check);
        } else {
            viewHolder.isChecked.setImageResource(R.drawable.uncheck);
        }

        viewHolder.isChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgEntry.get(position).setIschecked();

                if(bgEntry.get(position).isChecked()) {
                    viewHolder.isChecked.setImageResource(R.drawable.check);
                } else {
                    viewHolder.isChecked.setImageResource(R.drawable.uncheck);
                }
            }
        });

        return convertView;
    }

    public BGEntry getGame(int position) {
        return ((bgEntry != null) && (bgEntry.size() != 0) ? bgEntry.get(position) : null);
    }

    private class ViewHolder {
        final TextView name;
        final TextView date;
        final ImageView image;
        final ImageView isChecked;

        ViewHolder(View v) {
            this.name = v.findViewById(R.id.player_name);
            this.date = v.findViewById(R.id.player_last_play_label);
            this.image = v.findViewById(R.id.player_image);
            this.isChecked = v.findViewById(R.id.checkBox);
        }
    }
}

