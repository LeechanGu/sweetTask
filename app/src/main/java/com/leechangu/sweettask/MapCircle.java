package com.leechangu.sweettask;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Scanner;

/**
 * Created by Administrator on 2015/10/16.
 */
public class MapCircle implements Serializable {
    public static final String NO_MAP = "NO_MAP";
    private Circle circle;
    public MapCircle(Circle circle) {
        this.circle = circle;
    }

    public static Circle setLatLongRadius(Circle circle, String latLongRadius) {
        Scanner sc = new Scanner(latLongRadius);
        double lat = sc.nextDouble(),
                lng = sc.nextDouble(),
                radius = sc.nextDouble();
        circle.setCenter(new LatLng(lat, lng));
        circle.setRadius(radius);
        return circle;
    }

    public static LatLng getLatLngFromString(String latLongRadius)
    {
        Scanner sc = new Scanner(latLongRadius);
        double lat = sc.nextDouble(),
                lng = sc.nextDouble();
        return new LatLng(lat, lng);
    }

    public double getRadius()
    {
        return circle.getRadius();
    }

    public void setRadius(double radius)
    {
        circle.setRadius(radius);
    }

    public LatLng getCenter()
    {
        return circle.getCenter();
    }

    public void setCenter(LatLng center)
    {
        circle.setCenter(center);
    }

    @Override
    public String toString() {
        return circle.getCenter().latitude + " " + circle.getCenter().longitude + " " + circle.getRadius();
    }
}
