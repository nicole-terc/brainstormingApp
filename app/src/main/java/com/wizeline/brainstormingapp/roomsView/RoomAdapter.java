package com.wizeline.brainstormingapp.roomsView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wizeline.brainstormingapp.R;
import com.wizeline.brainstormingapp.repository.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicole Terc on 1/19/18.
 */

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    List<Room> items = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addRoom(Room room) {
        if (getRoomIndex(room) == -1) {
            items.add(room);
            notifyItemInserted(items.size() - 1);
        }
    }

    public void removeRoom(Room room) {
        int index = getRoomIndex(room);
        if (index != -1) {
            items.remove(index);
            notifyItemRemoved(index);
        }
    }

    public int getRoomIndex(Room room) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equalsIgnoreCase(room.getId())) {
                return i;
            }
        }
        return -1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.room_name);
            date = itemView.findViewById(R.id.room_date);
        }

        public void bind(Room room) {
            String titleText = room.getName();
            title.setText(titleText);
            date.setText(room.getHostEmail());
        }
    }
}
