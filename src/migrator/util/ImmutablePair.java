package migrator.util;

import java.util.Objects;

/**
 * A generic data structure for immutable pairs.
 *
 * @param <T1> type for the first element
 * @param <T2> type for the second element
 */
public class ImmutablePair<T1, T2> {
    public final T1 first;
    public final T2 second;

    public ImmutablePair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof ImmutablePair) {
            @SuppressWarnings("rawtypes")
            ImmutablePair other = (ImmutablePair) obj;
            return Objects.equals(first, other.first) && Objects.equals(second, other.second);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", first.toString(), second.toString());
    }
}
