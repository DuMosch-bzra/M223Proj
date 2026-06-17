package ch.glauserillnau.auftragsverwaltung.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class CustomerDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Firmenname ist pflicht")
        private String companyName;

        private String contactPerson;
        private String phoneNumber;
        private String email;
        private String address;
    }

    @Data
    public static class Response {
        private Long id;
        private String companyName;
        private String contactPerson;
        private String phoneNumber;
        private String email;
        private String address;
    }
}
