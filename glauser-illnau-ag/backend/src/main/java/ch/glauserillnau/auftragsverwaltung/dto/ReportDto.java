package ch.glauserillnau.auftragsverwaltung.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

public class ReportDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Arbeitsbeschreibung ist pflicht")
        private String workDescription;

        @NotNull(message = "Arbeitsstunden sind pflicht")
        private Double workingHours;

        private String usedMaterials;
    }

    @Data
    public static class Response {
        private Long id;
        private String workDescription;
        private Double workingHours;
        private String usedMaterials;
        private Boolean approved;
        private LocalDateTime createdAt;
        private Long workOrderId;
    }
}
