package com.comitative.pic.parsers;

import com.comitative.pic.CodeReference;

interface NameResolver {
    CodeReference resolve(String name);
}
