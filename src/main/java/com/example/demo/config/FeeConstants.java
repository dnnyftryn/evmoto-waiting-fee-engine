/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.config;

/**
 *
 * @author dolphin
 */
public final class FeeConstants {

    /**
     * Free waiting time (minutes)
     */
    public static final int FREE_WAITING_MINUTES = 5;

    /**
     * Waiting fee per started minute after free waiting
     */
    public static final int WAITING_FEE_PER_MINUTE = 500;

    /**
     * Maximum waiting fee
     */
    public static final int WAITING_FEE_CAP = 15_000;

    /**
     * Additional cancellation fee
     */
    public static final int CANCELLATION_SURCHARGE = 5_000;

    /**
     * Maximum cancellation fee
     */
    public static final int CANCELLATION_FEE_CAP = 20_000;

    /**
     * Maximum distance from pickup point before timer is paused
     */
    public static final int PICKUP_RADIUS_METERS = 100;
}
