package com.dgotal.pincode_projekt.dto;

import lombok.Data;

@Data
public class UpdateNodeRequest {
    private String title;

    public UpdateNodeRequest() {
    }

    public UpdateNodeRequest(String title) {
        this.title = title;
    }
}
