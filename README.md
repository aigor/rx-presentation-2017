# RxJava as a key component in mature Big Data product

[![Build Status](https://travis-ci.org/aigor/rx-presentation-2017.svg?branch=master)](https://travis-ci.org/aigor/rx-presentation-2017)

Small application that demonstrate Reactive approaches for Big Data visualization.

This is a demo application for presentation given on [JEEConf-2017](http://jeeconf.com/program/rxjava-as-key-component-in-a-mature-big-data-product/) and [JavaDay 2017](http://javaday.org.ua). Short description of the presentation is [here](http://javaday.org.ua/igor-lozynskyi-rxjava-as-a-key-component-in-a-mature-big-data-product/).


[Presentation lives here (on SlideShare)](https://www.slideshare.net/secret/4NFC0dun3dJ4Zn)

[![Slides](docs/images/presentation-title.png "Slides")](https://www.slideshare.net/secret/4NFC0dun3dJ4Zn)

---

#### Short description

This application simulates process of query execution in Big Data product.

![Application snapshot](docs/images/application-snapshot.png "Application snapshot")

It uses Server Sent Events for client-server communication and [RxJava](https://github.com/ReactiveX/RxJava/tree/1.x) library for asynchronous workflow.
It is based on [Spring Boot 2](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Release-Notes) & [SpringFramework 5](https://spring.io/blog/2016/09/22/new-in-spring-5-functional-web-framework). 
Application uses [WebFlux](https://spring.io/blog/2017/02/23/spring-framework-5-0-m5-update) framework for REST & Server Sent Events. UI components uses [Google Charts](https://developers.google.com/chart/). 
UI design is based on CSS framework [Skeleton](http://getskeleton.com/).

---

#### Build & run 

To build application you need ```JDK 1.8``` to be installed on your environment.

To run application you have to go into project root folder & run next command in terminal:
```mvn spring-boot:run```

After successful start application will be accessible on url: ```http://localhost:8080```.

---

#### Application Notices

- Spring Boot Actuator base url: ```/application```

---

#### Recommended articles

- [Reactive programming vs. Reactive systems (by Jonas Bonér & Viktor Klang)](https://www.oreilly.com/ideas/reactive-programming-vs-reactive-systems)
