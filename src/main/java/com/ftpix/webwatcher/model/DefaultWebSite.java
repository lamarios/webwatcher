package com.ftpix.webwatcher.model;

import com.ftpix.webwatcher.interfaces.WebSite;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DefaultWebSite implements WebSite {

    private String url, lastContentHash, cssSelector;
    private LocalDateTime lastCheck;

    public DefaultWebSite(String url) {
        this.url = url;
    }

    @Override
    public String getCssSelector() {
        return cssSelector;
    }


    @Override
    public void setCssSelector(String cssSelector) {
        this.cssSelector = cssSelector;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public LocalDateTime getLastCheck() {
        return lastCheck;
    }

    @Override
    public String getLastContentHash() {
        return lastContentHash;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setLastContentHash(String lastContentHash) {
        this.lastContentHash = lastContentHash;
    }

    @Override
    public void setLastCheck(LocalDateTime lastCheck) {
        this.lastCheck = lastCheck;
    }
}
