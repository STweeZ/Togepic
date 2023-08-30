package com.example.myapplication.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.UIMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.net.HttpURLConnection;

public class MapFragment extends DialogFragment {
    public static String TAG = "MapFragment";
    public static MapFragment newInstance() {
        MapFragment f = new MapFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_map, null);
        builder.setView(v);
        setListeners(v);

        MapView mapView = v.findViewById(R.id.mapView);

        if (mapView != null) {
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setBuiltInZoomControls(true);
            mapView.setMultiTouchControls(true);

            IMapController mapController = mapView.getController();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getContext());

            GeoPoint startPoint = new GeoPoint(sp.getInt("x", -1), 	sp.getInt("y", -1));
            Marker marker = new Marker(mapView);
            marker.setPosition(startPoint);
            mapView.getOverlays().add(marker);
            mapController.setZoom(7);
            mapController.setCenter(startPoint);
        }

        return builder.create();
    }

    private void setListeners(View v) {

    }
}
