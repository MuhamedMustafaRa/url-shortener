package com.github.url_shortner.service;


import com.github.url_shortner.dto.ShortLinkResponse;
import com.github.url_shortner.entity.ShortLink;
import com.github.url_shortner.entity.User;
import com.github.url_shortner.repository.ShortLinkRepository;
import com.github.url_shortner.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Random;

@AllArgsConstructor
@Service
public class ShortLinkService {

    private final ShortLinkRepository shortLinkRepository;
    private final UserRepository userRepository;

    private final String BASE_URL = "http://localhost:8080/";

    public ShortLinkResponse createShortLink(String url , String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newUrl = generateUniqueCode();
        ShortLink link = new ShortLink();
        link.setShortenLink(newUrl);
        link.setUser(user);
        link.setOriginalUrl(url);
        link.setClickCount((long)0);
        link.setExpirationDate(LocalDateTime.now().plusDays(365));

        shortLinkRepository.save(link);

        return mapToResponse(link);
    }

    public void deleteLink(String link , String username){
        ShortLink shortLink = shortLinkRepository.findByShortenLink(link)
                .orElseThrow(() -> new RuntimeException("Link doesn't exists"));

        if (!shortLink.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("User can only delete his links");
        }

        shortLinkRepository.deleteById(shortLink.getId());

    }

    public ShortLinkResponse getOriginalUrl(String shortUrl){
        ShortLink shortLink = shortLinkRepository.findByShortenLink(shortUrl)
                .orElseThrow(() -> new RuntimeException("short link isn't exist"));

        if (shortLink.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Link expired");
        }

        shortLink.setClickCount(shortLink.getClickCount() + 1);
        shortLinkRepository.save(shortLink);

        return mapToResponse(shortLink);
    }

    public List<ShortLinkResponse> getUserLinks(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return shortLinkRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    private ShortLinkResponse mapToResponse(ShortLink link) {
        return ShortLinkResponse.builder()
                .originalUrl(link.getOriginalUrl())
                .clickCount(link.getClickCount())
                .shortUrl(BASE_URL + link.getShortenLink())
                .build();
    }

    private String generateUniqueCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();

        String code = "";
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            code = sb.toString();
        } while (shortLinkRepository.existsByShortenLink(code));

        return code;
    }
}
