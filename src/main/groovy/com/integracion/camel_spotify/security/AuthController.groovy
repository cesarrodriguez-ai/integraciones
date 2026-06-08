package com.integracion.camel_spotify.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping('/auth')
class AuthController {

    @Autowired
    JwtUtil jwtUtil

    // Credenciales de clientes B2B (configurables por env vars)
    @Value('${auth.client-id}')
    String validClientId

    @Value('${auth.client-secret}')
    String validClientSecret

    @PostMapping('/token')
    ResponseEntity<?> getToken(@RequestBody Map<String, String> body) {
        String clientId     = body.get('clientId')
        String clientSecret = body.get('clientSecret')

        if (clientId == validClientId && clientSecret == validClientSecret) {
            String token = jwtUtil.generateToken(clientId)
            return ResponseEntity.ok([
                token    : token,
                expiresIn: 3600,
                tokenType: 'Bearer'
            ])
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body([error: 'Credenciales invalidas'])
    }
}
