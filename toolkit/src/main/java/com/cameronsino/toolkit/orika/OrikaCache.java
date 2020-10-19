package com.cameronsino.toolkit.orika;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 缓存类
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Component
public class OrikaCache {
    @Autowired(required = false)
    private List<OrikaDefaultConverter> _orikaDefaultConverters;

    @PostConstruct
    private void initialization() {
        ORIKA_DEFAULT_CONVERTERS = this._orikaDefaultConverters;
    }
    /**
     * 转换器
     */
    private static List<OrikaDefaultConverter> ORIKA_DEFAULT_CONVERTERS;
    /**
     * 缓存
     */
    private static final MapperFactory DEFAULT_MAPPER_FACTORY = new DefaultMapperFactory.Builder().build();
    private static final Map<String, MapperFacade> MAPPER_FACADE_CACHE_MAP = new ConcurrentHashMap<>(100);
    protected static final MapperFacade DEFAULT_MAPPER_FACADE = DEFAULT_MAPPER_FACTORY.getMapperFacade();

    /**
     * 获取 MapperFacade 对象没有就创建
     *
     * @param from   class类型
     * @param to     class类型
     * @param config 自定义配置
     */
    protected static <TData, TResult> MapperFacade getMapperFacade(Class<TData> from, Class<TResult> to, Consumer<ClassMapBuilder> config) {
        return getMapperFacade(from, to, null, config);
    }

    /**
     * 获取 MapperFacade 对象没有就创建
     *
     * @param from      class类型
     * @param to        class类型
     * @param converter 转换器
     * @param config    自定义配置
     */
    protected static <TData, TResult> MapperFacade getMapperFacade(Class<TData> from, Class<TResult> to, Consumer<ConverterFactory> converter, Consumer<ClassMapBuilder> config) {
        String key = String.format("%s_%s", from.getCanonicalName(), to.getCanonicalName());
        MapperFacade mapperFacade = MAPPER_FACADE_CACHE_MAP.get(key);
        if (Objects.nonNull(mapperFacade)) {
            return mapperFacade;
        }
        /*
         *自定义配置可以配置字段
         * .field("A","toB")
         * .field("A.a","toB")
         * .field("A[0]","toB")
         * .field("A['mapKey']","toB")
         *
         * 字段不匹配映射
         * .exclude()
         * null不映射
         * .mapNulls()
         * 自定义映射转换
         * .customize
         */
        MapperFactory factory = new DefaultMapperFactory.Builder().build();
        ConverterFactory converterFactory = factory.getConverterFactory();
        registerDefaultConverter(converterFactory);
        if (Objects.nonNull(converter)) {
            converter.accept(converterFactory);
        }

        ClassMapBuilder<?, ?> mapBuilder = factory.classMap(from, to);
        if (Objects.nonNull(config)) {
            config.accept(mapBuilder);
        }
        mapBuilder.byDefault().register();
        mapperFacade = factory.getMapperFacade();
        try {
            return mapperFacade;
        } finally {
            //添加当前配置到线程安全Map中缓存
            MAPPER_FACADE_CACHE_MAP.put(key, mapperFacade);
        }
    }

    /**
     * 注册全局  默认转换器
     *
     * @param converterFactory ConverterFactory
     */
    private static void registerDefaultConverter(ConverterFactory converterFactory) {
        if (Objects.nonNull(ORIKA_DEFAULT_CONVERTERS)) {
            List<Converter<?, ?>> converters = new ArrayList<>();
            for (OrikaDefaultConverter instance : ORIKA_DEFAULT_CONVERTERS) {
                instance.registerConverter(converters);
            }
            for (Converter<?, ?> converter : converters) {
                converterFactory.registerConverter(converter);
            }
        }
    }

}
