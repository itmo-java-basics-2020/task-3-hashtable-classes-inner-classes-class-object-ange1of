package ru.itmo.java;

import java.util.Map;
import java.util.Objects;

public class HashTable {

    public HashTable() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(double loadFactor) {
        this(DEFAULT_INITIAL_CAPACITY, loadFactor);
    }

    public HashTable(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(int initialCapacity, double loadFactor) {
        this.loadFactor = Math.max(Math.min(loadFactor, 1), 0);
        this.elements = new Entry[initialCapacity];

        this.capacity = initialCapacity;
        this.threshold = (int)(this.capacity * this.loadFactor);
    }

    public Object put(Object key, Object value) {
        Entry newElement = new Entry(key, value);
        int keyIndex = findKeyIndex(key);

        Object previousValue;

        if (keyIndex != -1) {
            previousValue = elements[keyIndex].value;
            elements[keyIndex] = newElement;

            return previousValue;
        }

        int index = hash(key);
        int currentIndex = index;
        int iteration = 0;

        while (!(elements[currentIndex] == null || elements[currentIndex].deleted)) {
            iteration++;
            currentIndex = (index + iteration * 7) % capacity;
        }

        elements[currentIndex] = newElement;
        size++;
        checkResize();

        return null;
    }

    public Object get(Object key) {
        int index = findKeyIndex(key);

        if (index == -1) {
            return null;
        }
        return elements[index].value;
    }

    public Object remove(Object key) {
        int index = findKeyIndex(key);
        if (index == -1) {
            return null;
        }

        Object removedValue = elements[index].value;
        elements[index] = DELETED_ENTRY;
        size--;

        return removedValue;
    }

    public int size() {
        return this.size;
    }

    private int hash(Object key) {
        return (int)(capacity * (0.618 * Math.abs(Objects.hashCode(key)) % 1));
    }

    private void checkResize() {
        if (size < threshold) {
            return;
        }

        Entry[] oldElements = elements;

        capacity *= 2;
        elements = new Entry[capacity];
        size = 0;
        threshold = (int)(capacity * loadFactor);

        for (Entry element : oldElements) {
            if (!(element == null || element.deleted)) {
                put(element.key, element.value);
            }
        }
    }

    private int findKeyIndex(Object key) {
        int index = hash(key);
        int currentIndex = index;
        int iteration = 0;
        while (elements[currentIndex] != null && (!Objects.equals(key, elements[currentIndex].key) || elements[currentIndex].deleted)) {
            iteration++;
            currentIndex = (index + iteration * 7) % capacity;
        }
        if (elements[currentIndex] == null) {
            return -1;
        }

        return currentIndex;
    }


    private static class Entry {
        public Entry(Object key, Object value) {
            this(key, value, false);
        }

        public Entry(Object key, Object value, boolean deleted) {
            this.key = key;
            this.value = value;
            this.deleted = deleted;
        }

        public final Object key;
        public final Object value;
        public final boolean deleted;
    }


    private Entry[] elements;

    private final double loadFactor;
    private int capacity;
    private int threshold;
    private int size = 0;


    private static final double DEFAULT_LOAD_FACTOR = 0.5;
    private static final int DEFAULT_INITIAL_CAPACITY = 500;
    private static final Entry DELETED_ENTRY = new Entry(null, null, true);
}
