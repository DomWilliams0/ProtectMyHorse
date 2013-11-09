package com.supaham.protectmyhorse.configuration;

/**
 * Represents a Property for a Yaml file.
 * 
 * @author SupaHam
 * 
 * @param <T> type of the value to retrieve
 */
@SuppressWarnings("rawtypes")
public class Property<T> implements Comparable<Property> {

    /**
     * Path of the property.
     */
    private final String path;
    /**
     * Name of the property.
     */
    private final String name;
    /**
     * Default value of the property.
     */
    private final T value;

    /**
     * Constructs a new property using a path and type.
     * 
     * @param path path to value
     * @param t default value
     */
    public Property(String path, T t) {

        if (path == null || t == null) {
            throw new IllegalArgumentException("path or t cannot be null.");
        }
        this.path = path;
        this.value = t;
        int index = path.lastIndexOf('.');
        if (index < 0)
            this.name = path;
        else
            this.name = path.substring(index + 1);
    }

    /**
     * Gets the path of the property.
     * 
     * @return path.to.value
     */
    public String getPath() {

        return path;
    }

    /**
     * Gets the name of the property.
     * 
     * @return the last index of '.' from the path
     */
    public String getName() {

        return name;
    }

    /**
     * Gets the default value of this property.
     * 
     * @return default value
     */
    public T getDefaultValue() {

        return value;
    }

    /**
     * Compares this property with another property.
     * 
     * @param prop property to compare with.
     */
    @Override
    public int compareTo(Property prop) {

        return getName().compareToIgnoreCase(prop.getName());
    }

    /**
     * Gets name of property.
     */
    @Override
    public String toString() {

        return getName();
    }
}
