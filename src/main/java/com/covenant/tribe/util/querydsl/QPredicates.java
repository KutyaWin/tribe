package com.covenant.tribe.util.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QPredicates {

    private final List<Predicate> predicates = new ArrayList<>();

    public static QPredicates builder() {
        return new QPredicates();
    }

    public <T> QPredicates add(T object, Function<T, Predicate> function) {
        if (object != null) {
            predicates.add(function.apply(object));
        }
        return this;
    }

    public <T> void add(T object, Predicate predicate) {
        if (object != null) {
            predicates.add(predicate);
        }
    }

    public Predicate build() {
        return Optional.ofNullable(ExpressionUtils.allOf(predicates))
                .orElseGet(() -> Expressions.asBoolean(true).isTrue());
    }

    public Predicate buildOr() {
        return Optional.ofNullable(ExpressionUtils.anyOf(predicates))
                .orElseGet(() -> Expressions.asBoolean(true).isTrue());
    }
}
