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
import net.openhft.koloboke.collect.map.ByteIntMap;
import net.openhft.koloboke.collect.map.hash.HashByteIntMapFactory;

import javax.annotation.Nonnull;
import java.util.Map;


public abstract class LHashSeparateKVByteIntMapFactorySO
        extends ByteLHashFactory 
        implements HashByteIntMapFactory {

    LHashSeparateKVByteIntMapFactorySO(HashConfig hashConf, int defaultExpectedSize
            , byte lower, byte upper) {
        super(hashConf, defaultExpectedSize, lower, upper);
    }

    

    

    


     MutableLHashSeparateKVByteIntMapGO uninitializedMutableMap() {
        return new MutableLHashSeparateKVByteIntMap();
    }
     UpdatableLHashSeparateKVByteIntMapGO uninitializedUpdatableMap() {
        return new UpdatableLHashSeparateKVByteIntMap();
    }
     ImmutableLHashSeparateKVByteIntMapGO uninitializedImmutableMap() {
        return new ImmutableLHashSeparateKVByteIntMap();
    }

    @Override
    @Nonnull
    public  MutableLHashSeparateKVByteIntMapGO newMutableMap(int expectedSize) {
        MutableLHashSeparateKVByteIntMapGO map = uninitializedMutableMap();
        map.init(configWrapper, expectedSize, getFree());
        return map;
    }

    

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVByteIntMapGO newUpdatableMap(int expectedSize) {
        UpdatableLHashSeparateKVByteIntMapGO map = uninitializedUpdatableMap();
        map.init(configWrapper, expectedSize, getFree());
        return map;
    }

    

    @Override
    @Nonnull
    public  UpdatableLHashSeparateKVByteIntMapGO newUpdatableMap(
            Map<Byte, Integer> map) {
        if (map instanceof ByteIntMap) {
            if (map instanceof SeparateKVByteIntLHash) {
                SeparateKVByteIntLHash hash = (SeparateKVByteIntLHash) map;
                if (hash.hashConfig().equals(hashConf)) {
                    UpdatableLHashSeparateKVByteIntMapGO res = uninitializedUpdatableMap();
                    res.copy(hash);
                    return res;
                }
            }
            UpdatableLHashSeparateKVByteIntMapGO res = newUpdatableMap(map.size());
            res.putAll(map);
            return res;
        }
        UpdatableLHashSeparateKVByteIntMapGO res = newUpdatableMap(map.size());
        for (Map.Entry<Byte, Integer> entry : map.entrySet()) {
            res.put(entry.getKey(), entry.getValue());
        }
        return res;
    }
}
