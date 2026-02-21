//package com.ddm.server.config.auth;
//import com.udd.SecurityIncidentSearch.service.auth.AuthorizationService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.lang.NonNull;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//
//import java.io.IOException;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final HandlerExceptionResolver handlerExceptionResolver;
//
//    private final AuthorizationService jwtService;
//    private final UserDetailsService userDetailsService;
//    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
//
//    public JwtAuthenticationFilter(
//            AuthorizationService jwtService,
//            UserDetailsService userDetailsService,
//            HandlerExceptionResolver handlerExceptionResolver
//    ) {
//        this.jwtService = jwtService;
//        this.userDetailsService = userDetailsService;
//        this.handlerExceptionResolver = handlerExceptionResolver;
//    }
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain
//    ) throws ServletException, IOException {
//        final String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            final String jwt = authHeader.substring(7);
//            final String userUsername = jwtService.extractUsername(jwt);
//
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            if (userUsername != null && authentication == null) {
//                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userUsername);
//
//                if (jwtService.isTokenValid(jwt, userDetails)) {
//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.getAuthorities()
//                    );
//
//                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            }
//
//            filterChain.doFilter(request, response);
//        } catch (Exception exception) {
//            log.error(exception.getMessage());
//            handlerExceptionResolver.resolveException(request, response, null, exception);
//        }
//    }
//}
