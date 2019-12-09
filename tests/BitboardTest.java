import com.jlogical.speedchess.bitboard.Bitboard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BitboardTest {

    @Test
    public void testSet() {
        Bitboard board = new Bitboard();
        assertFalse(board.get(0));
        assertFalse(board.get(0, 0));

        board.set(0, 0);

        assertTrue(board.get(0));
        assertTrue(board.get(0, 0));
    }

    @Test
    public void testClear() {
        Bitboard board = new Bitboard();

        board.set(1, 1);
        board.set(5, 2);

        board.clear(1, 1);
        board.clear(5, 2);

        assertFalse(board.get(1, 1));
        assertFalse(board.get(5, 2));
    }

    @Test
    public void testIntersection() {
        Bitboard b1 = new Bitboard();
        Bitboard b2 = new Bitboard();

        for (int i = 0; i < 64; i += 4) {
            b1.set(i);
        }

        assertEquals(b1.count(), 16);

        for (int i = 0; i < 64; i += 7) {
            b2.set(i);
        }

        assertEquals(b2.count(), 10);

        Bitboard intersect = Bitboard.and(b1, b2);
        assertTrue(intersect.get(0));
        assertFalse(intersect.get(4));
        assertEquals(intersect.count(), 3);

        assertEquals(intersect.toString(),
                "*X* * * * * * * *\n" +
                        "* * * * * * * * *\n" +
                        "* * * * * * * * *\n" +
                        "* * * * * * * * *\n" +
                        "* * * * *X* * * *\n" +
                        "* * * * * * * * *\n" +
                        "* * * * * * * * *\n" +
                        "*X* * * * * * * *\n");

        assertEquals(intersect, Bitboard.and(b1, b2, b1));
    }

    @Test
    public void testUnion() {
        Bitboard b1 = new Bitboard();
        Bitboard b2 = new Bitboard();

        for (int i = 0; i < 64; i += 4) {
            b1.set(i);
        }

        assertEquals(b1.count(), 16);

        for (int i = 0; i < 64; i += 7) {
            b2.set(i);
        }

        assertEquals(b2.count(), 10);

        Bitboard union = Bitboard.or(b1, b2);
        assertTrue(union.get(0));
        assertTrue(union.get(4));
        assertEquals(union.count(), 23);

        assertEquals(union.toString(),
                "*X* * * *X* * *X*\n" +
                        "*X*X* * *X* * * *\n" +
                        "*X* *X* *X* * * *\n" +
                        "*X* * *X*X* * * *\n" +
                        "*X* * * *X* * * *\n" +
                        "*X* * * *X*X* * *\n" +
                        "*X* * * *X* *X* *\n" +
                        "*X* * * *X* * *X*\n");

        assertEquals(union, Bitboard.or(b1, b2, b1));
    }

    @Test
    public void testString() {
        Bitboard board = new Bitboard();
        board.set(0, 0);
        board.set(1, 2);
        board.set(6, 6);
        board.set(7, 2);
        board.set(4, 7);

        assertEquals(board.toString(),
                "* * * * *X* * * *\n" +
                        "* * * * * * *X* *\n" +
                        "* * * * * * * * *\n" +
                        "* * * * * * * * *\n" +
                        "* * * * * * * * *\n" +
                        "* *X* * * * * *X*\n" +
                        "* * * * * * * * *\n" +
                        "*X* * * * * * * *\n");
    }
}
