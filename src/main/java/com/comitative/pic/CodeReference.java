package com.comitative.pic;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class CodeReference {

    private final boolean javaMethod;
    private final @NotNull String shortClassName;
    private final @NotNull String fqClassName;
    private final @NotNull String methodName;

    private CodeReference(
            boolean javaMethod,
            @NotNull String fqClassName,
            @NotNull String methodName) {
        this.javaMethod = javaMethod;
        this.fqClassName = fqClassName;
        this.methodName = methodName;

        String[] components = fqClassName.split("\\.");
        this.shortClassName = components[components.length - 1];
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private static final String EMPTY_NAME = "";

        private boolean javaMethod = true;
        private String fqClassName = EMPTY_NAME;
        private String methodName;

        private Builder() {}

        public CodeReference build() {
            if (methodName == null) {
                throw new IllegalArgumentException("method name can't be null");
            }

            return new CodeReference(javaMethod, fqClassName, methodName);
        }

        public Builder setJavaMethod(boolean javaMethod) {
            this.javaMethod = javaMethod;
            return this;
        }

        public Builder setFqClassName(@NotNull String className) {
            this.fqClassName = className;
            return this;
        }

        public Builder setMethodName(@NotNull String methodName) {
            this.methodName = methodName;
            return this;
        }
    }

    public boolean isJavaMethod() {
        return javaMethod;
    }

    public @NotNull String getFqClassName() {
        return fqClassName;
    }

    public @NotNull String getShortClassName() {
        return shortClassName;
    }

    public @NotNull String getMethodName() {
        return methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeReference that = (CodeReference) o;
        return javaMethod == that.javaMethod
                && fqClassName.equals(that.fqClassName)
                && methodName.equals(that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(javaMethod, fqClassName, methodName);
    }

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
