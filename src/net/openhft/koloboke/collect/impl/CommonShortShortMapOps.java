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

package net.openhft.koloboke.collect.impl;

import net.openhft.koloboke.function.ShortShortPredicate;
import net.openhft.koloboke.function.ShortShortConsumer;
import net.openhft.koloboke.collect.map.ShortShortMap;

import java.util.Map;


public final class CommonShortShortMapOps {

    public static boolean containsAllEntries(final InternalShortShortMapOps map,
            Map<?, ?> another) {
        if ( map == another )
            throw new IllegalArgumentException();
        if (another instanceof ShortShortMap) {
            ShortShortMap m2 = (ShortShortMap) another;
                if (map.size() < m2.size())
                    return false;
                if (m2 instanceof InternalShortShortMapOps) {
                    //noinspection unchecked
                    return ((InternalShortShortMapOps) m2).allEntriesContainingIn(map);
                }
            return m2.forEachWhile(new
                   ShortShortPredicate() {
                @Override
                public boolean test(short a, short b) {
                    return map.containsEntry(a, b);
                }
            });
        }
        for ( Map.Entry<?, ?> e : another.entrySet() ) {
            if ( !map.containsEntry((Short) e.getKey(),
                    (Short) e.getValue()))
                return false;
        }
        return true;
    }

    public static  void putAll(final InternalShortShortMapOps map,
            Map<? extends Short, ? extends Short> another) {
        if (map == another)
            throw new IllegalArgumentException();
        long maxPossibleSize = map.sizeAsLong() + Containers.sizeAsLong(another);
        map.ensureCapacity(maxPossibleSize);
        if (another instanceof ShortShortMap) {
            if (another instanceof InternalShortShortMapOps) {
                ((InternalShortShortMapOps) another).reversePutAllTo(map);
            } else {
                ((ShortShortMap) another).forEach(new ShortShortConsumer() {
                    @Override
                    public void accept(short key, short value) {
                        map.justPut(key, value);
                    }
                });
            }
        } else {
            for (Map.Entry<? extends Short, ? extends Short> e : another.entrySet()) {
                map.justPut(e.getKey(), e.getValue());
            }
        }
    }

    private CommonShortShortMapOps() {}
}
