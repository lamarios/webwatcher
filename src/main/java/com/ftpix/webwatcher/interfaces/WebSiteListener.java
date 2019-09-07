package com.ftpix.webwatcher.interfaces;

import java.util.List;

public interface WebSiteListener {

    /**
     * @param site        the website being checked, the with its last content hash
     * @param newContent  the list new plain html content, this is a list because a css selector can match to more than one element
     *                    if the site has no css selector, the first item will be the same as pageNewHtml
     * @param pageNewHtml the HTML of the content of the page
     */
    void onContentChange(WebSite site, List<String> newContent, String pageNewHtml);
}
