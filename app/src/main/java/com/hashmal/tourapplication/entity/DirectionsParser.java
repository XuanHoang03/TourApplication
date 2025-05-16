package com.hashmal.tourapplication.entity;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DirectionsParser {
    public static LineString parseLineStringFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray routes = jsonObject.getJSONArray("routes");
            if (routes.length() > 0) {
                JSONObject route = routes.getJSONObject(0);
                JSONObject geometry = route.getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");

                List<Point> points = new ArrayList<>();
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray coord = coordinates.getJSONArray(i);
                    double lon = coord.getDouble(0);
                    double lat = coord.getDouble(1);
                    points.add(Point.fromLngLat(lon, lat));
                }

                return LineString.fromLngLats(points);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
