package migrator.corr;

import static migrator.corr.StringDistance.levenshteinDistance;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringDistanceTest {
    @Test
    public void testLevenshteinDistance() {
        assertEquals(0, levenshteinDistance("", ""));
        assertEquals(1, levenshteinDistance("a", ""));
        assertEquals(3, levenshteinDistance("abc", ""));
        assertEquals(6, levenshteinDistance("abcdef", ""));
        assertEquals(6, levenshteinDistance("", "abcdef"));
        assertEquals(0, levenshteinDistance("abc", "abc"));
        assertEquals(0, levenshteinDistance("abcdef", "abcdef"));
        assertEquals(1, levenshteinDistance("a", "ab"));
        assertEquals(1, levenshteinDistance("a", "ba"));
        assertEquals(1, levenshteinDistance("ab", "abc"));
        assertEquals(1, levenshteinDistance("abc", "abcd"));
        assertEquals(3, levenshteinDistance("abc", "abcdef"));
        assertEquals(4, levenshteinDistance("abc", "xaybzcw"));
        assertEquals(1, levenshteinDistance("a", "b"));
        assertEquals(2, levenshteinDistance("ab", "xy"));
        assertEquals(3, levenshteinDistance("abc", "xyz"));
        assertEquals(6, levenshteinDistance("abcdef", "xyztuv"));
        assertEquals(6, levenshteinDistance("hello", "bonjour"));
        assertEquals(2, levenshteinDistance("hello", "yellow"));
        assertEquals(2, levenshteinDistance("watermelon", "wintermelon"));
        assertEquals(8, levenshteinDistance("watermelon juice", "wintermelon"));
        assertEquals(4, levenshteinDistance("levenshtein", "einstein"));
        assertEquals(624, levenshteinDistance(
                "Sed ut perspiciatis, unde omnis " +
                "iste natus error sit voluptatem " +
                "accusantium doloremque laudantium, " +
                "totam rem aperiam eaque ipsa, " +
                "quae ab illo inventore veritatis et " +
                "quasi architecto beatae vitae dicta sunt, " +
                "explicabo. Nemo enim ipsam voluptatem, " +
                "quia voluptas sit, aspernatur aut odit " +
                "aut fugit, sed quia consequuntur magni " +
                "dolores eos, qui ratione voluptatem sequi " +
                "nesciunt, neque porro quisquam est, qui " +
                "dolorem ipsum, quia dolor sit amet consectetur " +
                "adipisci velit, sed quia non-numquam eius modi " +
                "tempora incidunt, ut labore et dolore magnam " +
                "aliquam quaerat voluptatem. Ut enim ad minima " +
                "veniam, quis nostrum exercitationem ullam " +
                "corporis suscipit laboriosam, nisi ut aliquid " +
                "ex ea commodi consequatur? Quis autem vel eum " +
                "iure reprehenderit, qui in ea voluptate velit " +
                "esse, quam nihil molestiae consequatur, vel illum, " +
                "qui dolorem eum fugiat, quo voluptas nulla pariatur?",
                "Lorem ipsum dolor sit amet, " +
                "consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt " +
                "ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud " +
                "exercitation ullamco laboris nisi " +
                "ut aliquip ex ea commodo consequat. " +
                "Duis aute irure dolor in reprehenderit " +
                "in voluptate velit esse cillum dolore " +
                "eu fugiat nulla pariatur. Excepteur " +
                "sint occaecat cupidatat non proident, " +
                "sunt in culpa qui officia deserunt " +
                "mollit anim id est laborum."));
    }
}
