package com.integracion.camel_spotify

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap

@Service
class SpotifyTokenService {

    @Value('${spotify.client-id}')
    String clientId

    @Value('${spotify.client-secret}')
    String clientSecret

    @Value('${spotify.token-url}')
    String tokenUrl

    private String cachedToken
    private long tokenExpiresAt = 0

    String getAccessToken() {
        if (cachedToken && System.currentTimeMillis() < tokenExpiresAt) {
            return cachedToken
        }

        def credentials = Base64.encoder.encodeToString("${clientId}:${clientSecret}".bytes)
        def body = new LinkedMultiValueMap<String, String>()
        body.add('grant_type', 'client_credentials')

        def client = RestClient.create()
        def response = client.post()
                .uri(tokenUrl)
                .header('Authorization', "Basic ${credentials}")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(Map)

        cachedToken = response.access_token as String
        // Spotify tokens duran 3600s, renovamos 60s antes
        tokenExpiresAt = System.currentTimeMillis() + ((response.expires_in as long) - 60) * 1000

        return cachedToken
    }
}
