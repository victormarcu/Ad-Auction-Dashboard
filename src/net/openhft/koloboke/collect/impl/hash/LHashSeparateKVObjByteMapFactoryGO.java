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

import net.openhft.koloboke.collect.*;
import net.openhft.koloboke.collect.hash.*;
import net.openhft.koloboke.collect.impl.*;
import net.openhft.koloboke.collect.map.hash.HashObjByteMapFactory;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.openhft.koloboke.collect.map.hash.HashObjByteMap;

import javax.annotation.Nonnull;
import java.util.*;

import static net.openhft.koloboke.collect.impl.Containers.sizeAsInt;
import static net.openhft.koloboke.collect.impl.hash.LHashCapacities.configIsSuitableForMutableLHash;


public abstract class LHashSeparateKVObjByteMapFactoryGO<K>
        extends LHashSeparateKVObjByteMapFactorySO<K> {

    LHashSeparateKVObjByteMapFactoryGO(HashConfig hashConf, int defaultExpectedSize
            , boolean isNullKeyAllowed
            ) {
        super(hashConf, defaultExpectedSize, isNullKeyAllowed
            );
    }

    

    abstract HashObjByteMapFactory<K> thisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            );

    abstract HashObjByteMapFactory<K> lHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            );

    abstract HashObjByteMapFactory<K> qHashLikeThisWith(HashConfig hashConf, int defaultExpectedSize, boolean isNullKeyAllowed
            );

    @Override
    public final HashObjByteMapFactory<K> withHashConfig(@Nonnull HashConfig hashConf) {
        if (configIsSuitableForMutableLHash(hashConf))
            return lHashLikeThisWith(hashConf, getDefaultExpectedSize()
            , isNullKeyAllowed());
        return qHashLikeThisWith(hashConf, getDefaultExpectedSize()
            , isNullKeyAllowed());
    }

    @Override
    public final HashObjByteMapFactory<K> withDefaultExpectedSize(int defaultExpectedSize) {
        if (defaultExpectedSize == getDefaultExpectedSize())
            return this;
        return thisWith(getHashConfig(), defaultExpectedSize
                , isNullKeyAllowed());
    }

    @Override
    public final HashObjByteMapFactory<K> withNullKeyAllowed(boolean nullKeyAllowed) {
        if (nullKeyAllowed == isNullKeyAllowed())
            return this;
        return thisWith(getHashConfig(), getDefaultExpectedSize(), nullKeyAllowed);
    }

    @Override
    public String toString() {
        return "HashObjByteMapFactory[" + commonString() + keySpecialString() +
                ",defaultValue=" + getDefaultValue() +
        "]";
    }

    @Override
    public int hashCode() {
        int hashCode = keySpecialHashCode(commonHashCode());
        hashCode = hashCode * 31 + Primitives.hashCode(getDefaultValue());
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof HashObjByteMapFactory) {
            HashObjByteMapFactory factory = (HashObjByteMapFactory) obj;
            return commonEquals(factory) && keySpecialEquals(factory) &&
                    // boxing to treat NaNs correctly
                   ((Byte) getDefaultValue()).equals(factory.getDefaultValue())
                    ;
        } else {
            return false;
        }
    }

    @Override
    public byte getDefaultValue() {
        return (byte) 0;
    }

    

    

    

    

    

    

    
    

    
    

    private <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> shrunk(
            UpdatableLHashSeparateKVObjByteMapGO<K2> map) {
        Predicate<HashContainer> shrinkCondition;
        if ((shrinkCondition = hashConf.getShrinkCondition()) != null) {
            if (shrinkCondition.test(map))
                map.shrink();
        }
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap() {
        return newUpdatableMap(getDefaultExpectedSize());
    }
    @Override
    @Nonnull
    public <K2 extends K>
     MutableLHashSeparateKVObjByteMapGO<K2> newMutableMap() {
        return newMutableMap(getDefaultExpectedSize());
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Map<? extends K2, Byte> map) {
        return newUpdatableMap(map, map.size());
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Map<? extends K2, Byte> map1, Map<? extends K2, Byte> map2) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        return newUpdatableMap(map1, map2, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Map<? extends K2, Byte> map1, Map<? extends K2, Byte> map2,
            Map<? extends K2, Byte> map3) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        return newUpdatableMap(map1, map2, map3, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Map<? extends K2, Byte> map1, Map<? extends K2, Byte> map2,
            Map<? extends K2, Byte> map3, Map<? extends K2, Byte> map4) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        return newUpdatableMap(map1, map2, map3, map4, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Map<? extends K2, Byte> map1, Map<? extends K2, Byte> map2,
            Map<? extends K2, Byte> map3, Map<? extends K2, Byte> map4,
            Map<? extends K2, Byte> map5) {
        long expectedSize = (long) map1.size();
        expectedSize += (long) map2.size();
        expectedSize += (long) map3.size();
        expectedSize += (long) map4.size();
        expectedSize += (long) map5.size();
        return newUpdatableMap(map1, map2, map3, map4, map5, sizeAsInt(expectedSize));
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Map<? extends K2, Byte> map, int expectedSize) {
        return shrunk(super.newUpdatableMap(map, expectedSize));
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Map<? extends K2, Byte> map1, Map<? extends K2, Byte> map2,
            int expectedSize) {
        UpdatableLHashSeparateKVObjByteMapGO<K2> map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Map<? extends K2, Byte> map1, Map<? extends K2, Byte> map2,
            Map<? extends K2, Byte> map3, int expectedSize) {
        UpdatableLHashSeparateKVObjByteMapGO<K2> map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Map<? extends K2, Byte> map1, Map<? extends K2, Byte> map2,
            Map<? extends K2, Byte> map3, Map<? extends K2, Byte> map4,
            int expectedSize) {
        UpdatableLHashSeparateKVObjByteMapGO<K2> map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        return shrunk(map);
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Map<? extends K2, Byte> map1, Map<? extends K2, Byte> map2,
            Map<? extends K2, Byte> map3, Map<? extends K2, Byte> map4,
            Map<? extends K2, Byte> map5, int expectedSize) {
        UpdatableLHashSeparateKVObjByteMapGO<K2> map = newUpdatableMap(expectedSize);
        map.putAll(map1);
        map.putAll(map2);
        map.putAll(map3);
        map.putAll(map4);
        map.putAll(map5);
        return shrunk(map);
    }


    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Consumer<net.openhft.koloboke.function.ObjByteConsumer<K2>> entriesSupplier) {
        return newUpdatableMap(entriesSupplier, getDefaultExpectedSize());
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Consumer<net.openhft.koloboke.function.ObjByteConsumer<K2>> entriesSupplier,
            int expectedSize) {
        final UpdatableLHashSeparateKVObjByteMapGO<K2> map = newUpdatableMap(expectedSize);
        entriesSupplier.accept(new net.openhft.koloboke.function.ObjByteConsumer<K2>() {
             @Override
             public void accept(K2 k, byte v) {
                 map.put(k, v);
             }
         });
        return shrunk(map);
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            K2[] keys, byte[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            K2[] keys, byte[] values, int expectedSize) {
        UpdatableLHashSeparateKVObjByteMapGO<K2> map = newUpdatableMap(expectedSize);
        int keysLen = keys.length;
        if (keysLen != values.length)
            throw new IllegalArgumentException("keys and values arrays must have the same size");
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return shrunk(map);
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            K2[] keys, Byte[] values) {
        return newUpdatableMap(keys, values, keys.length);
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            K2[] keys, Byte[] values, int expectedSize) {
        UpdatableLHashSeparateKVObjByteMapGO<K2> map = newUpdatableMap(expectedSize);
        int keysLen = keys.length;
        if (keysLen != values.length)
            throw new IllegalArgumentException("keys and values arrays must have the same size");
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return shrunk(map);
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Iterable<? extends K2> keys, Iterable<Byte> values) {
        int expectedSize = keys instanceof Collection ? ((Collection) keys).size() :
                getDefaultExpectedSize();
        return newUpdatableMap(keys, values, expectedSize);
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMap(
            Iterable<? extends K2> keys, Iterable<Byte> values, int expectedSize) {
        UpdatableLHashSeparateKVObjByteMapGO<K2> map = newUpdatableMap(expectedSize);
        Iterator<? extends K2> keysIt = keys.iterator();
        Iterator<Byte> valuesIt = values.iterator();
        try {
            while (keysIt.hasNext()) {
                map.put(keysIt.next(), valuesIt.next());
            }
            return shrunk(map);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException(
                    "keys and values iterables must have the same size", e);
        }
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMapOf(
            K2 k1, byte v1) {
        UpdatableLHashSeparateKVObjByteMapGO<K2> map = newUpdatableMap(1);
        map.put(k1, v1);
        return map;
    }

    @Override
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMapOf(
            K2 k1, byte v1, K2 k2, byte v2) {
        UpdatableLHashSeparateKVObjByteMapGO<K2> map = newUpdatableMap(2);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMapOf(
            K2 k1, byte v1, K2 k2, byte v2,
            K2 k3, byte v3) {
        UpdatableLHashSeparateKVObjByteMapGO<K2> map = newUpdatableMap(3);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMapOf(
            K2 k1, byte v1, K2 k2, byte v2,
            K2 k3, byte v3, K2 k4, byte v4) {
        UpdatableLHashSeparateKVObjByteMapGO<K2> map = newUpdatableMap(4);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     UpdatableLHashSeparateKVObjByteMapGO<K2> newUpdatableMapOf(
            K2 k1, byte v1, K2 k2, byte v2,
            K2 k3, byte v3, K2 k4, byte v4,
            K2 k5, byte v5) {
        UpdatableLHashSeparateKVObjByteMapGO<K2> map = newUpdatableMap(5);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    
    

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(
            Map<? extends K2, Byte> map, int expectedSize) {
        MutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedMutableMap();
        res.move(newUpdatableMap(map, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, int expectedSize) {
        MutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, Map<? extends K2, Byte> map3, int expectedSize) {
        MutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, Map<? extends K2, Byte> map3,
            Map<? extends K2, Byte> map4, int expectedSize) {
        MutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, Map<? extends K2, Byte> map3,
            Map<? extends K2, Byte> map4, Map<? extends K2, Byte> map5, int expectedSize) {
        MutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5, expectedSize));
        return res;
    }



    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(
            Consumer<net.openhft.koloboke.function.ObjByteConsumer<K2>> entriesSupplier
            , int expectedSize) {
        MutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(K2[] keys,
            byte[] values, int expectedSize) {
        MutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(
            K2[] keys, Byte[] values, int expectedSize) {
        MutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(Iterable<? extends K2> keys,
            Iterable<Byte> values, int expectedSize) {
        MutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    
    

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(
            Map<? extends K2, Byte> map) {
        MutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedMutableMap();
        res.move(newUpdatableMap(map));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2) {
        MutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, Map<? extends K2, Byte> map3) {
        MutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, Map<? extends K2, Byte> map3,
            Map<? extends K2, Byte> map4) {
        MutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, Map<? extends K2, Byte> map3,
            Map<? extends K2, Byte> map4, Map<? extends K2, Byte> map5) {
        MutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedMutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5));
        return res;
    }



    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(
            Consumer<net.openhft.koloboke.function.ObjByteConsumer<K2>> entriesSupplier
            ) {
        MutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedMutableMap();
        map.move(newUpdatableMap(entriesSupplier));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(K2[] keys,
            byte[] values) {
        MutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(
            K2[] keys, Byte[] values) {
        MutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMap(Iterable<? extends K2> keys,
            Iterable<Byte> values) {
        MutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedMutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }


    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMapOf(K2 k1, byte v1) {
        MutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMapOf(K2 k1, byte v1,
             K2 k2, byte v2) {
        MutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMapOf(K2 k1, byte v1,
             K2 k2, byte v2, K2 k3, byte v3) {
        MutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMapOf(K2 k1, byte v1,
             K2 k2, byte v2, K2 k3, byte v3,
             K2 k4, byte v4) {
        MutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newMutableMapOf(K2 k1, byte v1,
             K2 k2, byte v2, K2 k3, byte v3,
             K2 k4, byte v4, K2 k5, byte v5) {
        MutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedMutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return map;
    }
    
    

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(
            Map<? extends K2, Byte> map, int expectedSize) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, int expectedSize) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, Map<? extends K2, Byte> map3, int expectedSize) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, Map<? extends K2, Byte> map3,
            Map<? extends K2, Byte> map4, int expectedSize) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, expectedSize));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, Map<? extends K2, Byte> map3,
            Map<? extends K2, Byte> map4, Map<? extends K2, Byte> map5, int expectedSize) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5, expectedSize));
        return res;
    }



    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(
            Consumer<net.openhft.koloboke.function.ObjByteConsumer<K2>> entriesSupplier
            , int expectedSize) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedImmutableMap();
        map.move(newUpdatableMap(entriesSupplier, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(K2[] keys,
            byte[] values, int expectedSize) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(
            K2[] keys, Byte[] values, int expectedSize) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(Iterable<? extends K2> keys,
            Iterable<Byte> values, int expectedSize) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values, expectedSize));
        return map;
    }

    
    

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(
            Map<? extends K2, Byte> map) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, Map<? extends K2, Byte> map3) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, Map<? extends K2, Byte> map3,
            Map<? extends K2, Byte> map4) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4));
        return res;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(Map<? extends K2, Byte> map1,
            Map<? extends K2, Byte> map2, Map<? extends K2, Byte> map3,
            Map<? extends K2, Byte> map4, Map<? extends K2, Byte> map5) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> res = uninitializedImmutableMap();
        res.move(newUpdatableMap(map1, map2, map3, map4, map5));
        return res;
    }



    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(
            Consumer<net.openhft.koloboke.function.ObjByteConsumer<K2>> entriesSupplier
            ) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedImmutableMap();
        map.move(newUpdatableMap(entriesSupplier));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(K2[] keys,
            byte[] values) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(
            K2[] keys, Byte[] values) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMap(Iterable<? extends K2> keys,
            Iterable<Byte> values) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedImmutableMap();
        map.move(newUpdatableMap(keys, values));
        return map;
    }


    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMapOf(K2 k1, byte v1) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMapOf(K2 k1, byte v1,
             K2 k2, byte v2) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMapOf(K2 k1, byte v1,
             K2 k2, byte v2, K2 k3, byte v3) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMapOf(K2 k1, byte v1,
             K2 k2, byte v2, K2 k3, byte v3,
             K2 k4, byte v4) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4));
        return map;
    }

    @Override
    @Nonnull
    public <K2 extends K>
     HashObjByteMap<K2> newImmutableMapOf(K2 k1, byte v1,
             K2 k2, byte v2, K2 k3, byte v3,
             K2 k4, byte v4, K2 k5, byte v5) {
        ImmutableLHashSeparateKVObjByteMapGO<K2> map = uninitializedImmutableMap();
        map.move(newUpdatableMapOf(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return map;
    }
}
