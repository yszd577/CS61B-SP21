package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.HashSet;
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
        } else {
            node.val = val;
        }
        node.num = 1 + size(node.left) + size(node.right);
        return node;
    }

    @Override
    public Set<K> keySet() {
        Set<K> BSTSet = new HashSet<>();
        keySet(root, BSTSet);
        return BSTSet;
    }

    private void keySet(BSTNode node, Set<K> BSTSet) {
        if (node == null) {
            return;
        }
        keySet(node.left, BSTSet);
        BSTSet.add(node.key);
        keySet(node.right, BSTSet);
    }

    @Override
    public V remove(K key) {
        V value = get(key);
        root = remove(key, root);
        return value;
    }

    @Override
    public V remove(K key, V value) {
        V val = get(key);
        if (containsKey(key) && val.equals(value)) {
            root = remove(key, root);
            return val;
        }
        return value;
    }

    private BSTNode remove(K key, BSTNode node) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(key, node.left);
        } else if (cmp > 0) {
            node.right = remove(key, node.right);
        } else {
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            BSTNode tmp = node;
            node = successor(tmp.right);
            node.right = deleteSuccessor(tmp.right);
            node.left = tmp.left;
        }
        node.num = 1 + size(node.left) + size(node.right);
        return node;
    }

    private BSTNode successor(BSTNode node) {
        if (node.left == null) {
            return node;
        }
        return successor(node.left);
    }

    private BSTNode deleteSuccessor(BSTNode node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = deleteSuccessor(node.left);
        node.num = size(node.left) + size(node.right) + 1;
        return node;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

}
