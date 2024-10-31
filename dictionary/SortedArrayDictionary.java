package dictionary;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public class SortedArrayDictionary<K, V> implements Dictionary<K, V> {

    private static final int DEF_CAPACITY = 16;

    private final Comparator<? super K> cmp;

    private int size;
    private Entry<K, V>[] data;

    public SortedArrayDictionary() {
        this(null);
    }

    public SortedArrayDictionary(Comparator<? super K> cmp) {
        if (cmp == null) {
            this.cmp = (x, y) -> ((Comparable<? super K>) x).compareTo(y);
            ;
        } else {
            this.cmp = cmp;
        }

        size = 0;
        data = new Entry[DEF_CAPACITY];
    }

    private int searchKey(K key) {
        int li = 0;
        int re = size - 1;
        while (re >= li) {
            int m = (li + re) / 2;
            if (cmp.compare(key, data[m].getKey()) < 0) {
                re = m - 1;
            } else if (cmp.compare(key, data[m].getKey()) > 0) {
                li = m + 1;
            } else {
                return m;
            }

        }
        return -1;
    }

    @Override
    public V insert(K key, V value) {
        int i = searchKey(key);

        if (i != -1) {
            V r = data[i].getValue();
            data[i].setValue(value);
            return r;
        }

        if (data.length == size) {
            data = Arrays.copyOf(data, 2 * size);
        }
        int j = size - 1;
        while (j >= 0 && cmp.compare(key, data[j].getKey()) < 0) {
            data[j + 1] = data[j];
            j--;
        }
        data[j + 1] = new Entry<K, V>(key, value);
        size++;
        return null;
    }

    @Override
    public V search(K key) {
        int li = 0;
        int re = size - 1;
        while (re >= li) {
            int m = (li + re) / 2;
            if (key.equals(data[m].getKey())) {
                return data[m].getValue();
            } else if (cmp.compare(key, data[m].getKey()) < 0) {
                re = m - 1;
            } else {
                li = m + 1;
            }

        }
        return null;
    }

    @Override
    public V remove(K key) {
        int i = searchKey(key);
        if (i == -1) {
            return null;
        }
        V r = data[i].getValue();
        for (int j = i; j < size - 1; j++)
            data[j] = data[j + 1];
        data[--size] = null;
        return r;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public Entry<K, V> next() {
                return data[i++];
            }
        };
    }
}
