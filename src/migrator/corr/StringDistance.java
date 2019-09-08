package migrator.corr;

public class StringDistance {

    /**
     * Computes the Levenshtein distance between the two given strings. The
     * Levenshtein distance is the minimum number of insertions, deletions, and
     * substitutions necessary to transform one string into another.
     *
     * @param first  the first string to compare
     * @param second the second string to compare
     * @return the Levenshtein distance between the two strings
     */
    public static int levenshteinDistance(String first, String second) {
        if (first.length() < second.length()) {
            return levenshteinDistance(second, first);
        }
        // only need to store two rows
        // holds distance from first[..i] to second[..j]
        int[] distance = new int[second.length() + 1];
        for (int j = 0; j <= second.length(); j++) {
            distance[j] = j;
        }
        int[] workingDistance = new int[second.length() + 1];
        for (int i = 1; i <= first.length(); i++) {
            workingDistance[0] = i;
            for (int j = 1; j <= second.length(); j++) {
                int deleteCost = distance[j] + 1;
                int insertCost = workingDistance[j - 1] + 1;
                int substituteCost = distance[j - 1];
                if (first.charAt(i - 1) != second.charAt(j - 1)) {
                    substituteCost++;
                }
                workingDistance[j] = Math.min(Math.min(deleteCost, insertCost), substituteCost);
            }
            // move workingDistance into distance
            // reuse old distance for next workingDistance
            int[] temp = distance;
            distance = workingDistance;
            workingDistance = temp;
        }
        return distance[second.length()];
    }

}
