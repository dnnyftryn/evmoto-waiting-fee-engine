/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.example.demo.controller;

import com.example.demo.domain.FeeBreakdown;
import com.example.demo.dto.request.FeePreviewRequest;
import com.example.demo.dto.response.FeePreviewResponse;
import com.example.demo.service.WaitingFeeCalculator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author dolphin
 */

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class FeePreviewController {

    private final WaitingFeeCalculator waitingFeeCalculator;


    @PostMapping("/{orderId}/fee-preview")
    public FeePreviewResponse previewFee(
            @PathVariable String orderId,
            @Valid @RequestBody FeePreviewRequest request) {

        FeeBreakdown breakdown = waitingFeeCalculator.calculate(request);

        return FeePreviewResponse.builder()
                .feeBreakdown(breakdown)
                .build();
    }
}
