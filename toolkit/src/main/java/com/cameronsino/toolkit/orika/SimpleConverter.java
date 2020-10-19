package com.cameronsino.toolkit.orika;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public abstract class SimpleConverter<A, B> extends BidirectionalConverter<A, B> {

    public abstract B leftToRight(A source);

    public abstract A rightToLeft(B source);

    @Override
    public final B convertTo(A source, Type<B> destinationType, MappingContext mappingContext) {
        return leftToRight(source);
    }

    @Override
    public final A convertFrom(B source, Type<A> destinationType, MappingContext mappingContext) {
        return rightToLeft(source);
    }
}
