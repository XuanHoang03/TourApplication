package com.hashmal.tourapplication.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.hashmal.tourapplication.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MapActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_LOCATION = 100;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView.getMapAsync(map -> {
            mapboxMap = map;
            mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                setupMapClickListener();
                addThuyLoiMarker();
                checkLocationPermissionAndShowUserLocation();
            });
        });
    }

    private void setupMapClickListener() {
        mapboxMap.addOnMapClickListener(point -> {
            double lat = point.getLatitude();
            double lng = point.getLongitude();
            Toast.makeText(this, "Tọa độ: " + lat + ", " + lng, Toast.LENGTH_SHORT).show();

            mapboxMap.addMarker(new MarkerOptions()
                    .position(point)
                    .title("Bạn đã bấm ở đây"));
        });
    }

    private void addThuyLoiMarker() {
        LatLng thuylLoiLatLng = new LatLng(21.0073, 105.823);
        mapboxMap.addMarker(new MarkerOptions()
                .position(thuylLoiLatLng)
                .title("Đại học Thủy Lợi")
                .snippet("105.823, 21.0073"));
    }

    private void checkLocationPermissionAndShowUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        } else {
            showUserLocation();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void showUserLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                LatLng userLatLng = new LatLng(lat, lon);

                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14));
                mapboxMap.addMarker(new MarkerOptions()
                        .position(userLatLng)
                        .title("Vị trí hiện tại của bạn"));

                Toast.makeText(this, "Vị trí hiện tại: " + lat + ", " + lon, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Không thể lấy vị trí hiện tại.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi khi lấy vị trí: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_LOCATION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showUserLocation();
        } else {
            Toast.makeText(this, "Quyền vị trí bị từ chối", Toast.LENGTH_SHORT).show();
        }
    }

    // MapView lifecycle
    @Override protected void onStart() { super.onStart(); mapView.onStart(); }
    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onPause() { super.onPause(); mapView.onPause(); }
    @Override protected void onStop() { super.onStop(); mapView.onStop(); }
    @Override protected void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
