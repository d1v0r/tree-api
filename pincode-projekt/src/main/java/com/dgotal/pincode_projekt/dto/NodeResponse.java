package com.dgotal.pincode_projekt.dto;

import java.util.List;
import lombok.Data;

@Data
public class NodeResponse {
    private Long id;
    private String title;
    private Long parentNodeId;
    private List<ChildNodeResponse> children;
}
