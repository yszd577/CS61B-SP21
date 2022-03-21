package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V>  implements Map61B<K, V> {
    BSTNode root;

    private class BSTNode {
        private K key;
        private V val;
        private BSTNode left, right;
        private int num;

        public BSTNode(K key, V val, int num) {
            this.key = key;
            this.val = val;
            this.num = num;
        }
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        return find(key, root) != null;
    }

    private BSTNode find(K key, BSTNode node) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node;
        } else if (cmp > 0) {
            return find(key, node.right);
        } else {
            return find(key, node.left);
        }
    }

    @Override
    public V get(K key) {
        BSTNode node = find(key, root);
        return node == null ? null : node.val;
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(BSTNode node) {
        if (node == null) {
            return 0;
        }
        return node.num;
    }

    @Override
    public void put(K key, V value) {
        root = insert(root, key, value);
    }

    private BSTNode insert(BSTNode node, K key, V val) {
        if (node == null) {
            return new BSTNode(key, val, 1);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key, val);
        } else if (cmp > 0) {
            node.right = insert(node.right, key, val);
        }
        node.val = val;
        node.num = 1 + size(node.left) + size(node.right);
        return node;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

}
