package com.ftpix.webwatcher;

import com.ftpix.webwatcher.interfaces.WebSite;
import com.ftpix.webwatcher.interfaces.WebSiteErrorListener;
import com.ftpix.webwatcher.interfaces.WebSiteListener;
import com.ftpix.webwatcher.model.DefaultWebSite;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WebWatcher<T  extends WebSite> {
    private boolean checkLoopRunning = true;
    private List<T> sites;
    private WebSiteListener<T> listener;
    private WebSiteErrorListener<T> errorListener;
    private boolean triggerEventIfNoPreviousHash = false;
    private boolean textOnly = false;

    /**
     * To decide whether the content check should only be  for the body  HTML element
     * or also include the headers
     *
     * @return set to true to limit to the check to the body, false to check the whole page including headers
     */
    private boolean bodyOnly;

    /**
     * Watch a list of websites
     *
     * @param sites a list of sites to watch
     */
    private WebWatcher(T... sites) {
        this.sites = Stream.of(sites).collect(Collectors.toList());
    }

    /**
     * Watch a list of websites
     *
     * @param urls a list of URLs to watch
     * @return a new web watcher for the given websites
     */
    public static WebWatcher<DefaultWebSite> watch(String... urls) {
        DefaultWebSite[] sites = Stream.of(urls)
                .filter(Objects::nonNull)
                .filter(url -> url.trim().length() > 0)
                .map(DefaultWebSite::new)
                .toArray(DefaultWebSite[]::new);
        return new WebWatcher<>(sites);
    }


    /**
     * Watch a list of websites
     * @param sites a list of  sites to watch
     * @param <U> a class that implements a WebSote
     * @return a new web watcher for the given websites
     */
    public static <U extends WebSite> WebWatcher<U> watch(U... sites) {
        return new WebWatcher<>(sites);
    }


    /**
     * Whetehr or not to trigger event if the website has never been checked before
     *
     * @param triggerEvent the flag
     * @return itself
     */
    public WebWatcher<T> triggerEventOnFirstCheck(boolean triggerEvent) {
        triggerEventIfNoPreviousHash = triggerEvent;
        return this;
    }


    /**
     * A listener to do something when the website changes
     *
     * @param listener a listener to do something when a website changes
     * @return itself
     */
    public WebWatcher<T> onChange(WebSiteListener<T> listener) {
        this.listener = listener;
        return this;
    }


    /**
     * a listener to handle errors
     *
     * @param errorListener an error listener to add to the watcher
     * @return itself
     */
    public WebWatcher<T> onError(WebSiteErrorListener<T> errorListener) {
        this.errorListener = errorListener;
        return this;
    }

    /**
     * To set whether the content checking is only on the visible text (true) or the html structure (false)
     *
     * @param textOnly defaults to false
     * @return itself
     */
    public WebWatcher<T> textOnly(boolean textOnly) {
        this.textOnly = textOnly;
        return this;
    }

    /**
     * To set whether the content checking is only happening from the body tag and ignore changes in the header
     * note that this parameter is overriden is a {@link WebSite} has a css selector
     *
     * @param bodyOnly defaults to false
     * @return itself
     */
    public WebWatcher<T> bodyOnly(boolean bodyOnly) {
        this.bodyOnly = bodyOnly;
        return this;
    }


    /**
     * Trigger the website check
     */
    public void check() {
        sites.forEach(this::checkSingleSite);
    }


    /**
     * Checks a single website
     *
     * @param site the site to check
     */
    private void checkSingleSite(T site) {

        try {
            Document parse = Jsoup.parse(new URL(site.getUrl()), 10000);

            String hash = "";
            List<String> content = new ArrayList<>();

            if (site.getCssSelector() != null && site.getCssSelector().trim().length() > 0) {
                Elements select = parse.select(site.getCssSelector());

                hash = hashElements(select.stream());
                content = select.stream()
                        .map(e -> textOnly ? e.text() : e.toString())
                        .collect(Collectors.toList());
            } else {
                //we get the hash of the whole body
                if (bodyOnly) {
                    Element body = parse.body();

                    String bodyContent = textOnly ? body.text() : body.toString();
                    hash = hash(bodyContent);
                    content = Collections.singletonList(bodyContent);

                } else {
                    String bodyContent = textOnly ? parse.text() : parse.toString();
                    hash = hash(bodyContent);
                    content = Collections.singletonList(bodyContent);
                }
            }


            site.setLastCheck(LocalDateTime.now());
            // first time we check the site
            if ((site.getLastContentHash() == null || site.getLastContentHash().trim().length() == 0) && triggerEventIfNoPreviousHash) {
                site.setLastContentHash(hash);
                listener.onContentChange(site, content, parse.toString());
            } else if (site.getLastContentHash() != null && !site.getLastContentHash().equalsIgnoreCase(hash)) {
                site.setLastContentHash(hash);
                listener.onContentChange(site, content, parse.toString());
            }else{
                site.setLastContentHash(hash);
            }

        } catch (Exception e) {
            if (errorListener != null) {
                errorListener.onError(site, e);
            }
        }


    }


    /**
     * Creates a hash from a list of elements
     *
     * @param elements the list of elements
     * @return the hash of the combined elements
     */
    private String hashElements(Stream<Element> elements) {
        String content = elements
                .map(e -> textOnly ? e.text() : e.toString()
                ).collect(Collectors.joining(""));

        return hash(content);

    }

    /**
     * Creates a MD5 hash of  a string
     *
     * @param text
     * @return  the hash of a text
     */
    private String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());
            byte[] digest = md.digest();
            BigInteger no = new BigInteger(1, digest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isCheckLoopRunning() {
        return checkLoopRunning;
    }

    public void setCheckLoopRunning(boolean checkLoopRunning) {
        this.checkLoopRunning = checkLoopRunning;
    }

    /**
     * trigger the website watch periodically (blocking)
     *
     * @param intervalInSeconds the delay in seconds between each checks
     */
    public void checkPeriodically(int intervalInSeconds) {
        checkLoopRunning = true;
        while (checkLoopRunning) {
            check();
            try {
                Thread.sleep(intervalInSeconds * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
