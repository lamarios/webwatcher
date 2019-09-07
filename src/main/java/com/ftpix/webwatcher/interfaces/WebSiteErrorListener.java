package com.ftpix.webwatcher.interfaces;

public interface WebSiteErrorListener {

    /**
     * Catch the errors throw by the check
      * @param site the site that was checked
     * @param e the exception that was thrown
     */
    void onError(WebSite site, Exception e);
}
