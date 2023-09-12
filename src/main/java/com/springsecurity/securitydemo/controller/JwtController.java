package com.springsecurity.securitydemo.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;

import com.springsecurity.securitydemo.jwtutil.JwtUserDetailsService;
import com.springsecurity.securitydemo.jwtutil.TokenManager;
import com.springsecurity.securitydemo.model.JwtRequestModel;
import com.springsecurity.securitydemo.model.JwtResponseModel;

@RestController
@CrossOrigin
public class JwtController {
    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenManager tokenManager;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseModel> createToken(@RequestBody JwtRequestModel request) throws Exception {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        }
        catch (DisabledException e){
            throw new Exception("USER_DISABLED", e);
        }
        catch (BadCredentialsException e){
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwtToken = tokenManager.generateJwtToken(userDetails);
        return ResponseEntity.ok(new JwtResponseModel(jwtToken));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> requestBody) {
        String tokenToInvalidate = requestBody.get("token");

        if (tokenToInvalidate != null) {
            tokenManager.invalidateToken(tokenToInvalidate);
            return ResponseEntity.ok("Logged out successfully");
        } else {
            return ResponseEntity.badRequest().body("Token not provided");
        }
    }


    // @GetMapping("/login")
    // public String login() {
    //     return ("<h1>Login</h1>");
    // }


}
