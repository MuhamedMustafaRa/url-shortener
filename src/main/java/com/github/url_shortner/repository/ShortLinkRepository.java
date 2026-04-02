package com.github.url_shortner.repository;

import com.github.url_shortner.entity.ShortLink;
import com.github.url_shortner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ShortLinkRepository extends JpaRepository<ShortLink , Integer> {
    Optional<ShortLink> findByShortenLink(String shortCode);

    List<ShortLink> findByUser(User user);

    boolean existsByShortenLink(String shortCode);

}
