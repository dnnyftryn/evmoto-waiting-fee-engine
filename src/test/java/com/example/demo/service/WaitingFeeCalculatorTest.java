/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.DriverPing;
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

    // ===== calculate waiting fee ======
    @Test
    void shouldChargeOneMinuteAtSixMinutes() {

        OffsetDateTime arrived
                = OffsetDateTime.parse("2026-08-10T09:00:00+07:00");

        OffsetDateTime ended
                = OffsetDateTime.parse("2026-08-10T09:06:00+07:00");

        FeePreviewRequest request
                = createRequest(arrived, ended, EndReason.TRIP_STARTED);

        FeeBreakdown result
                = calculator.calculate(request);

        assertEquals(1, result.getChargeableMinutes());
        assertEquals(500, result.getWaitingFee());
    }

    @Test
    void shouldCalculateWaitingFeeAfterTenMinutes() {

        OffsetDateTime arrived
                = OffsetDateTime.parse("2026-08-10T09:00:00+07:00");

        OffsetDateTime ended
                = OffsetDateTime.parse("2026-08-10T09:10:00+07:00");

        FeePreviewRequest request
                = createRequest(arrived, ended, EndReason.TRIP_STARTED);

        FeeBreakdown result
                = calculator.calculate(request);

        assertEquals(5, result.getChargeableMinutes());
        assertEquals(2500, result.getWaitingFee());
    }

    @Test
    void shouldRoundUpStartedMinuteWhenCalculatingWaitingFee() {

        OffsetDateTime arrived
                = OffsetDateTime.parse("2026-08-10T09:00:00+07:00");

        OffsetDateTime ended
                = OffsetDateTime.parse("2026-08-10T09:10:01+07:00");

        FeePreviewRequest request
                = createRequest(arrived, ended, EndReason.TRIP_STARTED);

        FeeBreakdown result
                = calculator.calculate(request);

        assertEquals(6, result.getChargeableMinutes());
        assertEquals(3000, result.getWaitingFee());
    }

    @Test
    void shouldNotChargeCancellationFeeBeforeFreeWaitingEnds() {

        OffsetDateTime arrived
                = OffsetDateTime.parse("2026-08-10T09:00:00+07:00");

        OffsetDateTime ended
                = OffsetDateTime.parse("2026-08-10T09:04:00+07:00");

        FeePreviewRequest request
                = createRequest(arrived, ended,
                        EndReason.CANCELLED_BY_CUSTOMER);

        FeeBreakdown result
                = calculator.calculate(request);

        assertEquals(0, result.getWaitingFee());
        assertEquals(0, result.getCancellationFee());
    }

    @Test
    void shouldChargeCancellationFeeAfterFreeWaitingEnds() {

        OffsetDateTime arrived
                = OffsetDateTime.parse("2026-08-10T09:00:00+07:00");

        OffsetDateTime ended
                = OffsetDateTime.parse("2026-08-10T09:10:00+07:00");

        FeePreviewRequest request
                = createRequest(arrived, ended,
                        EndReason.CANCELLED_BY_CUSTOMER);

        FeeBreakdown result
                = calculator.calculate(request);

        assertEquals(2500, result.getWaitingFee());
        assertEquals(7500, result.getCancellationFee());
        assertEquals(7500, result.getTotalFee());
    }

    @Test
    void shouldNotChargeCancellationFeeWhenDriverCancels() {

        OffsetDateTime arrived
                = OffsetDateTime.parse("2026-08-10T09:00:00+07:00");

        OffsetDateTime ended
                = OffsetDateTime.parse("2026-08-10T09:30:00+07:00");

        FeePreviewRequest request
                = createRequest(arrived, ended,
                        EndReason.CANCELLED_BY_DRIVER);

        FeeBreakdown result
                = calculator.calculate(request);

        assertEquals(0, result.getCancellationFee());
        assertEquals(0, result.getTotalFee());
    }

    // ====== waiting fee cap ======
    @Test
    void shouldApplyWaitingFeeCap() {

        OffsetDateTime arrived
                = OffsetDateTime.parse("2026-08-10T09:00:00+07:00");

        OffsetDateTime ended
                = OffsetDateTime.parse("2026-08-10T10:00:00+07:00");

        FeePreviewRequest request
                = createRequest(arrived, ended,
                        EndReason.TRIP_STARTED);

        FeeBreakdown result
                = calculator.calculate(request);

        assertEquals(15000, result.getWaitingFee());
        assertEquals(true, result.isWaitingFeeCapped());
    }

    // ===== cancelation fee cap =====
    @Test
    void shouldApplyCancellationFeeCap() {

        OffsetDateTime arrived
                = OffsetDateTime.parse("2026-08-10T09:00:00+07:00");

        OffsetDateTime ended
                = OffsetDateTime.parse("2026-08-10T10:00:00+07:00");

        FeePreviewRequest request
                = createRequest(arrived,
                        ended,
                        EndReason.CANCELLED_BY_CUSTOMER);

        FeeBreakdown result
                = calculator.calculate(request);

        assertEquals(20000, result.getCancellationFee());
        assertEquals(true, result.isCancellationFeeCapped());
    }

    // ===== gps pause =====
    @Test
    void shouldPauseWaitingTimeWhenDriverLeavesPickupArea() {

        FeePreviewRequest request = new FeePreviewRequest();

        request.setArrivedAt(
                OffsetDateTime.parse("2026-08-10T09:00:00+07:00"));

        request.setEndedAt(
                OffsetDateTime.parse("2026-08-10T09:21:40+07:00"));

        request.setEndReason(
                EndReason.CANCELLED_BY_CUSTOMER);

        request.setPickupPoint(
                new PickupPoint(-6.21462, 106.84513));

        List<DriverPing> pings = new ArrayList<>();

        pings.add(new DriverPing(
                OffsetDateTime.parse("2026-08-10T09:00:00+07:00"),
                -6.21462,
                106.84513));

        pings.add(new DriverPing(
                OffsetDateTime.parse("2026-08-10T09:08:00+07:00"),
                -6.21980,
                106.85110));

        pings.add(new DriverPing(
                OffsetDateTime.parse("2026-08-10T09:14:00+07:00"),
                -6.21470,
                106.84520));

        request.setDriverPings(pings);

        FeeBreakdown result
                = calculator.calculate(request);

        assertEquals(360, result.getPausedSeconds());
        assertEquals(940, result.getEffectiveWaitingSeconds());
    }
}
