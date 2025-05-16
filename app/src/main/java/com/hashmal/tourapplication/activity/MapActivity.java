package com.hashmal.tourapplication.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                LatLng destinationLatLng = new LatLng(21.0073, 105.823); // Tọa độ của Đại học Thủy Lợi
        Point userPoint = Point.fromLngLat(userLatLng.getLongitude(), userLatLng.getLatitude());
        Point destinationPoint = Point.fromLngLat(destinationLatLng.getLongitude(), destinationLatLng.getLatitude());
        List<Point> list = List.of(userPoint, destinationPoint);
                getRoute(list);
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

    private void getRoute(List<Point> points) {
        if (points == null || points.size() < 2) {
            Toast.makeText(this, "Cần ít nhất 2 điểm để vẽ đường đi", Toast.LENGTH_SHORT).show();
            return;
        }

        MapboxDirections.Builder builder = MapboxDirections.builder()
                .origin(points.get(0))
                .destination(points.get(points.size()-1))
                .accessToken(getString(R.string.mapbox_access_token))
                .geometries(DirectionsCriteria.GEOMETRY_POLYLINE6);

        for (int i = 1; i < points.size() - 1; i++) {
            builder.addWaypoint(points.get(i));
        }
        MapboxDirections client = builder.build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() != null && !response.body().routes().isEmpty()) {
                    DirectionsRoute route = response.body().routes().get(0);
                    drawRoute(route);
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Lỗi khi lấy lộ trình: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawRoute(DirectionsRoute route) {
        LineString lineString = LineString.fromPolyline(route.geometry(),6);
        List<Point> coordinates = lineString.coordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {

            double a = coordinates.get(i).latitude();
            double b = coordinates.get(i).longitude();
            points[i] = new LatLng(a, b, 0);
        }

        mapboxMap.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("#3887be"))
                .width(5));
    }
}
