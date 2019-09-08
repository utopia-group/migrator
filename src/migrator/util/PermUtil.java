package migrator.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PermUtil {

    /**
     * Compute the power set excluding empty set for a given list.
     *
     * @param list the given list
     * @return power set as list of lists
     */
    public static <T> List<List<T>> nonEmptyPowerSet(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Empty input for power set");
        } else if (list.size() > 30) {
            throw new IllegalArgumentException("The list is too large for power set");
        }
        return nonEmptyPowerSetImpl(list, list.size() - 1);
    }

    private static <T> List<List<T>> nonEmptyPowerSetImpl(List<T> list, int index) {
        List<List<T>> ret = new ArrayList<>();
        T element = list.get(index);
        ret.add(Collections.singletonList(element));
        if (index > 0) {
            List<List<T>> currPowerSet = nonEmptyPowerSetImpl(list, index - 1);
            ret.addAll(currPowerSet);
            for (List<T> set : currPowerSet) {
                List<T> newSet = new ArrayList<>(set);
                newSet.add(element);
                ret.add(newSet);
            }
        }
        return ret;
    }

}
