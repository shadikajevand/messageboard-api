package org.kajevand.messageboard.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final String jwtSecret;

    public JWTAuthorizationFilter(AuthenticationManager authManager, String jwtSecret) {
        super(authManager);
        this.jwtSecret = jwtSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer")) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization").split(" ")[1];
        if (token != null) {
            Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
            Date expiry = claims.getExpiration();
            Date now = new Date();
            String user = claims.getSubject();
            if (user != null && expiry != null && now.before(expiry)) {
                List<String> permissions = (ArrayList<String>) claims.get("permissions");
                UsernamePasswordAuthenticationToken t = new UsernamePasswordAuthenticationToken(user, null, permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                t.setDetails(claims);
                return t;
            }
            return null;
        }
        return null;
    }
}