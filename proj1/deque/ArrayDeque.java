package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    private void resizeLarge(int capacity) {
        T[] a = (T[]) new Object[capacity];
        System.arraycopy(items, 0, a, 0, nextLast);
        nextFirst = nextFirst + capacity - items.length;
        System.arraycopy(items, nextLast, a, nextFirst, items.length - nextLast);
        items = a;
    }

    private void resizeSmall(int capacity) {
        T[] a = (T[]) new Object[capacity];
        if (nextFirst < nextLast) {
            System.arraycopy(items, nextFirst, a, 0, nextLast - nextFirst);
            nextFirst = 0;
            nextLast = nextFirst + size + 1;
        } else {
            System.arraycopy(items, 0, a, 0, nextLast);
            System.arraycopy(items, nextFirst, a, nextFirst - items.length + capacity,
                    items.length - nextFirst);
            nextFirst = nextFirst - items.length + capacity;
        }
        items = a;
    }

    @Override
    public void addFirst(T item) {
        if (nextFirst == nextLast) {
            resizeLarge((int) (size * 1.5));
        }
        items[nextFirst] = item;
        nextFirst = Math.floorMod(nextFirst - 1, items.length);
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (nextFirst == nextLast) {
            resizeLarge((int) (size * 1.5));
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = (nextFirst + 1) % items.length; i != nextLast; i = (i + 1) % items.length) {
            System.out.print(items[i]);
            System.out.print(' ');
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) { return null; }
        if (items.length >= 16 && size < items.length / 4) {
            resizeSmall(items.length / 2);
        }
        int index = (nextFirst + 1) % items.length;
        nextFirst = index;
        size -= 1;
        return items[index];
    }

    @Override
    public T removeLast() {
        if (isEmpty()) { return null; }
        if (items.length >= 16 && size < items.length / 4) {
            resizeSmall(items.length / 2);
        }
        int index = Math.floorMod(nextLast - 1, items.length);
        nextLast = index;
        size -= 1;
        return items[index];
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        int indexReal = (nextFirst + 1 + index) % items.length;
        return items[indexReal];
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int pos;

        ArrayDequeIterator() {
            pos = (nextFirst + 1) % items.length;
        }

        @Override
        public boolean hasNext() {
            return pos != nextLast;
        }

        @Override
        public T next() {
            T returnItem = items[pos];
            pos = (pos + 1) % items.length;
            return returnItem;
        }
    }

    public Iterator<T>  iterator() {
        return new ArrayDequeIterator();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Deque) {
            Deque<T> other = (Deque<T>) o;
            if (this.size() != other.size()) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (!this.get(i).equals(other.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
