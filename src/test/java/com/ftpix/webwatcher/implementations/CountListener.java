package com.ftpix.webwatcher.implementations;

import com.ftpix.webwatcher.interfaces.WebSite;
import com.ftpix.webwatcher.interfaces.WebSiteErrorListener;
import com.ftpix.webwatcher.interfaces.WebSiteListener;
import com.ftpix.webwatcher.model.DefaultWebSite;

import java.util.List;

public class CountListener implements WebSiteListener<DefaultWebSite>, WebSiteErrorListener<DefaultWebSite> {
    public int count = 0, errors = 0;
    public List<String> lastContent;


    @Override
    public void onError(DefaultWebSite site, Exception e) {
        errors++;
        e.printStackTrace();
    }

    @Override
    public void onContentChange(DefaultWebSite site, List<String> newContent, String pageNewHtml) {
        count++;
//        System.out.println(pageNewHtml);
        lastContent = newContent;
    }
}
