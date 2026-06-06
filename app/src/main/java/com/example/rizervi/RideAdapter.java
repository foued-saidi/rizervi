package com.example.rizervi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private List<Ride> rideList;
    private OnRideClickListener listener;
    private OnDriverClickListener driverListener;
    private boolean showBookButton = true;

    public interface OnRideClickListener {
        void onBookClick(Ride ride);
    }

    public interface OnDriverClickListener {
        void onDriverClick(Ride ride);
    }

    public RideAdapter(List<Ride> rideList, OnRideClickListener listener, OnDriverClickListener driverListener) {
        this.rideList = rideList;
        this.listener = listener;
        this.driverListener = driverListener;
    }

    public void setShowBookButton(boolean show) {
        this.showBookButton = show;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_item, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        holder.tvDriver.setText(ride.getDriverName());
        holder.tvDriver.setOnClickListener(v -> driverListener.onDriverClick(ride));

        holder.tvRating.setText(String.format("%.1f ★", ride.getRating()));
        holder.tvRoute.setText(ride.getDeparture() + " → " + ride.getDestination());
        holder.tvTime.setText(ride.getTime());
        holder.tvDate.setText(ride.getDate());
        holder.tvCar.setText(ride.getCarBrand());

        holder.tvSeats.setText(holder.itemView.getContext().getString(R.string.seats_available, ride.getAvailableSeats()));
        holder.tvPrice.setText(String.format("%.2f TND", ride.getPrice()));

        if (!showBookButton) {
            holder.btnBook.setVisibility(View.GONE);
        } else {
            holder.btnBook.setVisibility(View.VISIBLE);
            if (ride.getAvailableSeats() <= 0) {
                holder.btnBook.setEnabled(false);
                holder.btnBook.setText(R.string.ride_full);
            } else {
                holder.btnBook.setEnabled(true);
                holder.btnBook.setText(R.string.book_button);
            }
        }

        holder.btnBook.setOnClickListener(v -> listener.onBookClick(ride));
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public void updateList(List<Ride> newList) {
        this.rideList = newList;
        notifyDataSetChanged();
    }



    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvDriver, tvRating, tvRoute, tvTime, tvDate, tvCar, tvPrice, tvSeats;
        Button btnBook;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDriver = itemView.findViewById(R.id.tvDriver);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCar = itemView.findViewById(R.id.tvCar);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}
