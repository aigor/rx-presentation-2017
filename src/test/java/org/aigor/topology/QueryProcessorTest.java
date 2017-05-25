package org.aigor.topology;

import lombok.Data;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.aigor.topology.QueryProcessorTest.QueryProcessor.CANCELLED_MESSAGE;
import static org.aigor.topology.QueryProcessorTest.QueryProcessor.RAW_FINAL_RESPONSE;
import static org.aigor.topology.QueryProcessorTest.RequestType.CANCELLATION;
import static org.aigor.topology.QueryProcessorTest.RequestType.REQUEST;
import static org.aigor.topology.QueryProcessorTest.RequestValidationException.EMPTY_QUERY_MESSAGE;
import static org.aigor.topology.QueryProcessorTest.ResultType.CANCELLATION_DONE;
import static org.aigor.topology.QueryProcessorTest.ResultType.FINAL_RESULT;
import static org.aigor.topology.QueryProcessorTest.ResultType.INTERMEDIATE_RESULT;
import static org.aigor.topology.QueryProcessorTest.ResultType.PROCESSING_ERROR;
import static org.aigor.topology.QueryProcessorTest.ResultType.PROGRESS;
import static org.aigor.topology.QueryProcessorTest.ResultType.REQUEST_ERROR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static rx.Observable.concat;
import static rx.Observable.interval;
import static rx.Observable.just;
import static rx.Observable.merge;
import static rx.Observable.never;

/**
 * Query processor test.
 * Test infrastructure for Query Processing in the way similar to ZD.
 */
public class QueryProcessorTest {
    private TestSubscriber<QueryResult> testSubscriber;

    private QueryProcessor queryProcessor;
    private QueryRequest qRequest = new QueryRequest(REQUEST, "1", "SELECT COUNT(*) FROM events");
    private QueryRequest eRequest = new QueryRequest(REQUEST, "1", " ");
    private QueryRequest qCancellation = new QueryRequest(CANCELLATION, "1", "");

    @Before
    public void setUp() throws Exception {
        queryProcessor = new QueryProcessor();
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void simple() throws Exception {
        merge(just("1"), never().first())
                .takeUntil(never())
                .doOnTerminate(() -> System.out.println("Terminated"))
                .subscribe(System.out::println);
    }

    @Test
    public void shouldProcessEmptyStream() throws Exception {
        Observable.<QueryRequest>empty()
                .compose(queryProcessor)
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent(10, SECONDS);
        testSubscriber.assertNoValues();
    }

    @Test
    public void shouldProcessSimpleRequest() throws Exception {
        just(qRequest)
                .compose(queryProcessor)
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        assertTrue(testSubscriber.getOnNextEvents().contains(
                new QueryResult(FINAL_RESULT, "1", RAW_FINAL_RESPONSE)
        ));
    }

    @Test
    public void shouldProcessRequest() throws Exception {
        PublishSubject<QueryRequest> queryEngineClient = PublishSubject.create();

        queryEngineClient
                .compose(queryProcessor)
                .subscribe(testSubscriber);

        new Thread(() -> {
            queryEngineClient.onNext(qRequest);
            queryEngineClient.onCompleted();
        }).start();

        testSubscriber.awaitTerminalEvent(10, SECONDS);
        assertTrue(testSubscriber.getOnNextEvents().contains(
                new QueryResult(FINAL_RESULT, "1", RAW_FINAL_RESPONSE)
        ));
    }

    @Test
    public void shouldProcessCancellation() throws Exception {
        PublishSubject<QueryRequest> queryEngineClient = PublishSubject.create();

        queryEngineClient
                .compose(queryProcessor)
                .subscribe(testSubscriber);

        new Thread(() -> {
            try {
                queryEngineClient.onNext(qRequest);
                Thread.sleep(150);
                queryEngineClient.onNext(qCancellation);
                Thread.sleep(150);
                queryEngineClient.onCompleted();
            } catch (InterruptedException ignore) {}
        }).start();

        testSubscriber.awaitTerminalEvent(10, SECONDS);
        assertTrue(testSubscriber.getOnNextEvents().contains(
                new QueryResult(CANCELLATION_DONE, "1", CANCELLED_MESSAGE)
        ));
    }

    @Test
    public void shouldProcessRequestLater() throws Exception {
        PublishSubject<QueryRequest> queryEngineClient = PublishSubject.create();

        queryEngineClient
                .compose(queryProcessor)
                .subscribe(testSubscriber);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                queryEngineClient.onNext(qRequest);
                queryEngineClient.onCompleted();
            } catch (InterruptedException ignore) {}
        }).start();

        testSubscriber.awaitTerminalEvent(10, SECONDS);
        assertTrue(testSubscriber.getOnNextEvents().contains(
                new QueryResult(FINAL_RESULT, "1", RAW_FINAL_RESPONSE)
        ));
    }

    @Test
    public void shouldProcessUnsubscribe() throws Exception {
        PublishSubject<QueryRequest> queryEngineClient = PublishSubject.create();

        queryEngineClient
                .compose(queryProcessor)
                .subscribe(testSubscriber);

        new Thread(() -> {
            try {
                queryEngineClient.onNext(qRequest);
                Thread.sleep(200);
                testSubscriber.unsubscribe();
            } catch (InterruptedException ignore) {}
        }).start();

        Thread.sleep(1000);
        assertFalse(testSubscriber.getOnNextEvents().contains(
                new QueryResult(FINAL_RESULT, "1", RAW_FINAL_RESPONSE)
        ));
    }

    @Test
    public void shouldDoRequestValidation() throws Exception {
        just(eRequest)
                .compose(queryProcessor)
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertReceivedOnNext(singletonList(new QueryResult(REQUEST_ERROR, "1", EMPTY_QUERY_MESSAGE)));
    }

    // BL classes -----------------

    /**
     * This processor should:
     * - start main query processing,
     * - report progress of execution
     * - provide intermediate results
     *
     * Protocol description (client):
     * - client send REQUEST -> processing starts
     * - client send CANCELLATION -> processing is interrupted
     * - client closes request stream or unsubscribed -> processing is interrupted
     * Protocol description (processor):
     * - processor sends ERROR in case of error (parse or execution)
     * - processor sends PROGRESS with message (% of execution, execution phase)
     * - processor sends INTERMEDIATE_RESULT if
     */
    static class QueryProcessor implements Observable.Transformer<QueryRequest, QueryResult> {
        private static final Logger log = LoggerFactory.getLogger(QueryProcessor.class);

        static final String RAW_FINAL_RESPONSE = "{ \"value\": 100 }";
        static final String CANCELLED_MESSAGE = "Cancelled per user request";

        @Override
        public Observable<QueryResult> call(Observable<QueryRequest> requests) {
            ConnectableObservable<QueryRequest> connectableRequests = requests
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .publish();

            ConnectableObservable<QueryRequest> stopStream =
                    concat(connectableRequests
                                    .refCount()
                                    .filter(this::isCancellation), never())
                            .doOnNext(r -> log.info("Cancellation request received"))
                            .publish();

            Observable<QueryResult> results = connectableRequests
                    .refCount()
                    // Logging
                    .doOnSubscribe(() -> log.info("QueryProcessor started"))
                    .doOnNext(req -> log.info("Processing request: {}", req))
                    // BL
                    .takeWhile(req -> !isCancellation(req))
                    .map(this::validateRequest)
                    .compose(r -> mainRequestProcessor(r, stopStream.refCount()))
                    //.compose(r -> r.map( req -> new QueryResult(FINAL_RESULT, req.getRequestId(), RAW_FINAL_RESPONSE)))
                    .onErrorReturn(this::handleProcessingErrors)
                    // Logging
                    .doOnUnsubscribe(() -> log.info("Client unsubscribed"))
                    .doOnTerminate(() -> log.info("QueryProcessor finished"))
                    .doOnNext(resp -> log.info("Produced result: {}", resp));

            stopStream.connect();
            connectableRequests.connect();
            return results;
        }

        boolean isCancellation(QueryRequest req) {
            return CANCELLATION.equals(req.getType());
        }

        Observable<QueryResult> mainRequestProcessor(Observable<QueryRequestParsed> request, Observable<QueryRequest> stopStream) {
            int totalTime = 500;
            int progressTickTime = 50;

            ConnectableObservable<QueryRequestParsed> connectableRequests = request.publish();

            Observable<QueryResult> resultStream = mainQuery(connectableRequests.refCount(), totalTime);
            Observable<QueryResult> progressStream = mainQueryProgress(connectableRequests.refCount(), resultStream, totalTime, progressTickTime);
            Observable<QueryResult> intermediateResultsStream = intermediateResults(connectableRequests.refCount(), resultStream, totalTime, progressTickTime);

            Observable<QueryResult> result = merge(
                    resultStream,
                    progressStream,
                    intermediateResultsStream
            );

            Observable<QueryResult> stopResponse = stopStream
                    .map(r -> new QueryResult(CANCELLATION_DONE, r.getRequestId(), CANCELLED_MESSAGE))
                    .takeUntil(result.filter(r -> false));

            connectableRequests.connect();
            return merge(result.takeUntil(stopStream), stopResponse);
        }

        private Observable<QueryResult> intermediateResults(Observable<QueryRequestParsed> cachedRequest, Observable<QueryResult> resultStream, int total_time, int progress_tick_time) {
            return cachedRequest
                        .subscribeOn(Schedulers.computation())
                        .flatMap(r ->
                                interval(progress_tick_time, MILLISECONDS)
                                        .map(i -> {
                                            String progress = "{ \"value\": " + 100 * (i + 1) * progress_tick_time / total_time + "}";
                                            return new QueryResult(INTERMEDIATE_RESULT, r.getRequestId(), progress);
                                        }))
                        .takeUntil(resultStream);
        }

        private Observable<QueryResult> mainQueryProgress(Observable<QueryRequestParsed> cachedRequest, Observable<QueryResult> resultStream, int total_time, int progress_tick_time) {
            return cachedRequest
                        .subscribeOn(Schedulers.computation())
                        .flatMap(r ->
                                interval(progress_tick_time, MILLISECONDS)
                                        .map(i -> {
                                            String progress = 100 * (i + 1) * progress_tick_time / total_time + "%";
                                            return new QueryResult(PROGRESS, r.getRequestId(), progress);
                                        }))
                        .takeUntil(resultStream);
        }

        private Observable<QueryResult> mainQuery(Observable<QueryRequestParsed> cachedRequest, int delayTime) {
            return cachedRequest
                            .subscribeOn(Schedulers.computation())
                            .delay(delayTime, MILLISECONDS)
                            .map(r -> new QueryResult(FINAL_RESULT, r.getRequestId(), RAW_FINAL_RESPONSE));
        }

        QueryResult handleProcessingErrors(Throwable e) {
            if (e instanceof RequestValidationException) {
                String requestId = ((RequestValidationException) e).getRequest().getRequestId();
                return new QueryResult(REQUEST_ERROR, requestId, e.getMessage());
            } else {
                return new QueryResult(PROCESSING_ERROR, "unknown", e.getMessage());
            }
        }

        QueryRequestParsed validateRequest(QueryRequest r) {
            if (r.getQuery() == null || r.getQuery().trim().isEmpty()) {
                throw new RequestValidationException(r, EMPTY_QUERY_MESSAGE);
            }
            log.info("Request validated: {}", r.getQuery());
            return new QueryRequestParsed(r.getType(), r.getRequestId(), r.getQuery());
        }
    }

    // Model classes --------------

    enum RequestType {
        REQUEST, CANCELLATION
    }

    @Data
    static class QueryRequest {
        private final RequestType type;
        private final String requestId;
        private final String query;
    }

    @Data
    static class QueryRequestParsed {
        private final RequestType type;
        private final String requestId;
        private final String query;
    }

    enum ResultType {
        FINAL_RESULT, REQUEST_ERROR, PROCESSING_ERROR, CANCELLATION_DONE, INTERMEDIATE_RESULT, PROGRESS
    }

    @Data
    static class QueryResult {
        private final ResultType type;
        private final String requestId;
        private final String body;
    }

    static class RequestValidationException extends RuntimeException {
        public static final String EMPTY_QUERY_MESSAGE = "Query shouldn't not be empty";

        @Getter
        private final QueryRequest request;

        public RequestValidationException(QueryRequest request, String message) {
            super(message);
            this.request = request;
        }
    }
}
