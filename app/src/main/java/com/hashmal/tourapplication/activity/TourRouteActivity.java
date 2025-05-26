package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.service.dto.DisplayBookingDTO;
import com.hashmal.tourapplication.service.dto.YourTourDTO;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourRouteActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    List<Point> points = new ArrayList<>();
    private static final int PERMISSIONS_REQUEST_LOCATION = 100;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private ImageButton imageButton;
    private FusedLocationProviderClient fusedLocationClient;
    private List<YourTourDTO.TourLocation> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_tour_route);

        imageButton = findViewById(R.id.btnBack);
        imageButton.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        String locationString = getIntent().getStringExtra("locations");
        Type locationType = new TypeToken<List<YourTourDTO.TourLocation>>() {
        }.getType();
        locations = gson.fromJson(locationString, locationType);
        if (Objects.nonNull(locations)) {
            locations.sort(Comparator.comparing(YourTourDTO.TourLocation::getVisitOrder));
            for (YourTourDTO.TourLocation location : locations) {
                double longitude = location.getLocation().getLongitude();
                double latitude = location.getLocation().getLatitude();
                Point point = Point.fromLngLat(longitude, latitude);
                points.add(point);
            }
        }



        mapView.getMapAsync(map -> {
            mapboxMap = map;
            mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
//                setupMapClickListener();
                addAllMarkers(locations);
                getRoute(points);
                LatLngBounds vietnamBounds = new LatLngBounds.Builder()
                        .include(new LatLng(23.393395, 102.144596)) // Điểm cực Bắc
                        .include(new LatLng(8.560699, 109.464638))  // Điểm cực Nam
                        .build();

// Áp dụng bounds với padding
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                        vietnamBounds,
                        100 // padding (pixel)
                ));
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
    private void addAllMarkers(List<YourTourDTO.TourLocation> locations) {
        for (YourTourDTO.TourLocation location : locations) {
            addCustomMarker(location);
        }
    }


    private void addCustomMarker(YourTourDTO.TourLocation location) {
        LatLng value = new LatLng(location.getLocation().getLatitude(), location.getLocation().getLongitude());
        mapboxMap.addMarker(new MarkerOptions()
                .position(value)
                        .title(location.getVisitOrder() + " - " + location.getLocation().getName())
                .snippet(location.getLocation().getFullAddress()));
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
                .geometries(DirectionsCriteria.GEOMETRY_POLYLINE6)
                .alternatives(true).steps(true)
                .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                ;

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
                Toast.makeText(TourRouteActivity.this, "Lỗi khi lấy lộ trình: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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