package com.springsecurity.securitydemo.jwtutil;

import java.util.ArrayList; 
import org.springframework.security.core.userdetails.User; 
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.security.core.userdetails.UserDetailsService; 
import org.springframework.security.core.userdetails.UsernameNotFoundException; 
import org.springframework.stereotype.Service; 
@Service
public class JwtUserDetailsService implements UserDetailsService { 
   @Override 
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      if ("user".equals(username)) { 
         System.out.println("User found");
         return new User("user", 
            "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6", 
            new ArrayList<>()); 
      } else { 
         System.out.println("User NOT found");
         throw new UsernameNotFoundException("User not found with username: " + username); 
      } 
   } 
}
