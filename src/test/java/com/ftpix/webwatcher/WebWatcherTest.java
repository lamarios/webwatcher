package com.ftpix.webwatcher;

import com.ftpix.sparknnotation.Sparknotation;
import com.ftpix.webwatcher.implementations.CountListener;
import com.ftpix.webwatcher.model.DefaultWebSite;
import com.ftpix.webwatcher.server.TestWebController;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class WebWatcherTest {

    @BeforeAll
    public static void setUp() throws IOException {
        Setup.setUp();
    }

    @AfterAll
    public static void stop() {
        Setup.stop();
    }


    @Test
    public void test() throws IOException {

        TestWebController controller = Sparknotation.getController(TestWebController.class);
        CountListener countListener = new CountListener();

        controller.setSimpleContent("myContent");

        WebWatcher webWatcher = WebWatcher.watch("http://localhost:4567/simple")
                .onChange(countListener)
                .onError(countListener)
                .triggerEventOnFirstCheck(true);


        webWatcher.check();

        assertEquals(1, countListener.count);
        assertEquals(0, countListener.errors);

        //triggering one more time to check the hash consistency, numbers should not increase>
        webWatcher.check();

        assertEquals(1, countListener.count);
        assertEquals(0, countListener.errors);

        //now changing the content the controller sends  and number should increase
        controller.setSimpleContent("yo");
        webWatcher.check();

        assertEquals(2, countListener.count);
        assertEquals(0, countListener.errors);
    }


    @Test
    public void testWithCssSelector() {
        TestWebController controller = Sparknotation.getController(TestWebController.class);
        CountListener countListener = new CountListener();

        controller.setSimpleContent("myContent");

        DefaultWebSite site = new DefaultWebSite("http://localhost:4567/simple");
        site.setCssSelector(".content");

        WebWatcher webWatcher = WebWatcher.watch(site)
                .onChange(countListener)
                .onError(countListener)
                .triggerEventOnFirstCheck(true);

        webWatcher.check();


        assertNotNull(site.getLastCheck());
        assertNotNull(site.getLastContentHash());

        assertEquals(1, countListener.count);
        assertEquals(0, countListener.errors);

        String hash = site.getLastContentHash();
        LocalDateTime checkTime = site.getLastCheck();

        webWatcher.check();
        assertEquals(hash, site.getLastContentHash());
        int comparison = checkTime.compareTo(site.getLastCheck());
        assertTrue(comparison < 0);

        assertEquals(1, countListener.count);
        assertEquals(0, countListener.errors);


        // changing content
        controller.setSimpleContent("yooo");
        webWatcher.check();
        assertNotEquals(hash, site.getLastContentHash());
        comparison = checkTime.compareTo(site.getLastCheck());
        assertTrue(comparison < 0);

        assertEquals(2, countListener.count);
        assertEquals(0, countListener.errors);
    }

    @Test
    public void testTextOnly() {
        TestWebController controller = Sparknotation.getController(TestWebController.class);
        CountListener countListener = new CountListener();

        controller.setSimpleContent("myContent");

        DefaultWebSite site = new DefaultWebSite("http://localhost:4567/simple");
        site.setCssSelector(".content");

        WebWatcher webWatcher = WebWatcher.watch(site)
                .onChange(countListener)
                .onError(countListener)
                .textOnly(true)
                .triggerEventOnFirstCheck(true);

        webWatcher.check();


        assertNotNull(site.getLastCheck());
        assertNotNull(site.getLastContentHash());

        assertEquals(1, countListener.lastContent.size());
        assertEquals("myContent", countListener.lastContent.get(0));

        assertEquals(1, countListener.count);
        assertEquals(0, countListener.errors);

        String hash = site.getLastContentHash();
        LocalDateTime checkTime = site.getLastCheck();

        webWatcher.check();

        assertEquals(hash, site.getLastContentHash());
        int comparison = checkTime.compareTo(site.getLastCheck());
        assertTrue(comparison < 0);

        assertEquals(1, countListener.lastContent.size());
        assertEquals("myContent", countListener.lastContent.get(0));

        assertEquals(1, countListener.count);
        assertEquals(0, countListener.errors);


        // changing content
        controller.setSimpleContent("yooo");
        webWatcher.check();
        assertNotEquals(hash, site.getLastContentHash());
        comparison = checkTime.compareTo(site.getLastCheck());
        assertTrue(comparison < 0);

        assertEquals(2, countListener.count);
        assertEquals(0, countListener.errors);

        assertEquals(1, countListener.lastContent.size());
        assertEquals("yooo", countListener.lastContent.get(0));

        // now checking text only on the overall page

        site.setCssSelector(null);
        countListener.count = 0;
        countListener.lastContent = null;

        webWatcher = WebWatcher.watch(site)
                .onChange(countListener)
                .onError(countListener)
                .textOnly(true)
                .triggerEventOnFirstCheck(true);

        webWatcher.check();


        assertNotNull(site.getLastCheck());
        assertNotNull(site.getLastContentHash());

        assertEquals(1, countListener.lastContent.size());
        assertEquals("heading yooo", countListener.lastContent.get(0));

        assertEquals(1, countListener.count);
        assertEquals(0, countListener.errors);


        // changing content
        controller.setSimpleContent("sup");
        webWatcher.check();
        assertNotEquals(hash, site.getLastContentHash());
        comparison = checkTime.compareTo(site.getLastCheck());
        assertTrue(comparison < 0);

        assertEquals(2, countListener.count);
        assertEquals(0, countListener.errors);

        assertEquals(1, countListener.lastContent.size());
        assertEquals("heading sup", countListener.lastContent.get(0));
    }

    @Test
    public void testBodyOnly() {
        TestWebController controller = Sparknotation.getController(TestWebController.class);
        CountListener countListener = new CountListener();

        controller.setSimpleContent("myContent");

        DefaultWebSite site = new DefaultWebSite("http://localhost:4567/simple");


        WebWatcher webWatcher = WebWatcher.watch(site)
                .onChange(countListener)
                .onError(countListener)
                .bodyOnly(true)
                .triggerEventOnFirstCheck(true);

        webWatcher.check();


        assertNotNull(site.getLastCheck());
        assertNotNull(site.getLastContentHash());

        assertEquals(1, countListener.lastContent.size());
        assertTrue(countListener.lastContent.get(0).startsWith("<body>"));

        assertEquals(1, countListener.count);
        assertEquals(0, countListener.errors);
    }


    @Test
    public void boostCoverage() {
        DefaultWebSite site = new DefaultWebSite("http://google.com");
        site.setUrl("https://archlinux.org");

        assertEquals("https://archlinux.org", site.getUrl());
    }


    @Test
    public void testErrorListener() {
        TestWebController controller = Sparknotation.getController(TestWebController.class);
        CountListener countListener = new CountListener();

        controller.setSimpleContent("myContent");

        DefaultWebSite site = new DefaultWebSite("http://localhost:4567/iDontExist");


        WebWatcher webWatcher = WebWatcher.watch(site)
                .onChange(countListener)
                .onError(countListener)
                .bodyOnly(true)
                .triggerEventOnFirstCheck(true);

        webWatcher.check();

        assertEquals(0, countListener.count);
        assertEquals(1, countListener.errors);

    }

    @Test
    public void testLoop() throws InterruptedException {
        TestWebController controller = Sparknotation.getController(TestWebController.class);
        CountListener countListener = new CountListener();

        controller.setSimpleContent("myContent");

        DefaultWebSite site = new DefaultWebSite("http://localhost:4567/simple");
        site.setCssSelector(".content");

        WebWatcher webWatcher = WebWatcher.watch(site)
                .onChange(countListener)
                .onError(countListener)
                .textOnly(true)
                .triggerEventOnFirstCheck(false);


        AtomicBoolean changeLoop = new AtomicBoolean(true);

        try {
            // triggers the checks every second
            new Thread(() -> webWatcher.checkPeriodically(1)).start();


            // we check every seconds so we sleep every to to make sure it goes through one round of check
            int sleepInterval = 2000;
            Thread.sleep(sleepInterval);


            assertTrue(webWatcher.isCheckLoopRunning());
            assertEquals(0, countListener.count);

            controller.setSimpleContent("test1");

            Thread.sleep(sleepInterval);
            assertEquals(1, countListener.count);
            assertEquals(1, countListener.lastContent.size());
            assertEquals("test1", countListener.lastContent.get(0));

            controller.setSimpleContent("test2");
            Thread.sleep(sleepInterval);
            assertEquals(2, countListener.count);
            assertEquals(1, countListener.lastContent.size());
            assertEquals("test2", countListener.lastContent.get(0));

        } finally {
            webWatcher.setCheckLoopRunning(false);
        }
    }


    @Test
    public void testWithMultipleElementOnSelector() {
        TestWebController controller = Sparknotation.getController(TestWebController.class);
        CountListener countListener = new CountListener();

        controller.setMultipleContent("c1", "c2", "c3");


        DefaultWebSite site = new DefaultWebSite("http://localhost:4567/multiple");
        site.setCssSelector(".content");

        WebWatcher webWatcher = WebWatcher.watch(site)
                .onChange(countListener)
                .onError(countListener)
                .textOnly(true)
                .triggerEventOnFirstCheck(true);


        webWatcher.check();

        assertEquals(3, countListener.lastContent.size());
        assertEquals("c1", countListener.lastContent.get(0));
        assertEquals("c2", countListener.lastContent.get(1));
        assertEquals("c3", countListener.lastContent.get(2));
        assertEquals(1, countListener.count);

        controller.setMultipleContent("c4", "c5");

        webWatcher.check();
        assertEquals(2, countListener.lastContent.size());
        assertEquals("c4", countListener.lastContent.get(0));
        assertEquals("c5", countListener.lastContent.get(1));
        assertEquals(2, countListener.count);
    }


    @Test
    public void testMultipleWebsites() {
        TestWebController controller = Sparknotation.getController(TestWebController.class);
        CountListener countListener = new CountListener();

        controller.setMultipleContent("c1", "c2", "c3");
        controller.setSimpleContent("yo");


        DefaultWebSite site = new DefaultWebSite("http://localhost:4567/simple");
        site.setCssSelector(".content");

        DefaultWebSite site2 = new DefaultWebSite("http://localhost:4567/multiple");
        site2.setCssSelector(".content");

        WebWatcher webWatcher = WebWatcher.watch(site, site2)
                .onChange(countListener)
                .onError(countListener)
                .textOnly(true)
                .triggerEventOnFirstCheck(true);


        webWatcher.check();

        //first check is true, so one per website
        assertEquals(2, countListener.count);

        //we only change one website content so count shouldb e only one more
        controller.setSimpleContent("yooo");

        webWatcher.check();
        assertEquals(3, countListener.count);
        assertEquals(1, countListener.lastContent.size());
        // there should only be one last content
        assertEquals("yooo", countListener.lastContent.get(0));

        // now we change the multiple content

        controller.setMultipleContent("yo1", "yo2");
        webWatcher.check();
        assertEquals(4, countListener.count);
        assertEquals(2, countListener.lastContent.size());
        // there should only be one last content
        assertEquals("yo1", countListener.lastContent.get(0));
        assertEquals("yo2", countListener.lastContent.get(1));


    }


    @Disabled
    @Test
    public void readMeCases() {
        // simple watch
        WebWatcher.watch("https://www.example.org", "https://www.archlinux.org")
                .onChange((site, newContent, pageNewHtml) -> System.out.println(pageNewHtml))
                .triggerEventOnFirstCheck(true)
                .checkPeriodically(3600);


        DefaultWebSite site = new DefaultWebSite("https://www.google.com");
        site.setCssSelector(".custom-css p.selector");

        WebWatcher.watch(site)
                .onChange((updatedSite, newContent, pageNewHtml) -> System.out.println(pageNewHtml))
                .triggerEventOnFirstCheck(true)
                .onError((nonWorkingWebSite, error) -> error.printStackTrace())
                .bodyOnly(true)
                .textOnly(true)
                .checkPeriodically(3600);
    }


    @Test
    public void test2ElementSelector() {
        TestWebController controller = Sparknotation.getController(TestWebController.class);
        CountListener countListener = new CountListener();

        DefaultWebSite site = new DefaultWebSite("http://localhost:4567/simple");
        site.setCssSelector(".content, h1");

        WebWatcher webWatcher = WebWatcher.watch(site)
                .onChange(countListener)
                .onError(countListener)
                .textOnly(true)
                .triggerEventOnFirstCheck(true);

        webWatcher.check();


        System.out.println(countListener.lastContent);

    }

}