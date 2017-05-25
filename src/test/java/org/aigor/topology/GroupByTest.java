/**
 * Copyright (C) Zoomdata, Inc. 2012-2017. All rights reserved.
 */
package org.aigor.topology;

import org.junit.Test;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class GroupByTest {

    @Test
    public void shouldCreateGroupBy() throws Exception {
        int factor = 17;
        int max = 10000;
        List<Integer> items = IntStream.range(1, max + 1).boxed().collect(toList());
        Observable.from(items)
                .groupBy(e -> key(e, factor))
                .flatMap(group -> group
                        .<List<Integer>>collect(ArrayList::new, List::add))
                .doOnNext(System.out::println)
                .scan(0, this::reduceStream)
                .last()
                .subscribe(e -> System.out.println("Actual: " + e));
        System.out.println("Expected: " + max * (max + 1) / 2);
    }

    private int reduceStream(Integer e, List<Integer> i) {
        return e + i.stream().mapToInt(Integer::intValue).sum();
    }

    private Integer key(Integer k, Integer factor) {
        return k % factor;
    }
}
