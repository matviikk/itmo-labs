package ru.itmo.lab1;

import java.util.Arrays;
import java.util.Objects;

public final class BTree {

    private final int t;
    private final BTreeTracer tracer;

    private Node root;

    public BTree(int t, BTreeTracer tracer) {
        if (t < 2) throw new IllegalArgumentException("t must be >= 2");
        this.t = t;
        this.tracer = (tracer == null) ? p -> {} : tracer;
        this.root = new Node(true, 2 * t - 1);
    }

    public Node root() {
        return root;
    }

    public void insert(CompositeKey key) {
        Objects.requireNonNull(key, "key");
        tracer.hit(TracePoint.INSERT_START);

        Node r = root;
        if (r.n == (2 * t - 1)) {
            tracer.hit(TracePoint.ROOT_FULL_SPLIT);
            Node s = new Node(false, 2 * t - 1);
            s.children[0] = r;
            splitChild(s, 0);
            root = s;
            insertNonFull(s, key);
        } else {
            insertNonFull(r, key);
        }
    }

    public boolean contains(CompositeKey key) {
        Objects.requireNonNull(key, "key");
        tracer.hit(TracePoint.SEARCH_START);
        return search(root, key);
    }

    private boolean search(Node x, CompositeKey k) {
        int i = 0;
        while (i < x.n && k.compareTo(x.keys[i]) > 0) i++;

        if (i < x.n && k.compareTo(x.keys[i]) == 0) {
            tracer.hit(TracePoint.SEARCH_HIT);
            return true;
        }

        if (x.leaf) {
            tracer.hit(TracePoint.SEARCH_MISS);
            return false;
        }

        tracer.hit(TracePoint.SEARCH_DESCEND);
        return search(x.children[i], k);
    }

    private void insertNonFull(Node x, CompositeKey k) {
        int i = x.n - 1;

        if (x.leaf) {
            tracer.hit(TracePoint.INSERT_INTO_LEAF);

            while (i >= 0 && k.compareTo(x.keys[i]) < 0) {
                x.keys[i + 1] = x.keys[i];
                i--;
            }
            x.keys[i + 1] = k;
            x.n++;
        } else {
            tracer.hit(TracePoint.INSERT_INTO_INTERNAL);

            while (i >= 0 && k.compareTo(x.keys[i]) < 0) i--;
            i++;

            tracer.hit(TracePoint.DESCEND_TO_CHILD);

            if (x.children[i].n == (2 * t - 1)) {
                splitChild(x, i);
                if (k.compareTo(x.keys[i]) > 0) i++;
            }
            insertNonFull(x.children[i], k);
        }
    }

    private void splitChild(Node parent, int i) {
        tracer.hit(TracePoint.SPLIT_CHILD_START);

        Node y = parent.children[i];
        Node z = new Node(y.leaf, 2 * t - 1);

        z.n = t - 1;

        for (int j = 0; j < t - 1; j++) {
            z.keys[j] = y.keys[j + t];
            y.keys[j + t] = null;
        }

        if (!y.leaf) {
            for (int j = 0; j < t; j++) {
                z.children[j] = y.children[j + t];
                y.children[j + t] = null;
            }
        }

        y.n = t - 1;

        for (int j = parent.n; j >= i + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[i + 1] = z;

        for (int j = parent.n - 1; j >= i; j--) {
            parent.keys[j + 1] = parent.keys[j];
        }

        tracer.hit(TracePoint.SPLIT_CHILD_PROMOTE_MIDDLE);
        parent.keys[i] = y.keys[t - 1];
        y.keys[t - 1] = null;

        parent.n++;

        tracer.hit(TracePoint.SPLIT_CHILD_DONE);
    }


    public static final class Node {
        final boolean leaf;
        int n;
        final CompositeKey[] keys;
        final Node[] children;

        Node(boolean leaf, int maxKeys) {
            this.leaf = leaf;
            this.keys = new CompositeKey[maxKeys];
            this.children = new Node[maxKeys + 1];
            this.n = 0;
        }

        @Override
        public String toString() {
            return "Node{leaf=" + leaf + ", n=" + n + ", keys=" + Arrays.toString(keys) + "}";
        }
    }
}