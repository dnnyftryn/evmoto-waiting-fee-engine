/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.example.demo.dto.response;

import com.example.demo.domain.FeeBreakdown;

import lombok.Data;

/**
 *
 * @author dolphin
 */

@Data
public class FeePreviewResponse {
    
    private FeeBreakdown feeBreakdown;
}
