package com.github.url_shortner.service;


import com.github.url_shortner.dto.LinkStateResponse;
import com.github.url_shortner.dto.ShortLinkResponse;
import com.github.url_shortner.entity.Click;
import com.github.url_shortner.entity.ShortLink;
import com.github.url_shortner.entity.User;
import com.github.url_shortner.exception.BadRequestException;
import com.github.url_shortner.exception.NotFoundException;
import com.github.url_shortner.exception.UnauthorizedException;
import com.github.url_shortner.repository.ClickRepository;
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
    private final ClickRepository clickRepository;

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
                .orElseThrow(() -> new NotFoundException("Link doesn't exists"));

        if (!shortLink.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("User can only delete his links");
        }

        shortLinkRepository.deleteById(shortLink.getId());

    }

    public ShortLinkResponse getOriginalUrl(String shortUrl){
        ShortLink shortLink = shortLinkRepository.findByShortenLink(shortUrl)
                .orElseThrow(() -> new NotFoundException("short link isn't exist"));

        if (shortLink.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Link expired");
        }

        Click click = new Click();
        click.setShortLink(shortLink);
        click.setTimestamp(LocalDateTime.now());
        clickRepository.save(click);


        shortLink.setClickCount(shortLink.getClickCount() + 1);
        shortLinkRepository.save(shortLink);

        return mapToResponse(shortLink);
    }


    public LinkStateResponse getStats(int linkId, String username) {

        ShortLink shortLink = shortLinkRepository.findById(linkId)
                .orElseThrow(() -> new NotFoundException("Link not found"));

        if (!shortLink.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("Not authorized");
        }

        long totalClicks = clickRepository.countByShortLink(shortLink);

        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();

        long clicksToday = clickRepository.countByShortLinkAndTimestampAfter(shortLink, startOfDay);

        LocalDateTime lastClickAt = clickRepository
                .findTopByShortLinkOrderByTimestampDesc(shortLink)
                .map(Click::getTimestamp)
                .orElse(null);

        return new LinkStateResponse(totalClicks, clicksToday, lastClickAt);
    }


    public List<ShortLinkResponse> getUserLinks(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

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
