package deque;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;



import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void testThreeAddThreeRemove() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        LinkedListDeque<Integer> b = new LinkedListDeque<>();
        for (int i = 4; i < 7; i++) {
            a.addLast(i);
            b.addLast(i);
        }
        for (int j = 6; j > 3; j--) {
            assertEquals(a.removeLast(), b.removeLast());
        }
    }

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        LinkedListDeque<Integer> M = new LinkedListDeque<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                M.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int sizeL = L.size();
                int sizeM = M.size();
                assertEquals(sizeL, sizeM);
            } else {
                // removeLast
                if (L.size() == 0 || M.size() == 0) {
                    continue;
                }
                assertEquals(L.removeLast(), M.removeLast());
                assertEquals(L.size(), M.size());
            }
        }
    }

    @Test
    public void addRemoveTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        LinkedListDeque<Integer> b = new LinkedListDeque<>();
        for (int i = 0; i < 1000; i++) {
            int rand = StdRandom.uniform(0, 1000);
            int order = StdRandom.uniform(0, 2);
            if (order == 0) {
                a.addFirst(rand);
                b.addFirst(rand);
            } else {
                a.addLast(rand);
                b.addLast(rand);
            }
        }
        assertEquals(a.get(0), b.get(0));
        assertEquals(a.size(), b.size());

        for (int i = 0; i < 1000; i++) {
            int order = StdRandom.uniform(0, 2);
            if (order == 0) {
                a.removeFirst();
                b.removeFirst();
            } else {
                a.removeLast();
                b.removeLast();
            }
        }
        assertEquals(a.get(0), b.get(0));
        assertEquals(a.size(), b.size());
    }

    @Test
    public void equalsTest() {
        LinkedListDeque<Integer> m = new LinkedListDeque<>();
        m.addLast(5);
        m.addLast(6);
        m.addLast(7);
        ArrayDeque<Integer> n = new ArrayDeque<>();
        n.addLast(5);
        n.addLast(6);
        n.addLast(7);
        assertEquals(true, m.equals(n));
    }
}
