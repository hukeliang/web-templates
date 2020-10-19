package com.cameronsino.toolkit.function;

@FunctionalInterface
public interface ExceptionRunnable<T extends Exception> {

    void run() throws T;
}
