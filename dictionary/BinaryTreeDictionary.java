// O. Bittel
// 22.09.2022
package dictionary;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Implementation of the Dictionary interface as AVL tree.
 * <p>
 * The entries are ordered using their natural ordering on the keys,
 * or by a Comparator provided at set creation time, depending on which constructor is used.
 * <p>
 * An iterator for this dictionary is implemented by using the parent node reference.
 *
 * @param <K> Key.
 * @param <V> Value.
 */
public class BinaryTreeDictionary<K, V> implements Dictionary<K, V> {

    private final Comparator<? super K> cmp;

    private Node<K, V> root;
    private int size = 0;
    private V oldValue;

    public BinaryTreeDictionary() {
        this(null);
    }

    public BinaryTreeDictionary(Comparator<? super K> cmp) {
        if (cmp == null) {
            this.cmp = (x, y) -> ((Comparable<? super K>) x).compareTo(y);
        } else {
            this.cmp = cmp;
        }

    }

    private static void printLevel(int level) {
        if (level == 0) {
            return;
        }

        for (int i = 0; i < level - 1; i++) {
            System.out.print("   ");
        }

        System.out.print("|__");
    }

    private V searchR(K key, Node<K,V> p) {
        if (p == null) {
            return null;
        }

        if (cmp.compare(key, p.key) < 0) {
            return searchR(key, p.left);
        }

        if (cmp.compare(key, p.key) > 0) {
            return searchR(key, p.right);
        }

        return p.value;
    }

    private Node<K,V> insertR(K key, V value, Node<K,V> p) {
        if (p == null) {
            p = new Node<>(key, value);

            oldValue = null;

            ++size;
        } else if (cmp.compare(key, p.key) < 0) {
            p.left = insertR(key, value, p.left);

            p.left.parent = p;
        } else if (cmp.compare(key, p.key) > 0) {
            p.right = insertR(key, value, p.right);

            p.right.parent = p;
        } else {
            oldValue = p.value;

            p.value = value;
        }

        return p;
    }

    private Node<K,V> removeR(K key, Node<K,V> p) {
        if (p == null) {
            oldValue = null;
        } else if(cmp.compare(key, p.key) < 0) {
            p.left = removeR(key, p.left);

            if (p.left != null) {
                p.left.parent = p;
            }
        } else if (cmp.compare(key, p.key) > 0) {
            p.right = removeR(key, p.right);

            if (p.right != null) {
                p.right.parent = p;
            }
        } else if (p.left == null || p.right == null) {
            oldValue = p.value;

            p = (p.left != null) ? p.left : p.right;

            --size;
        } else {
            MinEntry<K,V> min = new MinEntry<>();

            p.right = getRemMinR(p.right, min);

            if (p.right != null) {
                p.right.parent = p;
            }

            oldValue = p.value;

            p.key = min.key;
            p.value = min.value;

            --size;
        }

        return p;
    }

    private Node<K,V> getRemMinR(Node<K,V> p, MinEntry<K,V> min) {
        if (p.left == null) {
            min.key = p.key;
            min.value = p.value;

            p = p.right;
        } else {
            p.left = getRemMinR(p.left, min);
        }

        return p;
    }


    @Override
    public V insert(K key, V value) {
        root = insertR(key, value, root);

        return oldValue;
    }

    @Override
    public V search(K key) {
        return searchR(key, root);
    }

    @Override
    public V remove(K key) {
        root = removeR(key, root);

        return oldValue;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Iterator<>() {
            Node<K,V> p;

            {
                if (root != null) {
                    p = leftMostDescendant(root);
                }

            }

            @Override
            public boolean hasNext() {
                return p != null;
            }

            @Override
            public Entry<K, V> next() {
                Entry<K, V> entry = new Entry<>(p.key, p.value);

                if (p.right != null) {
                    p= leftMostDescendant(p.right);
                } else {
                    p = parentOfLeftMostAncestor(p);
                }

                return entry;

            }

            private Node<K,V> leftMostDescendant(Node<K,V> p) {
                while (p.left != null) {
                    p = p.left;
                }

                return p;
            }

            private Node<K,V> parentOfLeftMostAncestor(Node<K,V> p) {
                while (p.parent != null && p.parent.right == p) {
                    p = p.parent;
                }

                return p.parent;
            }
        };
    }

    /**
     * Pretty prints the tree
     */
    public void prettyPrint() {
        printR(0, root);
    }

    private void printR(int level, Node<K, V> p) {
        printLevel(level);

        if (p == null) {
            System.out.println("#");
        } else {
            System.out.println(p.key + " " + p.value + "^" + ((p.parent == null) ? "null" : p.parent.key));

            if (p.left != null || p.right != null) {
                printR(level + 1, p.left);
                printR(level + 1, p.right);
            }
        }
    }

    private static class MinEntry<K, V> {
        K key;
        V value;
    }

    private static class Node<K, V> {
        K key;
        V value;
        int height;
        Node<K, V> left;
        Node<K, V> right;
        Node<K, V> parent;

        Node(K k, V v) {
            key = k;
            value = v;
            height = 0;
            left = null;
            right = null;
            parent = null;
        }
    }
}
