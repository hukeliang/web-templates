package com.cameronsino.toolkit.orika;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

//@Component
public class DefaultConverterImpl implements OrikaDefaultConverter  {
    /**
     * 注册默认Orika 转换器接口
     *
     * @param converters converters
     */
    @Override
    public void registerConverter(List<Converter<?, ?>> converters) {
        converters.add(new BidirectionalConverter<String, Integer>() {
            @Override
            public Integer convertTo(String source, Type<Integer> destinationType, MappingContext mappingContext) {
                return Integer.valueOf(source);
            }

            @Override
            public String convertFrom(Integer source, Type<String> destinationType, MappingContext mappingContext) {
                return String.valueOf(source);
            }
        });
        converters.add(new BidirectionalConverter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convertTo(String source, Type<LocalDateTime> destinationType, MappingContext mappingContext) {
                return LocalDateTime.parse(source);
            }

            @Override
            public String convertFrom(LocalDateTime source, Type<String> destinationType, MappingContext mappingContext) {
                return source.toString();
            }
        });
        converters.add(new SimpleConverter<String, String>() {
            @Override
            public String leftToRight(String source) {
                return null;
            }

            @Override
            public String rightToLeft(String source) {
                return null;
            }
        });
    }
}
