package com.dgotal.pincode_projekt.dto;

import lombok.Data;

@Data
public class ChildNodeResponse {
    private Long id;
    private String title;
    private Long parentNodeId;
    private Boolean hasChildren;
    private Integer sortOrder;
}