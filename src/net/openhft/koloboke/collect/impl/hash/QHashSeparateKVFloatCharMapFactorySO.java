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

import net.openhft.koloboke.collect.hash.*;
import net.openhft.koloboke.collect.map.FloatCharMap;
import net.openhft.koloboke.collect.map.hash.HashFloatCharMapFactory;

import javax.annotation.Nonnull;
import java.util.Map;


public abstract class QHashSeparateKVFloatCharMapFactorySO
        extends FloatQHashFactory 
        implements HashFloatCharMapFactory {

    QHashSeparateKVFloatCharMapFactorySO(HashConfig hashConf, int defaultExpectedSize
            ) {
        super(hashConf, defaultExpectedSize);
    }

    

    

    


     MutableQHashSeparateKVFloatCharMapGO uninitializedMutableMap() {
        return new MutableQHashSeparateKVFloatCharMap();
    }
     UpdatableQHashSeparateKVFloatCharMapGO uninitializedUpdatableMap() {
        return new UpdatableQHashSeparateKVFloatCharMap();
    }
     ImmutableQHashSeparateKVFloatCharMapGO uninitializedImmutableMap() {
        return new ImmutableQHashSeparateKVFloatCharMap();
    }

    @Override
    @Nonnull
    public  MutableQHashSeparateKVFloatCharMapGO newMutableMap(int expectedSize) {
        MutableQHashSeparateKVFloatCharMapGO map = uninitializedMutableMap();
        map.init(configWrapper, expectedSize);
        return map;
    }

    

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVFloatCharMapGO newUpdatableMap(int expectedSize) {
        UpdatableQHashSeparateKVFloatCharMapGO map = uninitializedUpdatableMap();
        map.init(configWrapper, expectedSize);
        return map;
    }

    

    @Override
    @Nonnull
    public  UpdatableQHashSeparateKVFloatCharMapGO newUpdatableMap(
            Map<Float, Character> map) {
        if (map instanceof FloatCharMap) {
            if (map instanceof SeparateKVFloatCharQHash) {
                SeparateKVFloatCharQHash hash = (SeparateKVFloatCharQHash) map;
                if (hash.hashConfig().equals(hashConf)) {
                    UpdatableQHashSeparateKVFloatCharMapGO res = uninitializedUpdatableMap();
                    res.copy(hash);
                    return res;
                }
            }
            UpdatableQHashSeparateKVFloatCharMapGO res = newUpdatableMap(map.size());
            res.putAll(map);
            return res;
        }
        UpdatableQHashSeparateKVFloatCharMapGO res = newUpdatableMap(map.size());
        for (Map.Entry<Float, Character> entry : map.entrySet()) {
            res.put(entry.getKey(), entry.getValue());
        }
        return res;
    }
}
