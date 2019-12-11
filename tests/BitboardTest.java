import com.jlogical.speedchess.bitboard.Bitboard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BitboardTest {

    @Test
    public void testSet() {
        long board = 0L;
        assertFalse(Bitboard.get(board,0));
        assertFalse(Bitboard.get(board,0, 0));

        board = Bitboard.set(board,0, 0);

        assertTrue(Bitboard.get(board,0));
        assertTrue(Bitboard.get(board,0, 0));
    }

    @Test
    public void testClear() {
        long board = 0L;

        board = Bitboard.set(board,1, 1);
        board = Bitboard.set(board,5, 2);

        board = Bitboard.clear(board,1, 1);
        board = Bitboard.clear(board,5, 2);

        assertFalse(Bitboard.get(board,1, 1));
        assertFalse(Bitboard.get(board,5, 2));
    }

    @Test
    public void testIntersection() {
        long b1 = 0L;
        long b2 = 0L;

        for (int i = 0; i < 64; i += 4) {
            b1 = Bitboard.set(b1,i);
        }

        assertEquals(Bitboard.count(b1), 16);

        for (int i = 0; i < 64; i += 7) {
            b2 = Bitboard.set(b2,i);
        }

        assertEquals(Bitboard.count(b2), 10);

        long intersect = b1 & b2;
        assertTrue(Bitboard.get(intersect,0));
        assertFalse(Bitboard.get(intersect,4));
        assertEquals(Bitboard.count(intersect), 3);

        assertEquals(Bitboard.format(intersect),
                "*X* * * * * * * *\n" +
                        "* * * * * * * * *\n" +
                        "* * * * * * * * *\n" +
                        "* * * * * * * * *\n" +
                        "* * * * *X* * * *\n" +
                        "* * * * * * * * *\n" +
                        "* * * * * * * * *\n" +
                        "*X* * * * * * * *\n");

        assertEquals(intersect, b1 & b2);
    }

    @Test
    public void testUnion() {
        long b1 = 0L;
        long b2 = 0L;

        for (int i = 0; i < 64; i += 4) {
            b1 = Bitboard.set(b1,i);
        }

        assertEquals(Bitboard.count(b1), 16);

        for (int i = 0; i < 64; i += 7) {
            b2 = Bitboard.set(b2,i);
        }

        assertEquals(Bitboard.count(b2), 10);

        long union = b1 | b2;
        assertTrue(Bitboard.get(union,0));
        assertTrue(Bitboard.get(union,4));
        assertEquals(Bitboard.count(union), 23);

        assertEquals(Bitboard.format(union),
                "*X* * * *X* * *X*\n" +
                        "*X*X* * *X* * * *\n" +
                        "*X* *X* *X* * * *\n" +
                        "*X* * *X*X* * * *\n" +
                        "*X* * * *X* * * *\n" +
                        "*X* * * *X*X* * *\n" +
                        "*X* * * *X* *X* *\n" +
                        "*X* * * *X* * *X*\n");

        assertEquals(union, b1 | b2);
    }

    @Test
    public void testString() {
        long board = 0L;
        board = Bitboard.set(board,0, 0);
        board = Bitboard.set(board,1, 2);
        board = Bitboard.set(board,6, 6);
        board = Bitboard.set(board,7, 2);
        board = Bitboard.set(board,4, 7);

        assertEquals(Bitboard.format(board),
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
