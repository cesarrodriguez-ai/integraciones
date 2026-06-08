package com.integracion.camel_spotify.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        String authHeader = request.getHeader('Authorization')

        if (authHeader?.startsWith('Bearer ')) {
            String token = authHeader.substring(7)
            if (jwtUtil.isTokenValid(token)) {
                String clientId = jwtUtil.extractClientId(token)
                def auth = new UsernamePasswordAuthenticationToken(clientId, null, [])
                SecurityContextHolder.context.authentication = auth
            }
        }

        filterChain.doFilter(request, response)
    }
}
