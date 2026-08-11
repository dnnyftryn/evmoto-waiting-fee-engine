/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.util;

/**
 *
 * @author dolphin
 */
public final class DistanceUtil {

    private static final double EARTH_RADIUS = 6371000;

    /**
     * Menghitung jarak dua titik koordinat dengan satuan meter
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static double distanceInMeters(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2))
                    * Math.sin(lonDistance / 2)
                    * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * Cek apakah driver sudah dalam radius tertentu
     * 
     * @param driverLat
     * @param driverLon
     * @param pickupLat
     * @param pickupLon
     * @param radiusMeter
     * @return
     */
    public static boolean isWithinRadius(
            double driverLat,
            double driverLon,
            double pickupLat,
            double pickupLon,
            double radiusMeter) {

        return distanceInMeters(
                driverLat,
                driverLon,
                pickupLat,
                pickupLon
        ) <= radiusMeter;
    }
}
