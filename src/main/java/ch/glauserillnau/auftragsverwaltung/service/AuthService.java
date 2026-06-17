package ch.glauserillnau.auftragsverwaltung.service;

import ch.glauserillnau.auftragsverwaltung.dto.AuthDto;
import ch.glauserillnau.auftragsverwaltung.entity.Employee;
import ch.glauserillnau.auftragsverwaltung.enums.Role;
import ch.glauserillnau.auftragsverwaltung.repository.EmployeeRepository;
import ch.glauserillnau.auftragsverwaltung.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        String token = jwtUtil.generateToken(employee);
        return new AuthDto.LoginResponse(token, employee.getEmail(), employee.getFullName(), employee.getRole().name());
    }

    public AuthDto.LoginResponse register(AuthDto.RegisterRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("E-Mail bereits vergeben: " + request.getEmail());
        }

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .build();

        employeeRepository.save(employee);

        String token = jwtUtil.generateToken(employee);
        return new AuthDto.LoginResponse(token, employee.getEmail(), employee.getFullName(), employee.getRole().name());
    }
}
