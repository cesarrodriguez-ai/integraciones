package com.integracion.camel_spotify

import org.apache.camel.ProducerTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping('/api/spotify')
class SpotifyController {

    @Autowired
    ProducerTemplate producerTemplate

    @GetMapping('/search')
    ResponseEntity<String> search(
            @RequestParam String q,
            @RequestParam(defaultValue = 'track') String type) {

        def result = producerTemplate.requestBodyAndHeaders(
            'direct:spotify-search',
            null,
            [q: q, type: type],
            String
        )
        return ResponseEntity.ok(result)
    }
}
