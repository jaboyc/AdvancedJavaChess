package com.jlogical.speedchess.cpu;

/**
 * Stores a pair of items.
 */
public class Pair<T, K> {
    private T first; // The first item.
    private K second; // The second item.

    /**
     * Creates a new pair.
     */
    public Pair(T first, K second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public K getSecond() {
        return second;
    }

    public String toString(){
        return "(" + getFirst() + "," + getSecond()+ ")";
    }
}
