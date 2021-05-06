package com.comitative.pic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MethodReferenceTest {

    @Test
    void defaultMethodType_shouldBeJavaMethod() {
        MethodReference ref = MethodReference.builder()
                .setPackageName("com.comitative.pic")
                .setClassName("MethodReferenceTest")
                .setMethodName("defaultMethodType_shouldBeJavaMethod")
                .build();
        assertTrue(ref.isJavaMethod());
    }

    @Test
    void defaultPackageName_shouldBeEmpty() {
        MethodReference ref = MethodReference.builder()
                .setMethodName("defaultPackageName_shouldBeEmpty")
                .build();
        assertTrue(ref.getPackageName().isEmpty());
    }

    @Test
    void defaultClassName_shouldBeEmpty() {
        MethodReference ref = MethodReference.builder()
                .setMethodName("defaultClassName_shouldBeEmpty")
                .build();
        assertTrue(ref.getClassName().isEmpty());
    }

    @Test
    void methodReference_shouldBeEqualToItself() {
        MethodReference ref = MethodReference.builder()
                .setPackageName("com.comitative")
                .setClassName("Matrix")
                .setMethodName("multiply")
                .build();
        assertEquals(ref, ref);
    }

    @Test
    void methodReferencesWithSameAttributes_shouldBeEqual() {
        MethodReference r1 = MethodReference.builder()
                .setPackageName("com.comitative")
                .setClassName("Matrix")
                .setMethodName("multiply")
                .build();

        MethodReference r2 = MethodReference.builder()
                .setMethodName("multiply")
                .setClassName("Matrix")
                .setPackageName("com.comitative")
                .build();

        assertEquals(r1, r2);
    }

    @Test
    void methodType_shouldAffectEquality() {
        MethodReference r1 = MethodReference.builder()
                .setPackageName("com.comitative")
                .setClassName("Matrix")
                .setMethodName("multiply")
                .build();

        MethodReference r2 = MethodReference.builder()
                .setJavaMethod(false)
                .setMethodName("multiply")
                .setClassName("Matrix")
                .setPackageName("com.comitative")
                .build();

        assertNotEquals(r1, r2);
    }

    @Test
    void whenPackageNameIsPresent_toStringShouldStartWithPackageName() {
        MethodReference ref = MethodReference.builder()
                .setPackageName("com.comitative")
                .setClassName("Matrix")
                .setMethodName("multiply")
                .build();

        String repr = ref.toString();
        assertTrue(repr.startsWith("[com.comitative] "));
    }

    @Test
    void whenJavaMethod_toStringShouldEndWithJava() {
        MethodReference ref = MethodReference.builder()
                .setPackageName("com.comitative")
                .setClassName("Matrix")
                .setMethodName("multiply")
                .build();

        assertTrue(ref.toString().endsWith(" <java>"));
    }

    @Test
    void whenNotJavaMethod_toStringShouldEndWithNative() {
        MethodReference ref = MethodReference.builder()
                .setJavaMethod(false)
                .setMethodName("pthread_create")
                .build();

        assertTrue(ref.toString().endsWith(" <native>"));
    }

}