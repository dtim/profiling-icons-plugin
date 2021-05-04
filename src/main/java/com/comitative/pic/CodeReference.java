package com.comitative.pic;

import java.util.Objects;

// FIXME: argument validation in builder
// FIXME: null/not null reference components?
// FIXME: system/compiled

// TODO: what data about the code reference clients will need to know?
// TODO: do we need references to packages or to classes
// TODO: (e.g., to be able to get all stat entries for a file/class)?
// TODO: it seems useful (clients can ask for CodeReference("com.comitative.pt", "MainKt", null))
// TODO: but it may create confusion between code references and query patterns.
// TODO: Do we need to reference arbitrary code fragments like methods, classes, files, packages?
// TODO: Or we will always reference canonical pieces of code (e.g., methods), and use query patterns
// TODO: to select groups of these pieces?
// TODO: Most difficult case: a JVM method that is really not a method but a lambda expression (part of a method).
// TODO: In this case we need a way to make a reference inside a method, and we still have to reference methods
// TODO: without these details.

public final class CodeReference {

    private final boolean system;
    private final boolean compiled;

    private final String packageName;
    private final String className;
    private final String methodName;

    private CodeReference(
            String packageName,
            String className,
            String methodName) {
        this.system = false;
        this.compiled = false;
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
    }

    public static final class Builder {
        private String packageName;
        private String className;
        private String methodName;

        private Builder() {}

        public Builder setPackageName(String name) {
            this.packageName = name;
            return this;
        }

        public Builder setClassName(String name) {
            this.className = name;
            return this;
        }

        public Builder setMethodName(String name) {
            this.methodName = name;
            return this;
        }

        public CodeReference build() {
            return new CodeReference(packageName, className, methodName);
        }
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeReference that = (CodeReference) o;
        return Objects.equals(packageName, that.packageName)
                && Objects.equals(className, that.className)
                && Objects.equals(methodName, that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, className, methodName);
    }

    // FIXME: need a more concise representation?
    @Override
    public String toString() {
        return "CodeReference{" +
                "system=" + system +
                ", compiled=" + compiled +
                ", packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
