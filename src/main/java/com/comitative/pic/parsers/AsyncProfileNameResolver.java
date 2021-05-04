package com.comitative.pic.parsers;

import com.comitative.pic.CodeReference;

public class AsyncProfileNameResolver implements NameResolver {
    @Override
    public CodeReference resolve(String name) {
        return CodeReference
                .createBuilder()
                .setPackageName("com.comitative.pt")
                .setClassName("Matrix")
                .setMethodName("multiply")
                .build();
    }
}
