// Copyright (c) 2019 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.javaapi.data;

import com.digitalasset.ledger.api.v1.ValueOuterClass;

import java.util.Objects;
import java.util.Optional;

public final class Variant extends Value {

    private final Optional<Identifier> variantId;

    private final String constructor;

    private final Value value;

    public Variant(Identifier variantId, String constructor, Value value) {
        this.variantId = Optional.of(variantId);
        this.constructor = constructor;
        this.value = value;
    }

    public Variant(String constructor, Value value) {
        this.variantId = Optional.empty();
        this.constructor = constructor;
        this.value = value;
    }

    public static Variant fromProto(ValueOuterClass.Variant variant) {
        String constructor = variant.getConstructor();
        Value value = Value.fromProto(variant.getValue());
        if (variant.hasVariantId()) {
            Identifier variantId = Identifier.fromProto(variant.getVariantId());
            return new Variant(variantId, constructor, value);
        } else {
            return new Variant(constructor, value);
        }
    }

    public Optional<Identifier> getVariantId() {
        return variantId;
    }

    public String getConstructor() {
        return constructor;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public ValueOuterClass.Value toProto() {
        return ValueOuterClass.Value.newBuilder().setVariant(this.toProtoVariant()).build();
    }

    public ValueOuterClass.Variant toProtoVariant() {
        ValueOuterClass.Variant.Builder builder = ValueOuterClass.Variant.newBuilder();
        builder.setConstructor(this.getConstructor());
        this.getVariantId().ifPresent(identifier -> builder.setVariantId(identifier.toProto()));
        builder.setValue(this.value.toProto());
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variant variant = (Variant) o;
        return Objects.equals(variantId, variant.variantId) &&
                Objects.equals(constructor, variant.constructor) &&
                Objects.equals(value, variant.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(variantId, constructor, value);
    }

    @Override
    public String toString() {
        return "Variant{" +
                "variantId=" + variantId +
                ", constructor='" + constructor + '\'' +
                ", value=" + value +
                '}';
    }
}
