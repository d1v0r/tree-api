package com.dgotal.pincode_projekt.dto;

import lombok.Data;

@Data
public class ReorderNodeRequest {
    private Long parentNodeId;
    private Integer newIndex;
}
