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

import net.openhft.koloboke.collect.impl.*;
import net.openhft.koloboke.collect.map.hash.HashIntFloatMap;
import javax.annotation.Nonnull;


public abstract class ImmutableLHashParallelKVIntFloatMapSO
        extends ImmutableLHashParallelKVIntKeyMap
        implements HashIntFloatMap, InternalIntFloatMapOps, ParallelKVIntFloatLHash {


    
    int valueIndex(int value) {
        if (isEmpty())
            return -1;
        int index = -1;
        int free = freeValue;
        long[] tab = table;
        long entry;
        for (int i = tab.length - 1; i >= 0; i--) {
            if ((int) (entry = tab[i]) != free) {
                if (value == (int) (entry >>> 32)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    
    boolean containsValue(int value) {
        return valueIndex(value) >= 0;
    }

    boolean removeValue(int value) {
        throw new UnsupportedOperationException();
    }
    
    int valueIndex(float value) {
        if (isEmpty())
            return -1;
        int val = Float.floatToIntBits(value);
        int index = -1;
        int free = freeValue;
        long[] tab = table;
        long entry;
        for (int i = tab.length - 1; i >= 0; i--) {
            if ((int) (entry = tab[i]) != free) {
                if (val == (int) (entry >>> 32)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    @Override public
    boolean containsValue(float value) {
        return valueIndex(value) >= 0;
    }

    boolean removeValue(float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValue(((Float) value).floatValue());
    }

}
