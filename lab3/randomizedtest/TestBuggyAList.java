package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> a = new AListNoResizing<>();
        BuggyAList<Integer> b = new BuggyAList<>();
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
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> M = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
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
            } else if (operationNumber == 2) {
                // getLast
                if (L.size() == 0 || M.size() == 0) {
                    continue;
                }
                assertEquals(L.getLast(), M.getLast());
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
}
