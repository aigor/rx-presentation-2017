/**
 * Copyright (C) Zoomdata, Inc. 2012-2017. All rights reserved.
 */
package org.aigor.web.sse;

import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@RestController
public class DataStreaming {


   public Flux<String> getData() {
      return Flux.<String>interval(Duration.ofSeconds(1), Schedulers.elastic())
         .map(Object::toString);
   }
}
