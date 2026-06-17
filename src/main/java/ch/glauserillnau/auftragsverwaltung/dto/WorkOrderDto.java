package ch.glauserillnau.auftragsverwaltung.dto;

import ch.glauserillnau.auftragsverwaltung.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

public class WorkOrderDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Titel ist pflicht")
        private String title;

        private String description;

        private String address;

        @NotNull(message = "Kunden-ID ist pflicht")
        private Long customerId;
    }

    @Data
    public static class UpdateRequest {
        private String title;
        private String description;
        private String address;
    }

    @Data
    public static class DispatchRequest {
        @NotNull(message = "Mitarbeiter-ID ist pflicht")
        private Long employeeId;

        private LocalDateTime scheduledAt;
    }

    @Data
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String address;
        private OrderStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime scheduledAt;
        private LocalDateTime completedAt;
        private LocalDateTime invoicedAt;

        // Embedded customer info
        private Long customerId;
        private String customerName;

        // Embedded employee info
        private Long employeeId;
        private String employeeName;
    }
}
