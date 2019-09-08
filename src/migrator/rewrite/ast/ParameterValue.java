package migrator.rewrite.ast;

/**
 * A value representing the value of a parameter.
 */
public final class ParameterValue implements IValue {
    /**
     * The name of the parameter.
     */
    public final String name;

    /**
     * Constructs a value representing the given parameter by name.
     *
     * @param name the name of the parameter
     */
    public ParameterValue(String name) {
        this.name = name;
    }

    @Override
    public <T> T accept(IValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSqlString() {
        return "<" + name + ">";
    }

    @Override
    public String toString() {
        return String.format("ParameterValue(%s)", name);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ParameterValue))
            return false;
        ParameterValue other = (ParameterValue) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
