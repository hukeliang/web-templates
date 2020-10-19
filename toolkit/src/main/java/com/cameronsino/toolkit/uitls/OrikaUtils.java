package com.cameronsino.toolkit.uitls;

import com.cameronsino.toolkit.orika.OrikaCache;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

import java.util.*;
import java.util.function.Consumer;

public class OrikaUtils extends OrikaCache {
    /**
     * 映射实体（默认字段）
     *
     * @param data 数据（对象）
     * @param to   映射类对象
     */
    public static <TData, TResult> TResult map(TData data, Class<TResult> to) {
        if (Objects.isNull(data)) {
            return null;
        }
        return DEFAULT_MAPPER_FACADE.map(data, to);
    }

    /**
     * 映射实体（自定义配置）
     *
     * @param data   数据（对象）
     * @param to     映射类对象
     * @param config 自定义配置
     */
    public static <TData, TResult> TResult map(TData data, Class<TResult> to, Consumer<ClassMapBuilder> config) {
        return mapCustomize(data.getClass(), to, null, config);
    }

    /**
     * @param data      数据（对象）
     * @param to        映射类对象
     * @param converter 转换器
     */
    public static <TData, TResult> TResult mapCustomize(TData data, Class<TResult> to, Consumer<ConverterFactory> converter) {
        return mapCustomize(data.getClass(), to, converter, null);
    }

    /**
     * @param data      数据（对象）
     * @param to        映射类对象
     * @param config    自定义配置
     * @param converter 转换器
     */
    public static <TData, TResult> TResult mapCustomize(TData data, Class<TResult> to, Consumer<ConverterFactory> converter, Consumer<ClassMapBuilder> config) {
        if (Objects.isNull(data)) {
            return null;
        }
        MapperFacade mapperFacade = OrikaCache.getMapperFacade(data.getClass(), to, converter, config);
        return mapperFacade.map(data, to);
    }

    /**
     * 映射集合（默认字段）
     *
     * @param data 数据（集合）
     * @param to   映射类对象
     */
    public static <TData, TResult> List<TResult> mapAsList(Collection<TData> data, Class<TResult> to) {
        if (Objects.isNull(data) || data.isEmpty()) {
            return null;
        }
        return OrikaCache.DEFAULT_MAPPER_FACADE.mapAsList(data, to);
    }

    /**
     * 映射集合（自定义配置）
     *
     * @param data   数据（集合）
     * @param to     映射类对象
     * @param config 自定义配置
     */
    public static <TData, TResult> List<TResult> mapAsList(Collection<TData> data, Class<TResult> to, Consumer<ClassMapBuilder> config) {
        return mapAsListCustomize(data, to, null, config);
    }

    /**
     * @param data      数据（对象）
     * @param to        映射类对象
     * @param converter 转换器
     */
    public static <TData, TResult> List<TResult> mapAsListCustomize(Collection<TData> data, Class<TResult> to, Consumer<ConverterFactory> converter) {
        return mapAsListCustomize(data, to, converter, null);
    }

    /**
     * @param data      数据（对象）
     * @param to        映射类对象
     * @param config    自定义配置
     * @param converter 转换器
     */
    public static <TData, TResult> List<TResult> mapAsListCustomize(Collection<TData> data, Class<TResult> to, Consumer<ConverterFactory> converter, Consumer<ClassMapBuilder> config) {
        if (Objects.isNull(data) || data.isEmpty()) {
            return null;
        }
        TData first = data.iterator().next();
        MapperFacade mapperFacade = OrikaCache.getMapperFacade(first.getClass(), to, converter, config);
        return mapperFacade.mapAsList(data, to);
    }
}
