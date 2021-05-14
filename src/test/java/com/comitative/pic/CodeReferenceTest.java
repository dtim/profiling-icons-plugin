package com.comitative.pic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeReferenceTest {

    @Test
    void defaultMethodType_shouldBeJavaMethod() {
        CodeReference ref = CodeReference.builder()
                .setPackageName("com.comitative.pic")
                .setClassName("MethodReferenceTest")
                .setMethodName("defaultMethodType_shouldBeJavaMethod")
                .build();
        assertTrue(ref.isJavaMethod());
    }

    @Test
    void defaultPackageName_shouldBeEmpty() {
        CodeReference ref = CodeReference.builder()
                .setMethodName("defaultPackageName_shouldBeEmpty")
                .build();
        assertTrue(ref.getPackageName().isEmpty());
    }

    @Test
    void defaultClassName_shouldBeEmpty() {
        CodeReference ref = CodeReference.builder()
                .setMethodName("defaultClassName_shouldBeEmpty")
                .build();
        assertTrue(ref.getClassName().isEmpty());
    }

    @Test
    void methodReference_shouldBeEqualToItself() {
        CodeReference ref = CodeReference.builder()
                .setPackageName("com.comitative")
                .setClassName("Matrix")
                .setMethodName("multiply")
                .build();
        assertEquals(ref, ref);
    }

    @Test
    void methodReferencesWithSameAttributes_shouldBeEqual() {
        CodeReference r1 = CodeReference.builder()
                .setPackageName("com.comitative")
                .setClassName("Matrix")
                .setMethodName("multiply")
                .build();

        CodeReference r2 = CodeReference.builder()
                .setMethodName("multiply")
                .setClassName("Matrix")
                .setPackageName("com.comitative")
                .build();

        assertEquals(r1, r2);
    }

    @Test
    void methodType_shouldAffectEquality() {
        CodeReference r1 = CodeReference.builder()
                .setPackageName("com.comitative")
                .setClassName("Matrix")
                .setMethodName("multiply")
                .build();

        CodeReference r2 = CodeReference.builder()
                .setJavaMethod(false)
                .setMethodName("multiply")
                .setClassName("Matrix")
                .setPackageName("com.comitative")
                .build();

        assertNotEquals(r1, r2);
    }

    @Test
    void whenPackageNameIsPresent_toStringShouldStartWithPackageName() {
        CodeReference ref = CodeReference.builder()
                .setPackageName("com.comitative")
                .setClassName("Matrix")
                .setMethodName("multiply")
                .build();

        String repr = ref.toString();
        assertTrue(repr.startsWith("[com.comitative] "));
    }

    @Test
    void whenJavaMethod_toStringShouldEndWithJava() {
        CodeReference ref = CodeReference.builder()
                .setPackageName("com.comitative")
                .setClassName("Matrix")
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