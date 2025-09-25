package org.framegen.util;

/**
 * 一个方法类型
 * 用于解决直接使用 Function<String, Void> 时无法传入传统setter方法的问题
 */
@FunctionalInterface
public interface StringSetter {
    void set(String value);
}
