package com.hashmal.tourapplication.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.hashmal.tourapplication.R;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.ImageHolder;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;

public class MapActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private MapView   mapView;
    private MapboxMap mapboxMap;
    private boolean   hasZoomedToUserLocation = false;

    //───────────────────────────────────────────────────────────────────────────
    // LIFECYCLE
    //───────────────────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView  = findViewById(R.id.mapView);
        mapboxMap = mapView.getMapboxMap();

        // Nếu đã có quyền → init; nếu chưa có → yêu cầu
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initMap();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    @Override protected void onStart()  { super.onStart();  mapView.onStart();  }
    @Override protected void onStop()   { mapView.onStop();   super.onStop();   }
    @Override protected void onDestroy(){ mapView.onDestroy(); super.onDestroy();}
    @Override public    void onLowMemory(){ super.onLowMemory(); mapView.onLowMemory(); }

    //───────────────────────────────────────────────────────────────────────────
    // PERMISSION CALLBACK
    //───────────────────────────────────────────────────────────────────────────
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initMap();                        // quyền đã cấp, khởi tạo bản đồ
        } else {
            Toast.makeText(this,
                    "Cần quyền vị trí để hiển thị bản đồ",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //───────────────────────────────────────────────────────────────────────────
    // MAP & LOCATION
    //───────────────────────────────────────────────────────────────────────────
    private void initMap() {
        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS, style -> enableUserLocation());
    }

    private void enableUserLocation() {
        LocationComponentPlugin locationPlugin =
                (LocationComponentPlugin) mapView.getPlugin(
                        Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);

        if (locationPlugin == null) {
            Toast.makeText(this,
                    "Không khởi tạo được LocationComponent",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // tạo puck (dấu chấm) – dùng Builder để an toàn cho Java
        LocationPuck2D puck = new LocationPuck2D();
        puck.setBearingImage(ImageHolder.from(R.drawable.ic_user_location));
        locationPlugin.setLocationPuck(puck);
        locationPlugin.setEnabled(true);

        // Lắng nghe vị trí hiện tại, zoom 1 lần
        locationPlugin.addOnIndicatorPositionChangedListener(point -> {
            if (!hasZoomedToUserLocation) {
                hasZoomedToUserLocation = true;
                mapboxMap.setCamera(new CameraOptions.Builder()
                        .center(point)
                        .zoom(15.0)
                        .build());
            }
        });
    }
}
