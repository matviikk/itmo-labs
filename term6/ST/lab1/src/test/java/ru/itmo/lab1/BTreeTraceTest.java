package ru.itmo.lab1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BTreeTraceTest {

    @Test
    @DisplayName("Dataset A: insert 3 keys, no splits")
    void datasetA_noSplits() {
        ListTracer tracer = new ListTracer();
        BTree tree = new BTree(2, tracer);

        tree.insert(new CompositeKey(10));
        tree.insert(new CompositeKey(20));
        tree.insert(new CompositeKey(5));

        List<TracePoint> expected = List.of(
                TracePoint.INSERT_START, TracePoint.INSERT_INTO_LEAF,
                TracePoint.INSERT_START, TracePoint.INSERT_INTO_LEAF,
                TracePoint.INSERT_START, TracePoint.INSERT_INTO_LEAF);

        assertEquals(expected, tracer.snapshot());
    }

    @Test
    @DisplayName("Dataset B: 4th insert triggers root split")
    void datasetB_rootSplit() {
        ListTracer tracer = new ListTracer();
        BTree tree = new BTree(2, tracer);

        tree.insert(new CompositeKey(10));
        tree.insert(new CompositeKey(20));
        tree.insert(new CompositeKey(5));
        tree.insert(new CompositeKey(6));

        List<TracePoint> expected = List.of(
                TracePoint.INSERT_START, TracePoint.INSERT_INTO_LEAF,
                TracePoint.INSERT_START, TracePoint.INSERT_INTO_LEAF,
                TracePoint.INSERT_START, TracePoint.INSERT_INTO_LEAF,

                TracePoint.INSERT_START,
                TracePoint.ROOT_FULL_SPLIT,
                TracePoint.SPLIT_CHILD_START,
                TracePoint.SPLIT_CHILD_PROMOTE_MIDDLE,
                TracePoint.SPLIT_CHILD_DONE,
                TracePoint.INSERT_INTO_INTERNAL,
                TracePoint.DESCEND_TO_CHILD,
                TracePoint.INSERT_INTO_LEAF);

        assertEquals(expected, tracer.snapshot());
    }

    @Test
    @DisplayName("Dataset C: split child during insert")
    void datasetC_childSplit() {
        ListTracer tracer = new ListTracer();
        BTree tree = new BTree(2, tracer);

        tree.insert(new CompositeKey(10));
        tree.insert(new CompositeKey(20));
        tree.insert(new CompositeKey(5));
        tree.insert(new CompositeKey(6));
        tree.insert(new CompositeKey(12));
        tree.insert(new CompositeKey(30));
        tree.insert(new CompositeKey(25));

        List<TracePoint> expected = List.of(
                // 1
                TracePoint.INSERT_START, TracePoint.INSERT_INTO_LEAF,
                // 2
                TracePoint.INSERT_START, TracePoint.INSERT_INTO_LEAF,
                // 3
                TracePoint.INSERT_START, TracePoint.INSERT_INTO_LEAF,
                // 4
                TracePoint.INSERT_START,
                TracePoint.ROOT_FULL_SPLIT,
                TracePoint.SPLIT_CHILD_START,
                TracePoint.SPLIT_CHILD_PROMOTE_MIDDLE,
                TracePoint.SPLIT_CHILD_DONE,
                TracePoint.INSERT_INTO_INTERNAL,
                TracePoint.DESCEND_TO_CHILD,
                TracePoint.INSERT_INTO_LEAF,
                // 5
                TracePoint.INSERT_START,
                TracePoint.INSERT_INTO_INTERNAL,
                TracePoint.DESCEND_TO_CHILD,
                TracePoint.INSERT_INTO_LEAF,
                // 6
                TracePoint.INSERT_START,
                TracePoint.INSERT_INTO_INTERNAL,
                TracePoint.DESCEND_TO_CHILD,
                TracePoint.INSERT_INTO_LEAF,
                // 7
                TracePoint.INSERT_START,
                TracePoint.INSERT_INTO_INTERNAL,
                TracePoint.DESCEND_TO_CHILD,
                TracePoint.SPLIT_CHILD_START,
                TracePoint.SPLIT_CHILD_PROMOTE_MIDDLE,
                TracePoint.SPLIT_CHILD_DONE,
                TracePoint.INSERT_INTO_LEAF);

        assertEquals(expected, tracer.snapshot());
    }

    @Test
    @DisplayName("Dataset D: contains traces (hit/miss)")
    void datasetD_searchTraces() {
        ListTracer tracer = new ListTracer();
        BTree tree = new BTree(2, tracer);

        // dataset C inserts
        tree.insert(new CompositeKey(10));
        tree.insert(new CompositeKey(20));
        tree.insert(new CompositeKey(5));
        tree.insert(new CompositeKey(6));
        tree.insert(new CompositeKey(12));
        tree.insert(new CompositeKey(30));
        tree.insert(new CompositeKey(7));

        tracer.clear();

        tree.contains(new CompositeKey(12));
        List<TracePoint> expectedHit = List.of(
                TracePoint.SEARCH_START,
                TracePoint.SEARCH_DESCEND,
                TracePoint.SEARCH_HIT);
        assertEquals(expectedHit, tracer.snapshot());

        tracer.clear();

        tree.contains(new CompositeKey(13));
        List<TracePoint> expectedMiss = List.of(
                TracePoint.SEARCH_START,
                TracePoint.SEARCH_DESCEND,
                TracePoint.SEARCH_MISS);
        assertEquals(expectedMiss, tracer.snapshot());
    }
}