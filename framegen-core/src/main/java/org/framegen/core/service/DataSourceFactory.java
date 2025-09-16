package org.framegen.core.service;

@FunctionalInterface
public interface DataSourceFactory <T> {
    T getDataSource();
}
