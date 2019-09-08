package migrator.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

public class PermUtilTest {

    @Test
    public void testNonEmptyPowerSet() {
        List<Integer> list = new ArrayList<>(Arrays.asList(new Integer[] { 1, 2, 3 }));
        List<List<Integer>> powerSet = PermUtil.nonEmptyPowerSet(list);
        List<List<Integer>> exptected = new ArrayList<>();
        exptected.add(Collections.singletonList(3));
        exptected.add(Collections.singletonList(2));
        exptected.add(Collections.singletonList(1));
        exptected.add(Stream.of(1, 2).collect(Collectors.toList()));
        exptected.add(Stream.of(2, 3).collect(Collectors.toList()));
        exptected.add(Stream.of(1, 3).collect(Collectors.toList()));
        exptected.add(Stream.of(1, 2, 3).collect(Collectors.toList()));
        Assert.assertEquals(exptected, powerSet);
    }

}
