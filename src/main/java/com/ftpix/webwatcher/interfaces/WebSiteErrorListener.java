package com.ftpix.webwatcher.interfaces;

public interface WebSiteErrorListener<T extends WebSite> {

    /**
     * Catch the errors throw by the check
      * @param site the site that was checked
     * @param e the exception that was thrown
     */
    void onError(T site, Exception e);
}
