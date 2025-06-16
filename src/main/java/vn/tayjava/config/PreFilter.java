package vn.tayjava.config;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.tayjava.common.TokenType;
import vn.tayjava.service.JwtService;
import vn.tayjava.service.UserService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class PreFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      log.info("PreFilter");

        String path = request.getRequestURI();
        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

      final String authorization = request.getHeader("Authorization");
      if(StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
          filterChain.doFilter(request, response);
          return;
      }

      final String token = authorization.substring("Bearer ".length());
      final String username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
      if(StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
          if(jwtService.isValidToken(token, TokenType.ACCESS_TOKEN, userDetails)) {
              SecurityContext context = SecurityContextHolder.createEmptyContext();
              UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
              authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
              context.setAuthentication(authentication);
              SecurityContextHolder.setContext(context);
          }
      }
      filterChain.doFilter(request, response);
    }
}
