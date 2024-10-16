package com.example.avaliaon1;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.GnssStatus;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SatelliteSignalAdapter extends RecyclerView.Adapter<SatelliteSignalAdapter.ViewHolder> {
    private GnssStatus status;
    private String filteredConstellations = "All";
    private boolean filterUsedInFix = false;
    private static final int MAX_SIGNAL_STRENGTH = 20; // Ajuste conforme necessário
    private List<Integer> filteredSatelliteIndices = new ArrayList<>();

    public SatelliteSignalAdapter(GnssStatus status) {
        this.status = status;
        filterSatellites();
    }

    public void setFilters(String filteredConstellations, boolean filterUsedInFix) {
        this.filteredConstellations = filteredConstellations;
        this.filterUsedInFix = filterUsedInFix;
        filterSatellites();
        notifyDataSetChanged();
    }

    public void setStatus(GnssStatus status) {
        this.status = status;
        filterSatellites();
        notifyDataSetChanged();
    }

    private void filterSatellites() {
        filteredSatelliteIndices.clear();
        if (status != null) {
            for (int i = 0; i < status.getSatelliteCount(); i++) {
                boolean shouldDraw = filteredConstellations.equals("All") ||
                        getConstellationName(status.getConstellationType(i)).equals(filteredConstellations);
                if (shouldDraw && (!filterUsedInFix || (filterUsedInFix && status.usedInFix(i)))) {
                    filteredSatelliteIndices.add(i);
                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_satellite_signal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (status != null && position < filteredSatelliteIndices.size()) {
            int i = filteredSatelliteIndices.get(position);
            float signal = status.getCn0DbHz(i);
            holder.bind(signal, formatId(status.getSvid(i)), status.getConstellationType(i));
        }
    }

    @Override
    public int getItemCount() {
        return filteredSatelliteIndices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View signalBar;
        private TextView satelliteId;
        private ImageView constellationIcon; // ImageView da bandeira

        public ViewHolder(View itemView) {
            super(itemView);
            signalBar = itemView.findViewById(R.id.signal_bar);
            satelliteId = itemView.findViewById(R.id.satellite_id);
            constellationIcon = itemView.findViewById(R.id.constellation_icon); // ImageView da bandeira
            signalBar.setBackgroundColor(Color.YELLOW);
        }

        public void bind(float signal, String id, int constellationType) {
            ViewGroup.LayoutParams layoutParams = signalBar.getLayoutParams();
            layoutParams.height = (int) ((signal / MAX_SIGNAL_STRENGTH) * 150); // Ajuste conforme necessário
            if (layoutParams.height == 0) layoutParams.height = 5;
            signalBar.setLayoutParams(layoutParams);
            satelliteId.setText(id);

            // Definir a bandeira da constelação
            Drawable flag = getConstellationIcon(constellationType);
            constellationIcon.setImageDrawable(flag);
        }

        private Drawable getConstellationIcon(int constellationType) {
            switch (constellationType) {
                case GnssStatus.CONSTELLATION_GPS:
                    return itemView.getContext().getDrawable(R.drawable.eua);
                case GnssStatus.CONSTELLATION_GLONASS:
                    return itemView.getContext().getDrawable(R.drawable.russia);
                case GnssStatus.CONSTELLATION_BEIDOU:
                    return itemView.getContext().getDrawable(R.drawable.china);
                case GnssStatus.CONSTELLATION_GALILEO:
                    return itemView.getContext().getDrawable(R.drawable.eu);
                case GnssStatus.CONSTELLATION_QZSS:
                    return itemView.getContext().getDrawable(R.drawable.japao);
                default:
                    return null;
            }
        }
    }

    private String formatId(int id) {
        return id < 10 ? "0" + id : "" + id;
    }

    private String getConstellationName(int constellationType) {
        switch (constellationType) {
            case GnssStatus.CONSTELLATION_GPS:
                return "GPS";
            case GnssStatus.CONSTELLATION_GLONASS:
                return "Glonass";
            case GnssStatus.CONSTELLATION_BEIDOU:
                return "Beidou";
            case GnssStatus.CONSTELLATION_GALILEO:
                return "Galileo";
            case GnssStatus.CONSTELLATION_QZSS:
                return "QZSS";
            default:
                return "Unknown";
        }
    }
}
