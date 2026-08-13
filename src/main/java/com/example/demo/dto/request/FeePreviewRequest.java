/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.example.demo.dto.request;

import java.time.OffsetDateTime;
import java.util.List;

import com.example.demo.domain.DriverPing;
import com.example.demo.domain.EndReason;
import com.example.demo.domain.PickupPoint;

import lombok.Data;

/**
 *
 * @author Danny Fitriyana
 */

@Data
public class FeePreviewRequest {

    private OffsetDateTime arrivedAt;

    private OffsetDateTime endedAt;

    private EndReason endReason;

     private PickupPoint pickupPoint;

    private List<DriverPing> driverPings;
}
