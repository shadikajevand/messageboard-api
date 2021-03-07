package org.kajevand.messageboard.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.kajevand.messageboard.entity.User;
import org.kajevand.messageboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LoginController {

    private UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/login")
    public String login(@RequestBody User user) throws AuthenticationException {

        User existingUser = userRepository.findByUsernameIgnoreCase(user.getUsername())
                .stream()
                .findFirst()
                .orElseThrow(AuthenticationException::new);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(!encoder.matches(user.getPassword(), existingUser.getPassword())) {
            throw new AuthenticationException();
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("permissions", List.of("USER"));

        return Jwts.builder()
                .setSubject(existingUser.getUsername())
                .setIssuer("org.kajevand.messageboard")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 7 * 24 * 60 * 60 * 1000))
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

}
