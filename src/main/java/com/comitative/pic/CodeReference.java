package com.comitative.pic;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An immutable reference to the code fragment.
 *
 * The code fragment is referenced by its class and method names.
 * It is possible to refer to nested functions, methods of anonymous classes, lambda functions and so on
 * using specially constructed class or method names.
 *
 * In the current version of the plugin, code fragments only refer to Java/Kotlin methods,
 * but in principle, native (C/C++) names may be represented as well (although there may be no way
 * to mark them with icons).
 *
 * In the future, other attributes are scheduled to be added, e.g., the method signatures.
 *
 * The principal problem with code references is the matching of names between profiler snapshots and the code.
 * Users should follow the convention:
 * <ul>
 *   <li>Names for "ordinary" (non-nested, non-anonymous) classes and methods should be stored as they are represented
 *     in the snapshot:
 *     {@code "com.comitative.pic.CodeReference.getMethodName" -> ("com.comitative.pic.CodeReference", "getMethodName")}
 *   </li>
 *   <li>Constructor names represented as "&lt;init&gt;" should be kept as "&lt;init&gt;":
 *     {@code "com.comitative.pic.CodeReference.<init>" -> ("com.comitative.pic.CodeReference", "<init>")
 *   </li>
 *   <li>If a Java constructor is referred with the class name, you may keep it as is or replace with "&lt;init&gt;":
 *     {@code "MyClass.MyClass" -> ("MyClass", "MyClass")}
 *     {@code "MyClass.MyClass" -> ("MyClass", "<init>")}
 *   </li>
 *   <li>Dollar markers for the nested classes should be replaced with a dot:
 *     {@code "MyClass$MyInnerClass.myMethod" -> ("MyClass.MyInnerClass", "myMethod")}
 *   </li>
 * </ul>
 */
public final class CodeReference {

    private final boolean javaMethod;
    private final @NotNull String shortClassName;
    private final @NotNull String fqClassName;
    private final @NotNull String methodName;

    /**
     * A private constructor with the mandatory components of the name as arguments.
     * @param javaMethod true if the name refers to the user/library Java/Kotlin/(other JVM language) code, false if
     *                   it is a reference to a native function (e.g., a component of the JVM itself).
     * @param fqClassName fully qualified class name (e.g., "com.comitative.pic.CodeReference")
     * @param methodName  the method name or designator (e.g., "getMethodName")
     */
    private CodeReference(
            boolean javaMethod,
            @NotNull String fqClassName,
            @NotNull String methodName) {
        this.javaMethod = javaMethod;
        this.fqClassName = fqClassName;
        this.methodName = methodName;

        String[] components = fqClassName.split("\\.");
        if (components.length > 0) {
            this.shortClassName = components[components.length - 1];
        } else {
            this.shortClassName = fqClassName;
        }
    }

    /**
     * Get a builder for incremental construction of a code reference.
     *
     * @return a code reference builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A code reference builder class.
     * It should be used to incrementally construct a code reference in a parser or a name matcher.
     */
    public static final class Builder {
        private static final String EMPTY_NAME = "";

        private boolean javaMethod = true;
        private String fqClassName = EMPTY_NAME;
        private String methodName;

        private Builder() {}

        /**
         * Check the arguments and create a code reference.
         *
         * @return a code reference with the accumulated arguments.
         * @throws IllegalArgumentException when required data is missing or the reference is incorrect.
         */
        public CodeReference build() {
            if (methodName == null) {
                throw new IllegalArgumentException("method name can't be null");
            }

            return new CodeReference(javaMethod, fqClassName, methodName);
        }

        /**
         * Set the java/native method attribute.
         *
         * @param javaMethod true for Java/Kotlin names, false for native code names
         * @return the builder instance for chaining calls
         */
        public Builder setJavaMethod(boolean javaMethod) {
            this.javaMethod = javaMethod;
            return this;
        }

        /**
         * Sets the fully qualified name of a class.
         *
         * @param className the fully qualified class name (may be empty but not null)
         * @return the builder instance for chaining calls
         */
        public Builder setFqClassName(@NotNull String className) {
            this.fqClassName = className;
            return this;
        }

        /**
         * Sets the name of a method.
         *
         * @param methodName a method name.
         * @return the builder instance for chaining calls
         */
        public Builder setMethodName(@NotNull String methodName) {
            this.methodName = methodName;
            return this;
        }
    }

    /**
     * A getter for the java/native attribute
     * @return true for Java/Kotlin methods, false for native methods
     */
    public boolean isJavaMethod() {
        return javaMethod;
    }

    /**
     * A getter for the fully qualified class name.
     * @return a fully qualified class name
     */
    public @NotNull String getFqClassName() {
        return fqClassName;
    }

    /**
     * A getter for the short (immediate) class name.
     * @return short class name
     */
    public @NotNull String getShortClassName() {
        return shortClassName;
    }

    /**
     * A getter for the method name.
     * @return method name
     */
    public @NotNull String getMethodName() {
        return methodName;
    }

    /**
     * Equality predicate.
     *
     * @param o another object to compare.
     * @return true if two code references correspond to the same fragment, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeReference that = (CodeReference) o;
        return javaMethod == that.javaMethod
                && fqClassName.equals(that.fqClassName)
                && methodName.equals(that.methodName);
    }

    /**
     * Hash code function.
     * @return a hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(javaMethod, fqClassName, methodName);
    }

    /**
     * Human-readable string representation of the code reference.
     * This representation will probably change in the future, but currently it has the format
     * of "fully.qualified.class::method &lt;java&gt;" or "fully.qualified.class::method &lt;native&gt;"
     *
     * @return the string representation of the code reference
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(fqClassName).append("::").append(methodName);
        if (javaMethod) {
            sb.append(" <java>");
        } else {
            sb.append(" <native>");
        }
        return sb.toString();
    }

}
