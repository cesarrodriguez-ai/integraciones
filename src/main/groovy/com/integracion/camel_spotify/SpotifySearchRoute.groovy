package com.integracion.camel_spotify

import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SpotifySearchRoute extends RouteBuilder {

    @Autowired
    SpotifyTokenService tokenService

    @Value('${spotify.api-url}')
    String apiUrl

    @Override
    void configure() {
        from('direct:spotify-search')
            .routeId('spotify-search')
            .process { exchange ->
                def query = exchange.in.getHeader('q', String)
                def type  = exchange.in.getHeader('type', 'track', String)
                def token = tokenService.getAccessToken()

                exchange.in.setHeader('Authorization', "Bearer ${token}".toString())
                exchange.in.setHeader('CamelHttpMethod', 'GET')
                exchange.in.setHeader('CamelHttpQuery',
                    "q=${URLEncoder.encode(query, 'UTF-8')}&type=${type}&limit=10".toString())
                exchange.in.body = null
            }
            .toD((apiUrl + '/search?bridgeEndpoint=true').toString())
            .log('Spotify respondio: ${header.CamelHttpResponseCode}')
    }
}
