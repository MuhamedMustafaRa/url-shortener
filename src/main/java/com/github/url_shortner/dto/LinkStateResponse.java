package com.github.url_shortner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
public class LinkStateResponse {
    private long totalClicks;
    private long clicksToday;
    private LocalDateTime lastClickAt;
}
