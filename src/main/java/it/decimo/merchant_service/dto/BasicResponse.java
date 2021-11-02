package it.decimo.merchant_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BasicResponse {
    private String message;
    private String code;
}
