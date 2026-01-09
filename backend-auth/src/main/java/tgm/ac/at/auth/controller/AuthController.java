package tgm.ac.at.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tgm.ac.at.auth.service.JwtService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // Simple in-memory users (for demo - in production use database!)
    private static final Map<String, UserInfo> USERS = new HashMap<>();

    static {
        // Default users - passwords should be hashed in production
        USERS.put("admin", new UserInfo("admin", 
            "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsAORp6xXsVz6TXhG2", // "admin123"
            "Admin"));
        USERS.put("user", new UserInfo("user",
            "$2a$10$EqKcp1WFKVQISheBxkQJoOqFbGjGRXMpMJrKQCNqqNLnnYrl0FCaS", // "user123" 
            "Reader"));
        USERS.put("researcher", new UserInfo("researcher",
            "$2a$10$pGHr7dNRkLHrPMHh0dXSxOy6TmP0xVQT4.pDlXeVkwRH4mVhZyVVq", // "research123"
            "Researcher"));
    }

    public AuthController(JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        UserInfo user = USERS.get(request.username());

        if (user == null) {
            return ResponseEntity.status(401)
                .body(Map.of("message", "Ungültiger Benutzername oder Passwort"));
        }

        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            return ResponseEntity.status(401)
                .body(Map.of("message", "Ungültiger Benutzername oder Passwort"));
        }

        String token = jwtService.generateToken(user.username(), user.role());

        return ResponseEntity.ok(Map.of(
            "token", token,
            "user", Map.of(
                "username", user.username(),
                "role", user.role()
            )
        ));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("valid", false));
        }

        String token = authHeader.substring(7);
        
        if (jwtService.isTokenValid(token)) {
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", jwtService.extractUsername(token),
                "role", jwtService.extractRole(token)
            ));
        }

        return ResponseEntity.status(401).body(Map.of("valid", false));
    }

    // DTOs
    public record LoginRequest(String username, String password) {}
    
    private record UserInfo(String username, String passwordHash, String role) {}
}
