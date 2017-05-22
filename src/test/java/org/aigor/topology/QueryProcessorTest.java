package org.aigor.topology;

import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import static java.util.Collections.singletonList;
import static org.aigor.topology.QueryProcessorTest.RequestType.REQUEST;
import static org.aigor.topology.QueryProcessorTest.ResultType.FINAL_RESULT;
import static rx.Observable.just;

/**
 * Query processor test.
 * Test infrastructure for Query Processing in the way similar to ZD.
 */
public class QueryProcessorTest {
    private TestSubscriber<QueryResult> testSubscriber;

    private QueryProcessor queryProcessor;

    @Before
    public void setUp() throws Exception {
        queryProcessor = new QueryProcessor();
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void shouldProcessEmptyStream() throws Exception {
        Observable.<QueryRequest>empty()
                .compose(queryProcessor)
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();
    }

    @Test
    public void shouldProcessSimpleRequest() throws Exception {
        just(new QueryRequest(REQUEST,"1", "SELECT COUNT(*) FROM events"))
                .compose(queryProcessor)
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertReceivedOnNext(singletonList(new QueryResult(FINAL_RESULT, "1", "10")));
    }

    // BL classes -----------------

    /**
     * This processor should:
     * - start main query processing,
     * - report progress of execution
     * - provide intermediate results (sharpening)
     * - play data?
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
        @Override
        public Observable<QueryResult> call(Observable<QueryRequest> requests) {
            return requests
                    .last()
                    .map(r -> new QueryResult(FINAL_RESULT, r.getRequestId(), "10"));
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

    enum ResultType {
        ERROR, FINAL_RESULT, INTERMEDIATE_RESULT, PROGRESS, PLAYBACK_TICK
    }

    @Data
    static class QueryResult {
        private final ResultType type;
        private final String requestId;
        private final String body;
    }
}
