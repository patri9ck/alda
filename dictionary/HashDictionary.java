package dictionary;

import java.util.Iterator;

public class HashDictionary<K, V> implements Dictionary<K, V> {

    private static final int[] PRIMES = {
            3, 7, 11, 17, 23, 29, 37, 47, 59, 71, 89, 107, 131, 163, 197, 239, 293, 353, 431, 521, 631, 761, 919,
            1103, 1327, 1597, 1931, 2333, 2801, 3371, 4049, 4861, 5839, 7013, 8419, 10103, 12143, 14591,
            17519, 21023, 25229, 30293, 36353, 43627, 52361, 62851, 75431, 90523, 108631, 130363, 156437,
            187751, 225307, 270371, 324449, 389357, 467237, 560689, 672827, 807403, 968897, 1162687, 1395263,
            1674319, 2009191, 2411033, 2893249, 3471899, 4166287, 4999559, 5999471, 7199369};

    private static final int DEF_CAPACITY = 31;

    private int size;
    private Node<K, V>[] data;

    public HashDictionary() {
        this(DEF_CAPACITY);
    }

    public HashDictionary(int capacity) {
        size = 0;
        data = new Node[capacity];
    }

    private boolean isPrime(int candidate) {
        if ((candidate & 1) != 0) {
            int limit = (int) Math.sqrt(candidate);

            for (int divisor = 3; divisor <= limit; divisor += 2) {

                if ((candidate % divisor) == 0) {
                    return false;
                }
            }

            return true;
        }

        return (candidate == 2);
    }

    private int getPrime(int min) {
        for (int prime : PRIMES) {
            if (prime >= min) {
                return prime;
            }
        }

        for (int i = (min | 1); i < Integer.MAX_VALUE; i += 2) {
            if (isPrime(i)) {
                return i;
            }
        }

        return min;
    }

    private int hash(K key, int length) {
        return Math.abs(key.hashCode()) % length;
    }

    private Entry<K, V> search(K key, int hash) {
        Node<K, V> node = data[hash];

        while (node != null) {
            if (node.entry.getKey().equals(key)) {
                return node.entry;
            }

            node = node.next;
        }

        return null;
    }

    private void insert(Entry<K, V> entry, int hash, Node<K, V>[] data) {
        Node<K, V> node = data[hash];

        if (node == null) {
            data[hash] = new Node<>(entry, null);

            return;
        }

        while (node.next != null) {
            node = node.next;
        }

        node.next = new Node<>(entry, null);
    }

    @Override
    public V insert(K key, V value) {
        int hash = hash(key, data.length);

        Entry<K, V> entry = search(key, hash);

        if (entry == null) {
            ++size;

            entry = new Entry<>(key, value);

            if (size / data.length > 2) {
                Node<K, V>[] data = new Node[getPrime((this.data.length * 2))];

                for (Entry<K, V> e : this) {
                    insert(e, hash(e.getKey(), data.length), data);
                }

                insert(entry, hash(entry.getKey(), data.length), data);

                this.data = data;

                return null;
            }

            insert(entry, hash, data);

            return null;
        }

        V oldValue = entry.getValue();

        entry.setValue(value);

        return oldValue;
    }

    @Override
    public V search(K key) {
        Entry<K, V> entry = search(key, hash(key, data.length));

        if (entry == null) {
            return null;
        }

        return entry.getValue();
    }

    @Override
    public V remove(K key) {
        int hash = hash(key, data.length);

        Node<K, V> node = data[hash];

        if (node == null) {
            return null;
        }

        if (node.entry.getKey().equals(key)) {
            --size;

            data[hash] = null;

            return node.entry.getValue();
        }

        while (node.next != null) {
            if (node.next.entry.getKey().equals(key)) {
                --size;

                V oldValue = node.next.entry.getValue();

                node.next = node.next.next;

                return oldValue;
            }

            node = node.next;
        }

        return null;
    }

    @Override
    public int size() {
        return size;
    }

    public Iterator<Entry<K, V>> iterator() {
        return new Iterator<>() {
            int index = 0;
            Node<K, V> currentNode;

            {
                moveToNextNonEmptyBucket();
            }

            @Override
            public boolean hasNext() {
                return currentNode != null;
            }

            @Override
            public Entry<K, V> next() {
                Entry<K, V> entry = currentNode.entry;
                currentNode = currentNode.next;

                if (currentNode == null) {
                    index++;
                    moveToNextNonEmptyBucket();
                }

                return entry;
            }

            private void moveToNextNonEmptyBucket() {
                while (index < data.length && data[index] == null) {
                    index++;
                }
                if (index < data.length) {
                    currentNode = data[index];
                }
            }
        };
    }

    static class Node<K, V> {
        final Entry<K, V> entry;
        Node<K, V> next;

        public Node(Entry<K, V> entry, Node<K, V> next) {
            this.entry = entry;
            this.next = next;
        }
    }

}