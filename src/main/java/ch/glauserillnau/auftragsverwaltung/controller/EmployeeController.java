package ch.glauserillnau.auftragsverwaltung.controller;

import ch.glauserillnau.auftragsverwaltung.entity.Employee;
import ch.glauserillnau.auftragsverwaltung.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        List<Map<String, Object>> employees = employeeRepository.findAll().stream()
                .map(e -> Map.<String, Object>of(
                        "id", e.getId(),
                        "fullName", e.getFullName(),
                        "email", e.getEmail(),
                        "role", e.getRole().name()
                ))
                .toList();
        return ResponseEntity.ok(employees);
    }
}
