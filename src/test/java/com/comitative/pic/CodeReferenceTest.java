package com.comitative.pic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeReferenceTest {

    @Test
    void defaultMethodType_shouldBeJavaMethod() {
        CodeReference ref = CodeReference.builder()
                .setQualifiedClassName("MethodReferenceTest")
                .setMethodName("defaultMethodType_shouldBeJavaMethod")
                .build();
        assertTrue(ref.isJavaMethod());
    }

    @Test
    void defaultFqClassName_shouldBeEmpty() {
        CodeReference ref = CodeReference.builder()
                .setMethodName("defaultClassName_shouldBeEmpty")
                .build();
        assertTrue(ref.getQualifiedClassName().isEmpty());
    }

    @Test
    void methodReference_shouldBeEqualToItself() {
        CodeReference ref = CodeReference.builder()
                .setQualifiedClassName("com.comitative.Matrix")
                .setMethodName("multiply")
                .build();
        assertEquals(ref, ref);
    }

    @Test
    void methodReferencesWithSameAttributes_shouldBeEqual() {
        CodeReference r1 = CodeReference.builder()
                .setQualifiedClassName("com.comitative.Matrix")
                .setMethodName("multiply")
                .build();

        CodeReference r2 = CodeReference.builder()
                .setMethodName("multiply")
                .setQualifiedClassName("com.comitative.Matrix")
                .build();

        assertEquals(r1, r2);
    }

    @Test
    void methodType_shouldAffectEquality() {
        CodeReference r1 = CodeReference.builder()
                .setQualifiedClassName("Matrix")
                .setMethodName("multiply")
                .build();

        CodeReference r2 = CodeReference.builder()
                .setJavaMethod(false)
                .setMethodName("multiply")
                .setQualifiedClassName("Matrix")
                .build();

        assertNotEquals(r1, r2);
    }

    @Test
    void whenJavaMethod_toStringShouldEndWithJava() {
        CodeReference ref = CodeReference.builder()
                .setQualifiedClassName("Matrix")
                .setMethodName("multiply")
                .build();

        assertTrue(ref.toString().endsWith(" <java>"));
    }

    @Test
    void whenNotJavaMethod_toStringShouldEndWithNative() {
        CodeReference ref = CodeReference.builder()
                .setJavaMethod(false)
                .setMethodName("pthread_create")
                .build();

        assertTrue(ref.toString().endsWith(" <native>"));
    }

}