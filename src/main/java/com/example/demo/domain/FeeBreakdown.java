/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author dolphin
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeBreakdown {
    
    private long effectiveWaitingSeconds;

    private long pausedSeconds;

    private int chargeableMinutes;

    private int waitingFee;

    private int cancellationFee;

    private int totalFee;

    private boolean waitingFeeCapped;

    private boolean cancellationFeeCapped;
    
}
