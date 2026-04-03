package com.github.url_shortner.repository;

import com.github.url_shortner.entity.Click;
import com.github.url_shortner.entity.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ClickRepository extends JpaRepository<Click, Long> {

    long countByShortLink(ShortLink shortLink);

    long countByShortLinkAndTimestampAfter(ShortLink shortLink, LocalDateTime time);

    Optional<Click> findTopByShortLinkOrderByTimestampDesc(ShortLink shortLink);
}
