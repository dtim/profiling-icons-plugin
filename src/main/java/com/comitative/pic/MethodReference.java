package com.comitative.pic;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MethodReference {

    private final boolean javaMethod;
    private final @NotNull String packageName;
    private final @NotNull String className;
    private final @NotNull String methodName;

    private MethodReference(
            boolean javaMethod,
            @NotNull String packageName,
            @NotNull String className,
            @NotNull String methodName) {
        this.javaMethod = javaMethod;
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private static final String EMPTY_NAME = "";

        private boolean javaMethod = true;
        private String packageName = EMPTY_NAME;
        private String className = EMPTY_NAME;
        private String methodName;

        // TODO: we may allow users to directly construct builder instances: new MethodReference.Builder()
        // TODO: do we gain anything by making a constructor private?
        private Builder() {}

        public MethodReference build() {
            if (methodName == null) {
                throw new IllegalArgumentException("method name can't be null");
            }

            return new MethodReference(javaMethod, packageName, className, methodName);
        }

        public Builder setJavaMethod(boolean javaMethod) {
            this.javaMethod = javaMethod;
            return this;
        }

        public Builder setPackageName(@NotNull String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder setClassName(@NotNull String className) {
            this.className = className;
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

    public @NotNull String getPackageName() {
        return packageName;
    }

    public @NotNull String getClassName() {
        return className;
    }

    public @NotNull String getMethodName() {
        return methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodReference that = (MethodReference) o;
        return javaMethod == that.javaMethod
                && packageName.equals(that.packageName)
                && className.equals(that.className)
                && methodName.equals(that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(javaMethod, packageName, className, methodName);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        if (!packageName.isEmpty()) {
            sb.append("[").append(packageName).append("] ");
        }

        sb.append(className).append("::").append(methodName);
        if (javaMethod) {
            sb.append(" <java>");
        } else {
            sb.append(" <native>");
        }
        return sb.toString();
    }

}
