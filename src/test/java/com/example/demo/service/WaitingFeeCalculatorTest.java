/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.EndReason;
import com.example.demo.domain.FeeBreakdown;
import com.example.demo.domain.PickupPoint;
import com.example.demo.dto.request.FeePreviewRequest;

/**
 *
 * @author dolphin
 */
class WaitingFeeCalculatorTest {

    private WaitingFeeCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new WaitingFeeCalculator();
    }

    private FeePreviewRequest createRequest(
            OffsetDateTime arrivedAt,
            OffsetDateTime endedAt,
            EndReason endReason) {

        FeePreviewRequest request = new FeePreviewRequest();

        request.setArrivedAt(arrivedAt);
        request.setEndedAt(endedAt);
        request.setEndReason(endReason);

        request.setPickupPoint(
                new PickupPoint(-6.21462, 106.84513));

        request.setDriverPings(new ArrayList<>());

        return request;
    }

    @Test
    void shouldNotChargeWaitingFeeWithinFreeWaitingPeriod() {

        OffsetDateTime arrived
                = OffsetDateTime.parse("2026-08-10T09:00:00+07:00");

        OffsetDateTime ended
                = OffsetDateTime.parse("2026-08-10T09:04:59+07:00");

        FeePreviewRequest request
                = createRequest(arrived, ended, EndReason.TRIP_STARTED);

        FeeBreakdown result
                = calculator.calculate(request);

        assertEquals(0, result.getWaitingFee());
        assertEquals(0, result.getChargeableMinutes());
        assertEquals(0, result.getCancellationFee());
    }

    @Test
    void shouldNotChargeWaitingFeeAtExactlyFiveMinutes() {

        OffsetDateTime arrived
                = OffsetDateTime.parse("2026-08-10T09:00:00+07:00");

        OffsetDateTime ended
                = OffsetDateTime.parse("2026-08-10T09:05:00+07:00");

        FeePreviewRequest request
                = createRequest(arrived, ended, EndReason.TRIP_STARTED);

        FeeBreakdown result
                = calculator.calculate(request);

        assertEquals(0, result.getWaitingFee());
        assertEquals(0, result.getChargeableMinutes());
    }

    @Test
    void shouldChargeOneMinuteAfterFreeWaitingEnds() {

        OffsetDateTime arrived
                = OffsetDateTime.parse("2026-08-10T09:00:00+07:00");

        OffsetDateTime ended
                = OffsetDateTime.parse("2026-08-10T09:05:01+07:00");

        FeePreviewRequest request
                = createRequest(arrived, ended, EndReason.TRIP_STARTED);

        FeeBreakdown result
                = calculator.calculate(request);

        assertEquals(1, result.getChargeableMinutes());
        assertEquals(500, result.getWaitingFee());
    }
}
