/**
 *    Copyright 2009-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * References a generic type.
 *
 * @param <T> the referenced type
 * @since 3.1.0
 * @author Simone Tripodi
 */

/**
 * 用来处理泛型
 * @param <T>
 */
public abstract class TypeReference<T> {

  // 泛型类中的实际类型
  private final Type rawType;

  protected TypeReference() {
    //grammar.basic.在无参构造函数中为类属性赋值，这样只要对象创建这个属性立马就有值了
    //grammar.basic.子类实例化导致的父类无参方法被调用时，getClass返回的是子类的类型
    rawType = getSuperclassTypeParameter(getClass());
  }

  /**
   * 解析出当前TypeHandler实现类能够处理的目标类型
   * @param clazz TypeHandler实现类
   * @return 该TypeHandler实现类能够处理的目标类型
   */
  Type getSuperclassTypeParameter(Class<?> clazz) {
    // 获取clazz类的带有泛型的直接父类
    //Class和ParameterizedTypeImpl都实现了Type接口，这里的type其实是一个ParameterizedTypeImpl
    //tbr.为啥这里返回的type是ParameterizedTypeImpl呢，因为返回的对象本来就可以是Type的子类
    //grammar.reflect.这个方法返回的是类的带范型参数的父类
    Type genericSuperclass = clazz.getGenericSuperclass();
    //grammar.reflect.原来上面的get方法取到的父类，如果是不带范型的类，这个类的类型就是Class，带范型的类的类型就是Parameterized,这个反射机制记住
    //这个判断条件其实就是在判断获取的父类是不是带范型的，不是带范型的就再找父类的父类直到找到带范型的那个
    if (genericSuperclass instanceof Class) { //tbr.什么时候会是class类呢，不是应该都是Parameterized吗：TypeReferenceDemo里有例子
      //如果是类且不是TypeReference说明还得找哪里把范型给实例化了，就继续往上找
      if (TypeReference.class != genericSuperclass) { // genericSuperclass不是TypeReference类本身
        return getSuperclassTypeParameter(clazz.getSuperclass());
      }
      // 说明clazz实现了TypeReference类，但是却没有使用泛型
      throw new TypeException("'" + getClass() + "' extends TypeReference but misses the type parameter. "
        + "Remove the extension or add a type parameter to it.");
    }

    // 运行到这里说明genericSuperclass是泛型类。获取泛型的第一个参数，即T
    Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
    if (rawType instanceof ParameterizedType) { // 如果是参数化类型
      // 获取参数化类型的实际类型
      rawType = ((ParameterizedType) rawType).getRawType();
    }
    return rawType;
  }

  public final Type getRawType() {
    return rawType;
  }

  @Override
  public String toString() {
    return rawType.toString();
  }

}
