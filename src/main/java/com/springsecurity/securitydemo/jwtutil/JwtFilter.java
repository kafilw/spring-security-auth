package com.springsecurity.securitydemo.jwtutil;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;


@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String tokenHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if(tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            token  = tokenHeader.substring(7);
            try {
                username = tokenManager.getUsernameFromToken(token);
            }
            catch(IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            }
            catch(ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        }
        
        else if (request.getRequestURI().equals("/login")) {
                //filterChain.doFilter(request, response);
                return;
            }
         
        else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        

        if(null != username && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (tokenBlacklist.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
            if(tokenManager.validateJwtToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken
                authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                    );
                authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
    
}
