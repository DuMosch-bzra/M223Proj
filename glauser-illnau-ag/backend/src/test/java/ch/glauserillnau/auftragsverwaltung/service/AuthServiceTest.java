package ch.glauserillnau.auftragsverwaltung.service;

import ch.glauserillnau.auftragsverwaltung.dto.AuthDto;
import ch.glauserillnau.auftragsverwaltung.entity.Employee;
import ch.glauserillnau.auftragsverwaltung.enums.Role;
import ch.glauserillnau.auftragsverwaltung.repository.EmployeeRepository;
import ch.glauserillnau.auftragsverwaltung.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnsTokenAndUserDetails() {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        Employee employee = Employee.builder()
                .id(1L)
                .firstName("Max")
                .lastName("Mustermann")
                .email("user@example.com")
                .password("encoded")
                .role(Role.MANAGER)
                .build();

        when(employeeRepository.findByEmail("user@example.com")).thenReturn(Optional.of(employee));
        when(jwtUtil.generateToken(employee)).thenReturn("token-abc");

        AuthDto.LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token-abc", response.getToken());
        assertEquals("user@example.com", response.getEmail());
        assertEquals("Max Mustermann", response.getFullName());
        assertEquals("MANAGER", response.getRole());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void registerCreatesEmployeeWhenEmailIsAvailable() {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest();
        request.setFirstName("Anna");
        request.setLastName("Muster");
        request.setEmail("anna@example.com");
        request.setPassword("secret");
        request.setRole("employee");

        when(employeeRepository.existsByEmail("anna@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");

        Employee savedEmployee = Employee.builder()
                .id(2L)
                .firstName("Anna")
                .lastName("Muster")
                .email("anna@example.com")
                .password("encoded-secret")
                .role(Role.EMPLOYEE)
                .build();

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        when(jwtUtil.generateToken(savedEmployee)).thenReturn("token-xyz");

        AuthDto.LoginResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("token-xyz", response.getToken());
        assertEquals("anna@example.com", response.getEmail());
        assertEquals("Anna Muster", response.getFullName());
        assertEquals("EMPLOYEE", response.getRole());

        verify(employeeRepository).existsByEmail("anna@example.com");
        verify(passwordEncoder).encode("secret");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void registerThrowsWhenEmailAlreadyExists() {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest();
        request.setFirstName("Anna");
        request.setLastName("Muster");
        request.setEmail("anna@example.com");
        request.setPassword("secret");
        request.setRole("employee");

        when(employeeRepository.existsByEmail("anna@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));

        assertTrue(exception.getMessage().contains("E-Mail bereits vergeben"));
        verify(employeeRepository).existsByEmail("anna@example.com");
        verify(employeeRepository, never()).save(any());
    }
}
