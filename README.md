#Web Watcher
![build](https://ci.ftpix.com/app/rest/builds/buildType:Webwatcher_Build/statusIcon)
![maven](https://maven-badges.herokuapp.com/maven-central/com.ftpix/webwatcher/badge.svg)


Web watcher is a simple library to detect changes in a web page

## Requirements
- java 8

## Download
```xml
<dependency>
  <groupId>com.ftpix</groupId>
  <artifactId>webwatcher</artifactId>
  <version>1.1</version>
</dependency>
```


## Usage

[Java doc](https://lamarios.github.io/webwatcher/)

### Basic usage
To monitor a bunch of websites, do the following:
```java
WebWatcher.watch("https://www.example.org", "https://www.archlinux.org")
        .onChange((site, newContent, pageNewHtml) -> System.out.println(pageNewHtml))
        .triggerEventOnFirstCheck(true)
        .checkPeriodically(3600);
```

this will monitor the HTML code of the two webistes and print the content the first time its checked and the subsequent if changes are detected

To monitor parts of a website, you need to create a DefaultWebsite object


```java
DefaultWebSite site = new DefaultWebSite("https://www.google.com");
site.setCssSelector(".custom-css p.selector");

DefaultWebSite archWebSite = new DefaultWebSite("https://www.archlinux.org");
archWebSite.setCssSelector(".custom.class");

WebWatcher.watch(site, archWebSite)
        .onChange((updatedSite, newContent, pageNewHtml) -> System.out.println(pageNewHtml))
        .triggerEventOnFirstCheck(true)
        .checkPeriodically(3600);
```

There are a bunch of options available on for the WebWatcher object
```java
DefaultWebSite site = new DefaultWebSite("https://www.google.com");
site.setCssSelector(".custom-css p.selector");

WebWatcher.watch(site)
        .onChange((updatedSite, newContent, pageNewHtml) -> System.out.println(pageNewHtml))
        .triggerEventOnFirstCheck(true) 
        .onError((nonWorkingWebSite, error) -> error.printStackTrace()) // event listener to handle errors
        .bodyOnly(true) // will only check the body of the HTML content, meaning that any change in the headers will not trigger the event. If yo uuse a css selector for your site this is ignore
        .textOnly(true) // when doing the content comparison, compare only the visible text and not the HTML
        .checkPeriodically(3600);
```


### Use your own POJO

If you want to use your own POJO instead of DefaultWebSite (if you want t osafe into a DB or something) you can just implement the WebSite interface

```java

public class MyCustomClass implements WebSite{
    //implements the required methods
}
```

```java
MyCustomClass site = new MyCustomClass();
//set up your pojo


WebWatcher.watch(site)
        .onChange((updatedSite, newContent, pageNewHtml) -> System.out.println(pageNewHtml)) // updatedSite will be a MyCustomClass instance
        .triggerEventOnFirstCheck(true)
        .checkPeriodically(3600);
```

### Check websites once only

If you want to control when to check for the websites you can simply use the *.check()* method instead of *.checkPeriodically()*

```java
WebWatcher.watch("https://www.example.org", "https://www.archlinux.org")
        .onChange((site, newContent, pageNewHtml) -> System.out.println(pageNewHtml))
        .triggerEventOnFirstCheck(true)
        .check();
```

This allows you to have more control on when to check. It can be useful if you want to load a bunch of sites from a DB,
check it once save the result to the DB and be done.

