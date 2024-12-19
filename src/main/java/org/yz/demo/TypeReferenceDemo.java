package org.yz.demo;

import org.apache.ibatis.type.IntegerTypeHandler;
import org.apache.ibatis.type.TypeReference;

public class TypeReferenceDemo {

    public static void main(String[] args) {
        MyTypeReference reference = new MyTypeReference();
        System.out.println(reference.getRawType());
    }

    static class MyTypeReference extends IntegerTypeHandler {

    }
}
