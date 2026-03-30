package com.github.url_shortner.repository;

import com.github.url_shortner.entity.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortLinkRepository extends JpaRepository<ShortLink , Integer> {
    Optional<ShortLink> shortenLink(String shortenLink);
}
