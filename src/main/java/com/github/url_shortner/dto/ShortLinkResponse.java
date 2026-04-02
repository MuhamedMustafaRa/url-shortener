package com.github.url_shortner.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortLinkResponse {
    private String shortUrl;
    private String originalUrl;
    private long clickCount;
}
