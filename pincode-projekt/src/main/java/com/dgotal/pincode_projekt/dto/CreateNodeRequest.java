package com.dgotal.pincode_projekt.dto;

import lombok.Data;

@Data
public class CreateNodeRequest {
    private String title;
    private Long parentNodeId;
}