/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.config.FeeConstants;
import com.example.demo.domain.DriverPing;
import com.example.demo.domain.EndReason;
import com.example.demo.domain.FeeBreakdown;
import com.example.demo.domain.PickupPoint;
import com.example.demo.dto.request.FeePreviewRequest;
import com.example.demo.util.DistanceUtil;

/**
 *
 * @author dolphin
 */
@Service
public class WaitingFeeCalculator {

    public FeeBreakdown calculate(FeePreviewRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request must not be null");
        }

        if (request.getArrivedAt() == null || request.getEndedAt() == null) {
            throw new IllegalArgumentException("ArrivedAt and EndedAt are required");
        }

        long totalWaitingSeconds = calculateTotalWaitingSeconds(
                request.getArrivedAt(),
                request.getEndedAt());

        long pausedSeconds = calculatePausedSeconds(
                request.getPickupPoint(),
                request.getDriverPings(),
                request.getEndedAt());

        long effectiveWaitingSeconds = calculateEffectiveWaitingSeconds(
                totalWaitingSeconds,
                pausedSeconds);

        int chargeableMinutes = calculateChargeableMinutes(
                effectiveWaitingSeconds);

        int waitingFee = calculateWaitingFee(
                chargeableMinutes);

        int cancellationFee = calculateCancellationFee(
                request.getEndReason(),
                effectiveWaitingSeconds,
                waitingFee);

        int totalFee = request.getEndReason() == EndReason.TRIP_STARTED
                ? waitingFee
                : cancellationFee;

        return FeeBreakdown.builder()
                .totalWaitingSeconds(totalWaitingSeconds)
                .pausedSeconds(pausedSeconds)
                .effectiveWaitingSeconds(effectiveWaitingSeconds)
                .chargeableMinutes(chargeableMinutes)
                .waitingFee(waitingFee)
                .cancellationFee(cancellationFee)
                .totalFee(totalFee)
                .waitingFeeCapped(waitingFee >= FeeConstants.WAITING_FEE_CAP)
                .cancellationFeeCapped(cancellationFee >= FeeConstants.CANCELLATION_FEE_CAP)
                .build();
    }

    /**
     * calculate total waiting in seconds
     */
    private long calculateTotalWaitingSeconds(
            OffsetDateTime arrivedAt,
            OffsetDateTime endedAt) {

        return Duration.between(arrivedAt, endedAt).getSeconds();
    }

    /**
     * Calculates paused waiting time while driver is outside the pickup radius.
     */
    private long calculatePausedSeconds(
            PickupPoint pickupPoint,
            List<DriverPing> pings,
            OffsetDateTime endedAt) {

        if (pickupPoint == null || pings == null || pings.isEmpty()) {
            return 0;
        }

        long pausedSeconds = 0;

        boolean paused = false;
        OffsetDateTime pauseStartedAt = null;

        for (DriverPing ping : pings) {
            boolean outsideRadius = !DistanceUtil.isWithinRadius(
                    ping.getLat(),
                    ping.getLng(),
                    pickupPoint.getLat(),
                    pickupPoint.getLng(),
                    FeeConstants.PICKUP_RADIUS_METERS);

            if (outsideRadius && !paused) {
                paused = true;
                pauseStartedAt = ping.getAt();
                continue;
            }

            if (!outsideRadius && paused) {
                pausedSeconds += Duration.between(
                        pauseStartedAt,
                        ping.getAt())
                        .getSeconds();

                paused = false;
                pauseStartedAt = null;
            }
        }

        if (paused && pauseStartedAt != null) {

            pausedSeconds += Duration.between(
                    pauseStartedAt,
                    endedAt)
                    .getSeconds();
        }

        return pausedSeconds;
    }

    /**
     * calculate effective waiting in seconds
     */
    private long calculateEffectiveWaitingSeconds(
            long totalWaitingSeconds,
            long pausedSeconds) {

        return Math.max(0, totalWaitingSeconds - pausedSeconds);
    }

    /**
     * calculate chargeable in minutes
     */
    private int calculateChargeableMinutes(
            long effectiveWaitingSeconds) {
        long freeSeconds = FeeConstants.FREE_WAITING_MINUTES * 60L;

        if (effectiveWaitingSeconds <= freeSeconds) {
            return 0;
        }

        long chargeableSeconds = effectiveWaitingSeconds - freeSeconds;

        return (int) Math.ceil(chargeableSeconds / 60.0);
    }

    /**
     * calculate waiting fee
     */
    private int calculateWaitingFee(
            int chargeableMinutes) {
        int fee = chargeableMinutes * FeeConstants.WAITING_FEE_PER_MINUTE;

        return Math.min(
                fee,
                FeeConstants.WAITING_FEE_CAP);
    }

    /**
     * calculate cancellation fee
     */
    private int calculateCancellationFee(
            EndReason endReason,
            long effectiveWaitingSeconds,
            int waitingFee) {
        if (endReason == EndReason.TRIP_STARTED) {
            return 0;
        }

        if (endReason == EndReason.CANCELLED_BY_DRIVER) {
            return 0;
        }

        long freeSeconds = FeeConstants.FREE_WAITING_MINUTES * 60L;

        if (effectiveWaitingSeconds <= freeSeconds) {
            return 0;
        }

        int fee = waitingFee + FeeConstants.CANCELLATION_SURCHARGE;

        return Math.min(
                fee,
                FeeConstants.CANCELLATION_FEE_CAP);
    }
}
