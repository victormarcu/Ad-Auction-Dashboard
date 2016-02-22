/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.koloboke.collect.impl.hash;

import net.openhft.koloboke.collect.Equivalence;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


final class ImmutableQHashSeparateKVObjDoubleMap<K>
        extends ImmutableQHashSeparateKVObjDoubleMapGO<K> {

    

    static final class WithCustomKeyEquivalence<K>
            extends ImmutableQHashSeparateKVObjDoubleMapGO<K> {
        Equivalence<? super K> keyEquivalence;

        

        @Override
        @Nonnull
        public Equivalence<K> keyEquivalence() {
            // noinspection unchecked
            return (Equivalence<K>) keyEquivalence;
        }

        @Override
        boolean nullableKeyEquals(@Nullable K a, @Nullable K b) {
            return keyEquivalence.nullableEquivalent(a, b);
        }

        @Override
        boolean keyEquals(@Nonnull K a, @Nullable K b) {
            return b != null && keyEquivalence.equivalent(a, b);
        }

        @Override
        int nullableKeyHashCode(@Nullable K key) {
            return keyEquivalence.nullableHash(key);
        }

        @Override
        int keyHashCode(@Nonnull K key) {
            return keyEquivalence.hash(key);
        }
    }

    static final class WithCustomDefaultValue<K>
            extends ImmutableQHashSeparateKVObjDoubleMapGO<K> {
        double defaultValue;

        

        @Override
        public double defaultValue() {
            return defaultValue;
        }
    }


    static final class WithCustomKeyEquivalenceAndDefaultValue<K>
            extends ImmutableQHashSeparateKVObjDoubleMapGO<K> {
        Equivalence<? super K> keyEquivalence;
        double defaultValue;

        @Override
        @Nonnull
        public Equivalence<K> keyEquivalence() {
            // noinspection unchecked
            return (Equivalence<K>) keyEquivalence;
        }

        @Override
        boolean nullableKeyEquals(@Nullable K a, @Nullable K b) {
            return keyEquivalence.nullableEquivalent(a, b);
        }

        @Override
        boolean keyEquals(@Nonnull K a, @Nullable K b) {
            return b != null && keyEquivalence.equivalent(a, b);
        }

        @Override
        int nullableKeyHashCode(@Nullable K key) {
            return keyEquivalence.nullableHash(key);
        }

        @Override
        int keyHashCode(@Nonnull K key) {
            return keyEquivalence.hash(key);
        }

        @Override
        public double defaultValue() {
            return defaultValue;
        }
    }
}
