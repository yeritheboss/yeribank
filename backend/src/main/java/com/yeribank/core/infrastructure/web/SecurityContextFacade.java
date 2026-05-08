package com.yeribank.core.infrastructure.web;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextFacade {

  public UUID currentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return UUID.fromString(authentication.getName());
  }

  public boolean isAdmin() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getAuthorities().stream()
        .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
  }
}
