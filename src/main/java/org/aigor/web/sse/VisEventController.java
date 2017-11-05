/**
 * Copyright (C) Zoomdata, Inc. 2012-2017. All rights reserved.
 */
package org.aigor.web.sse;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static java.util.Arrays.asList;


@Slf4j
@RestController
public class VisEventController {

   @RequestMapping(value = "/sse")
   public SseEmitter visEvents(HttpServletRequest request) {

      String remoteUser = request.getRemoteUser();
      SseEmitter emitter = new SseEmitter(180_000L);
      generateStreamingEvents()
         .doOnSubscribe(s -> log.info("SSE started for client: {}", remoteUser))
         .doOnTerminate(() -> log.info("SSE stopped for client: {}", remoteUser))
         .subscribe(sseSend(emitter), error -> log.warn("Error on SSE", error), () ->
         {
            // emitter::complete
         });

      return emitter;
   }

   private Flux<Message> generateStreamingEvents() {
      return Flux.concat(
               Mono.just(new Progress("Preparing query for visualization...")),
               Mono.delay(Duration.ofSeconds(3))
                  .map(e -> new Progress("Executing query")),
               getSharpening(),
               Mono.just(new Progress("Visualization built"))
         )
         .cast(Message.class);
   }

   private Flux<Message> getSharpening() {
      Random rnd = new Random();
      DataStorage ds = new DataStorage();

      Map<Integer, QueryResultRow> runningResults = new ConcurrentHashMap<>();

      int startYear = 2000;
      int yearCount = 18;

      return Flux.range(startYear, yearCount)
         .flatMap(year ->
            Mono.delay(Duration.ofSeconds(rnd.nextInt(15)))
               .map(_i -> ds.getDataForYear(year)))
         .scan(runningResults, (acc, yearData) -> {
            acc.put((Integer) yearData.cells.get(0), yearData);
            return acc;
         })
         .flatMap(data -> Flux.just(
            calculateProgress(data, yearCount),
            toResult(data, startYear, yearCount))
         )
         .cast(Message.class);
   }

   private Progress calculateProgress(Map data, int yearCount) {
      int pg = (int)(data.keySet().size() * 100.0 / yearCount);
      return new Progress(String.valueOf(pg) + "% of data processed");
   }

   private QueryResult toResult(Map<Integer, QueryResultRow> data, int startYear, int yearCount){
      DataStorage ds = new DataStorage();
      List<QueryResultRow> rows = new ArrayList<>(yearCount);
      rows.add(ds.header());

      for (Integer i = startYear; i < startYear + yearCount; i++ ){
         rows.add(data.containsKey(i) ? data.get(i) : ds.getEmptyDataForYear(i));
      }

      return new QueryResult(rows);
   }

   private Consumer<Object> sseSend(SseEmitter emitter) {
      return elem -> {
         try {
            emitter.send(elem, MediaType.APPLICATION_JSON);
         } catch (IOException e) {
            throw new RuntimeException("Can not send data", e);
         }
      };
   }
}

class DataStorage {
   Random rnd = new Random();

   public QueryResultRow header() {
      return new QueryResultRow(asList("Year", "Sales", "Expenses", "Profit", "NRT"));
   }

   public QueryResultRow getDataForYear(int year) {
      return new QueryResultRow(asList(
         year,
         rnd.nextInt(2000),
         rnd.nextInt(1800),
         rnd.nextInt(1900),
         rnd.nextInt(1400)
      ));
   }

   public QueryResultRow getEmptyDataForYear(int year) {
      return new QueryResultRow(asList(
         year, 0, 0, 0, 0
      ));
   }
}

abstract class Message {
   public String getType(){
      return getClass().getSimpleName().toLowerCase();
   }
}

@Data
class Progress extends Message {
   final String message;
}

@Data
class QueryResult extends Message {
   public QueryResult(List<QueryResultRow> rows) {
      data = new ArrayList<>(rows.size());
      rows.forEach(e -> {
         List<Object> newRow = new ArrayList<>(e.getCells());
         Object remove = newRow.remove(0);
         newRow.add(0, remove.toString());
         data.add(newRow);
      });
   }

   final List<List<Object>> data;
}

@Data
class QueryResultRow extends Message {
   final List<Object> cells;
}