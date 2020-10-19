package com.cameronsino.toolkit.orika;

import ma.glasnost.orika.Converter;

import java.util.List;

public interface OrikaDefaultConverter {
    /**
     * 注册默认Orika 转换器接口
     *
     * @param converters converters
     */
    void registerConverter(List<Converter<?, ?>> converters);
}
