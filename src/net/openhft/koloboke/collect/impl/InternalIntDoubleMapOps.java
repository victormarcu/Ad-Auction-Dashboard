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

import net.openhft.koloboke.collect.map.IntDoubleMap;


public interface InternalIntDoubleMapOps
        extends IntDoubleMap, InternalMapOps<Integer, Double> {

    boolean containsEntry(int key, double value);

    void justPut(int key, double value);

    boolean containsEntry(int key, long value);

    void justPut(int key, long value);

    boolean allEntriesContainingIn(InternalIntDoubleMapOps map);

    void reversePutAllTo(InternalIntDoubleMapOps map);
}
