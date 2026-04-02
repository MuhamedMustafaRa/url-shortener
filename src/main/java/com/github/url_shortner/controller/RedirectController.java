package com.github.url_shortner.controller;


import com.github.url_shortner.dto.ShortLinkResponse;
import com.github.url_shortner.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final ShortLinkService shortLinkService;

    @GetMapping("/{shortLink}")
    public ResponseEntity<?> redirect(@PathVariable String shortLink) {

        ShortLinkResponse shortLinkResponse = shortLinkService.getOriginalUrl(shortLink);

        System.out.println(shortLinkResponse.getOriginalUrl());

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(shortLinkResponse.getOriginalUrl()))
                .build();
    }

}
