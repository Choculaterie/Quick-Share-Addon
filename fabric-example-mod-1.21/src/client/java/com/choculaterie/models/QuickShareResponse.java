package com.choculaterie.models;

public class QuickShareResponse {
    private final String shortUrl;
    public QuickShareResponse(String shortUrl) { this.shortUrl = shortUrl; }
    public String getShortUrl() { return shortUrl; }
}
