package com.ftpix.webwatcher.implementations;

import com.ftpix.webwatcher.interfaces.WebSite;
import com.ftpix.webwatcher.interfaces.WebSiteErrorListener;
import com.ftpix.webwatcher.interfaces.WebSiteListener;

import java.util.List;

public class CountListener implements WebSiteListener, WebSiteErrorListener {
    public int count = 0, errors = 0;
    public List<String> lastContent;


    @Override
    public void onError(WebSite site, Exception e) {
        errors++;
        e.printStackTrace();
    }

    @Override
    public void onContentChange(WebSite site, List<String> newContent, String pageNewHtml) {
        count++;
        System.out.println(pageNewHtml);
        lastContent = newContent;
    }
}
