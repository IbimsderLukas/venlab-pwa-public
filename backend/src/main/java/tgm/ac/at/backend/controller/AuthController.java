package tgm.ac.at.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tgm.ac.at.backend.dto.LoginRequest;
import tgm.ac.at.backend.dto.LoginResponse;
import tgm.ac.at.backend.security.JwtUtils;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Einfache User-Datenbank (in Produktion: echte Datenbank verwenden)
    private static final Map<String, String> USERS = new HashMap<>();
    
    static {
        // Demo-User: admin / admin123
        // Passwort-Hash für "admin123"
        USERS.put("admin", "$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrI1t0lJhB5YqN5Zz5vqNJQj9qQ7zO");
    }

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        // Einfache Validierung für Demo
        if ("admin".equals(username) && "admin123".equals(password)) {
            String token = jwtUtils.generateToken(username);
            return ResponseEntity.ok(new LoginResponse(token, username));
        }

        // Prüfe gegen gespeicherte User
        String storedHash = USERS.get(username);
        if (storedHash != null && passwordEncoder.matches(password, storedHash)) {
            String token = jwtUtils.generateToken(username);
            return ResponseEntity.ok(new LoginResponse(token, username));
        }

        return ResponseEntity.status(401)
            .body(Map.of("message", "Ungültige Anmeldedaten"));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        // Wenn wir hier ankommen, ist der Token gültig (durch Filter geprüft)
        return ResponseEntity.ok(Map.of("valid", true));
    }
}
