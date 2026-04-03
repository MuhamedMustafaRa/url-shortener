package com.github.url_shortner.controller;

import com.github.url_shortner.dto.LinkStateResponse;
import com.github.url_shortner.dto.ShortLinkResponse;
import com.github.url_shortner.entity.ShortLink;
import com.github.url_shortner.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    @PostMapping("/new")
    public ResponseEntity<ShortLinkResponse> create(@RequestBody String request, Authentication authentication) {

        String username = authentication.getName();

        ShortLinkResponse response = shortLinkService.createShortLink(request, username);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public String deleteLink(@RequestBody String link, Authentication authentication){
        String username = authentication.getName();
        shortLinkService.deleteLink(link , username);
        return "Link deleted successfully.";
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<LinkStateResponse> getStats(
            @PathVariable int id,
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(shortLinkService.getStats(id, username));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ShortLinkResponse>> getLinks(
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                shortLinkService.getUserLinks(username)
        );
    }

}
