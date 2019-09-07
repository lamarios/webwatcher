package com.ftpix.webwatcher.interfaces;

import java.time.LocalDateTime;

public interface WebSite {

    /**
     * THe URL of the website to check
     *
     * @return the url of the website
     */
    String getUrl();

    /**
     * When the content was checked last
     *
     * @return the date
     */
    LocalDateTime getLastCheck();


    /**
     * Gets the hash of the content the last time it was checked
     *
     * @return the last hash
     */
    String getLastContentHash();

    /**
     * Specifies a css selector to check against.
     * If this is set it will ignore {@link com.ftpix.webwatcher.WebWatcher#bodyOnly} parameter
     *
     * @return the css selector
     */
    String getCssSelector();


    void setUrl(String url);

    void setLastCheck(LocalDateTime lastCheck);

    void setLastContentHash(String hash);

    void setCssSelector(String cssSelector);
}
