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
import net.openhft.koloboke.collect.hash.HashConfig;
import net.openhft.koloboke.collect.impl.*;
import net.openhft.koloboke.collect.map.*;
import net.openhft.koloboke.collect.map.hash.*;
import net.openhft.koloboke.collect.set.*;
import net.openhft.koloboke.collect.set.hash.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.openhft.koloboke.function.CharPredicate;
import net.openhft.koloboke.function.CharObjConsumer;
import net.openhft.koloboke.function.CharObjPredicate;
import net.openhft.koloboke.function.CharObjFunction;
import net.openhft.koloboke.function.CharFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import java.util.*;


public class MutableQHashSeparateKVCharObjMapGO<V>
        extends MutableQHashSeparateKVCharObjMapSO<V> {

    @Override
    final void copy(SeparateKVCharObjQHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    @Override
    final void move(SeparateKVCharObjQHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    @Override
    @Nonnull
    public Equivalence<V> valueEquivalence() {
        return Equivalence.defaultEquality();
    }


    @Override
    public boolean containsEntry(char key, Object value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return nullableValueEquals(values[index], (V) value);
        } else {
            // key is absent
            return false;
        }
    }


    @Override
    public V get(Object key) {
        int index = index((Character) key);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return null;
        }
    }

    

    @Override
    public V get(char key) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        int index = index((Character) key);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public V getOrDefault(char key, V defaultValue) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public void forEach(BiConsumer<? super Character, ? super V> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    action.accept(key, vals[i]);
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    action.accept(key, vals[i]);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void forEach(CharObjConsumer<? super V> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    action.accept(key, vals[i]);
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    action.accept(key, vals[i]);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public boolean forEachWhile(CharObjPredicate<? super V> predicate) {
        if (predicate == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return true;
        boolean terminated = false;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    if (!predicate.test(key, vals[i])) {
                        terminated = true;
                        break;
                    }
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    if (!predicate.test(key, vals[i])) {
                        terminated = true;
                        break;
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return !terminated;
    }

    @Nonnull
    @Override
    public CharObjCursor<V> cursor() {
        int mc = modCount();
        if (!noRemoved())
            return new SomeRemovedMapCursor(mc);
        return new NoRemovedMapCursor(mc);
    }


    @Override
    public boolean containsAllEntries(Map<?, ?> m) {
        return CommonCharObjMapOps.containsAllEntries(this, m);
    }

    @Override
    public boolean allEntriesContainingIn(InternalCharObjMapOps<?> m) {
        if (isEmpty())
            return true;
        boolean containsAll = true;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    if (!m.containsEntry(key, vals[i])) {
                        containsAll = false;
                        break;
                    }
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    if (!m.containsEntry(key, vals[i])) {
                        containsAll = false;
                        break;
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return containsAll;
    }

    @Override
    public void reversePutAllTo(InternalCharObjMapOps<? super V> m) {
        if (isEmpty())
            return;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    m.justPut(key, vals[i]);
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    m.justPut(key, vals[i]);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    @Nonnull
    public HashObjSet<Map.Entry<Character, V>> entrySet() {
        return new EntryView();
    }

    @Override
    @Nonnull
    public ObjCollection<V> values() {
        return new ValueView();
    }


    @Override
    public boolean equals(Object o) {
        return CommonMapOps.equals(this, o);
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    hashCode += key ^ nullableValueHashCode(vals[i]);
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    hashCode += key ^ nullableValueHashCode(vals[i]);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return hashCode;
    }

    @Override
    public String toString() {
        if (isEmpty())
            return "{}";
        StringBuilder sb = new StringBuilder();
        int elementCount = 0;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    sb.append(' ');
                    sb.append(key);
                    sb.append('=');
                    Object val = vals[i];
                    sb.append(val != this ? val : "(this Map)");
                    sb.append(',');
                    if (++elementCount == 8) {
                        int expectedLength = sb.length() * (size() / 8);
                        sb.ensureCapacity(expectedLength + (expectedLength / 2));
                    }
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    sb.append(' ');
                    sb.append(key);
                    sb.append('=');
                    Object val = vals[i];
                    sb.append(val != this ? val : "(this Map)");
                    sb.append(',');
                    if (++elementCount == 8) {
                        int expectedLength = sb.length() * (size() / 8);
                        sb.ensureCapacity(expectedLength + (expectedLength / 2));
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        sb.setCharAt(0, '{');
        sb.setCharAt(sb.length() - 1, '}');
        return sb.toString();
    }


    @Override
    void rehash(int newCapacity) {
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        initForRehash(newCapacity);
        mc++; // modCount is incremented in initForRehash()
        char[] newKeys = set;
        int capacity = newKeys.length;
        V[] newVals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    int index;
                    if (newKeys[index = SeparateKVCharKeyMixing.mix(key) % capacity] != free) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if (newKeys[bIndex] == free) {
                                index = bIndex;
                                break;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if (newKeys[fIndex] == free) {
                                index = fIndex;
                                break;
                            }
                            step += 2;
                        }
                    }
                    newKeys[index] = key;
                    newVals[index] = vals[i];
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    int index;
                    if (newKeys[index = SeparateKVCharKeyMixing.mix(key) % capacity] != free) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if (newKeys[bIndex] == free) {
                                index = bIndex;
                                break;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if (newKeys[fIndex] == free) {
                                index = fIndex;
                                break;
                            }
                            step += 2;
                        }
                    }
                    newKeys[index] = key;
                    newVals[index] = vals[i];
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }


    @Override
    public V put(Character key, V value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            V[] vals = values;
            V prevValue = vals[index];
            vals[index] = value;
            return prevValue;
        }
    }

    @Override
    public V put(char key, V value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            V[] vals = values;
            V prevValue = vals[index];
            vals[index] = value;
            return prevValue;
        }
    }

    @Override
    public V putIfAbsent(Character key, V value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            return values[index];
        }
    }

    @Override
    public V putIfAbsent(char key, V value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            return values[index];
        }
    }

    @Override
    public void justPut(char key, V value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return;
        } else {
            // key is present
            values[index] = value;
            return;
        }
    }


    @Override
    public V compute(Character key,
            BiFunction<? super Character, ? super V, ? extends V> remappingFunction) {
        char k = key;
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        char free;
        char removed = removedValue;
        if (k == (free = freeValue)) {
            free = changeFree();
        } else if (k == removed) {
            removed = changeRemoved();
        }
        char[] keys = set;
        V[] vals = values;
        int capacity, index;
        char cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVCharKeyMixing.mix(k) % (capacity = keys.length)]) != k) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = keys[bIndex]) == k) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = keys[fIndex]) == k) {
                                index = fIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            }
                            step += 2;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                keyAbsentRemovedSlot: {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = keys[bIndex]) == k) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = keys[fIndex]) == k) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = fIndex;
                        }
                        step += 2;
                    }
                }
                // key is absent, removed slot
                V newValue = remappingFunction.apply(k, null);
                if (newValue != null) {
                    incrementModCount();
                    keys[firstRemoved] = k;
                    vals[firstRemoved] = newValue;
                    postRemovedSlotInsertHook();
                    return newValue;
                } else {
                    return null;
                }
            }
            // key is absent, free slot
            V newValue = remappingFunction.apply(k, null);
            if (newValue != null) {
                incrementModCount();
                keys[index] = k;
                vals[index] = newValue;
                postFreeSlotInsertHook();
                return newValue;
            } else {
                return null;
            }
        }
        // key is present
        V newValue = remappingFunction.apply(k, vals[index]);
        if (newValue != null) {
            vals[index] = newValue;
            return newValue;
        } else {
            incrementModCount();
            keys[index] = removed;
            vals[index] = null;
            postRemoveHook();
            return null;
        }
    }


    @Override
    public V compute(char key, CharObjFunction<? super V, ? extends V> remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        char free;
        char removed = removedValue;
        if (key == (free = freeValue)) {
            free = changeFree();
        } else if (key == removed) {
            removed = changeRemoved();
        }
        char[] keys = set;
        V[] vals = values;
        int capacity, index;
        char cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVCharKeyMixing.mix(key) % (capacity = keys.length)]) != key) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = keys[bIndex]) == key) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = keys[fIndex]) == key) {
                                index = fIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            }
                            step += 2;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                keyAbsentRemovedSlot: {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = keys[bIndex]) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = keys[fIndex]) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = fIndex;
                        }
                        step += 2;
                    }
                }
                // key is absent, removed slot
                V newValue = remappingFunction.apply(key, null);
                if (newValue != null) {
                    incrementModCount();
                    keys[firstRemoved] = key;
                    vals[firstRemoved] = newValue;
                    postRemovedSlotInsertHook();
                    return newValue;
                } else {
                    return null;
                }
            }
            // key is absent, free slot
            V newValue = remappingFunction.apply(key, null);
            if (newValue != null) {
                incrementModCount();
                keys[index] = key;
                vals[index] = newValue;
                postFreeSlotInsertHook();
                return newValue;
            } else {
                return null;
            }
        }
        // key is present
        V newValue = remappingFunction.apply(key, vals[index]);
        if (newValue != null) {
            vals[index] = newValue;
            return newValue;
        } else {
            incrementModCount();
            keys[index] = removed;
            vals[index] = null;
            postRemoveHook();
            return null;
        }
    }


    @Override
    public V computeIfAbsent(Character key,
            Function<? super Character, ? extends V> mappingFunction) {
        char k = key;
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        char free;
        char removed = removedValue;
        if (k == (free = freeValue)) {
            free = changeFree();
        } else if (k == removed) {
            removed = changeRemoved();
        }
        char[] keys = set;
        V[] vals = values;
        int capacity, index;
        char cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVCharKeyMixing.mix(k) % (capacity = keys.length)]) != k) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = keys[bIndex]) == k) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = keys[fIndex]) == k) {
                                index = fIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            }
                            step += 2;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                keyAbsentRemovedSlot: {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = keys[bIndex]) == k) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = keys[fIndex]) == k) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = fIndex;
                        }
                        step += 2;
                    }
                }
                // key is absent, removed slot
                V value = mappingFunction.apply(k);
                if (value != null) {
                    incrementModCount();
                    keys[firstRemoved] = k;
                    vals[firstRemoved] = value;
                    postRemovedSlotInsertHook();
                    return value;
                } else {
                    return null;
                }
            }
            // key is absent, free slot
            V value = mappingFunction.apply(k);
            if (value != null) {
                incrementModCount();
                keys[index] = k;
                vals[index] = value;
                postFreeSlotInsertHook();
                return value;
            } else {
                return null;
            }
        }
        // key is present
        V val;
        if ((val = vals[index]) != null) {
            return val;
        } else {
            V value = mappingFunction.apply(k);
            if (value != null) {
                vals[index] = value;
                return value;
            } else {
                return null;
            }
        }
    }


    @Override
    public V computeIfAbsent(char key, CharFunction<? extends V> mappingFunction) {
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        char free;
        char removed = removedValue;
        if (key == (free = freeValue)) {
            free = changeFree();
        } else if (key == removed) {
            removed = changeRemoved();
        }
        char[] keys = set;
        V[] vals = values;
        int capacity, index;
        char cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVCharKeyMixing.mix(key) % (capacity = keys.length)]) != key) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = keys[bIndex]) == key) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = keys[fIndex]) == key) {
                                index = fIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            }
                            step += 2;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                keyAbsentRemovedSlot: {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = keys[bIndex]) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = keys[fIndex]) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = fIndex;
                        }
                        step += 2;
                    }
                }
                // key is absent, removed slot
                V value = mappingFunction.apply(key);
                if (value != null) {
                    incrementModCount();
                    keys[firstRemoved] = key;
                    vals[firstRemoved] = value;
                    postRemovedSlotInsertHook();
                    return value;
                } else {
                    return null;
                }
            }
            // key is absent, free slot
            V value = mappingFunction.apply(key);
            if (value != null) {
                incrementModCount();
                keys[index] = key;
                vals[index] = value;
                postFreeSlotInsertHook();
                return value;
            } else {
                return null;
            }
        }
        // key is present
        V val;
        if ((val = vals[index]) != null) {
            return val;
        } else {
            V value = mappingFunction.apply(key);
            if (value != null) {
                vals[index] = value;
                return value;
            } else {
                return null;
            }
        }
    }


    @Override
    public V computeIfPresent(Character key,
            BiFunction<? super Character, ? super V, ? extends V> remappingFunction) {
        char k = key;
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(k);
        if (index >= 0) {
            // key is present
            V[] vals = values;
            V val;
            if ((val = vals[index]) != null) {
                V newValue = remappingFunction.apply(k, val);
                if (newValue != null) {
                    vals[index] = newValue;
                    return newValue;
                } else {
                    incrementModCount();
                    set[index] = removedValue;
                    vals[index] = null;
                    postRemoveHook();
                    return null;
                }
            } else {
                return null;
            }
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public V computeIfPresent(char key, CharObjFunction<? super V, ? extends V> remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(key);
        if (index >= 0) {
            // key is present
            V[] vals = values;
            V val;
            if ((val = vals[index]) != null) {
                V newValue = remappingFunction.apply(key, val);
                if (newValue != null) {
                    vals[index] = newValue;
                    return newValue;
                } else {
                    incrementModCount();
                    set[index] = removedValue;
                    vals[index] = null;
                    postRemoveHook();
                    return null;
                }
            } else {
                return null;
            }
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public V merge(Character key, V value,
            BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        char k = key;
        if (value == null)
            throw new java.lang.NullPointerException();
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        char free;
        char removed = removedValue;
        if (k == (free = freeValue)) {
            free = changeFree();
        } else if (k == removed) {
            removed = changeRemoved();
        }
        char[] keys = set;
        V[] vals = values;
        int capacity, index;
        char cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVCharKeyMixing.mix(k) % (capacity = keys.length)]) != k) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = keys[bIndex]) == k) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = keys[fIndex]) == k) {
                                index = fIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            }
                            step += 2;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                keyAbsentRemovedSlot: {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = keys[bIndex]) == k) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = keys[fIndex]) == k) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = fIndex;
                        }
                        step += 2;
                    }
                }
                // key is absent, removed slot
                incrementModCount();
                keys[firstRemoved] = k;
                vals[firstRemoved] = value;
                postRemovedSlotInsertHook();
                return value;
            }
            // key is absent, free slot
            incrementModCount();
            keys[index] = k;
            vals[index] = value;
            postFreeSlotInsertHook();
            return value;
        }
        // key is present
        V val;
        if ((val = vals[index]) != null) {
            V newValue = remappingFunction.apply(val, value);
            if (newValue != null) {
                vals[index] = newValue;
                return newValue;
            } else {
                incrementModCount();
                keys[index] = removed;
                vals[index] = null;
                postRemoveHook();
                return null;
            }
        } else {
            vals[index] = value;
            return value;
        }
    }


    @Override
    public V merge(char key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        if (value == null)
            throw new java.lang.NullPointerException();
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        char free;
        char removed = removedValue;
        if (key == (free = freeValue)) {
            free = changeFree();
        } else if (key == removed) {
            removed = changeRemoved();
        }
        char[] keys = set;
        V[] vals = values;
        int capacity, index;
        char cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVCharKeyMixing.mix(key) % (capacity = keys.length)]) != key) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = keys[bIndex]) == key) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = keys[fIndex]) == key) {
                                index = fIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            }
                            step += 2;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                keyAbsentRemovedSlot: {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = keys[bIndex]) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = bIndex;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = keys[fIndex]) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            if (firstRemoved < 0) {
                                index = fIndex;
                                break keyAbsentFreeSlot;
                            } else {
                                break keyAbsentRemovedSlot;
                            }
                        } else if (cur == removed && firstRemoved < 0) {
                            firstRemoved = fIndex;
                        }
                        step += 2;
                    }
                }
                // key is absent, removed slot
                incrementModCount();
                keys[firstRemoved] = key;
                vals[firstRemoved] = value;
                postRemovedSlotInsertHook();
                return value;
            }
            // key is absent, free slot
            incrementModCount();
            keys[index] = key;
            vals[index] = value;
            postFreeSlotInsertHook();
            return value;
        }
        // key is present
        V val;
        if ((val = vals[index]) != null) {
            V newValue = remappingFunction.apply(val, value);
            if (newValue != null) {
                vals[index] = newValue;
                return newValue;
            } else {
                incrementModCount();
                keys[index] = removed;
                vals[index] = null;
                postRemoveHook();
                return null;
            }
        } else {
            vals[index] = value;
            return value;
        }
    }




    @Override
    public void putAll(@Nonnull Map<? extends Character, ? extends V> m) {
        CommonCharObjMapOps.putAll(this, m);
    }


    @Override
    public V replace(Character key, V value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            V[] vals = values;
            V oldValue = vals[index];
            vals[index] = value;
            return oldValue;
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public V replace(char key, V value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            V[] vals = values;
            V oldValue = vals[index];
            vals[index] = value;
            return oldValue;
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public boolean replace(Character key, V oldValue, V newValue) {
        return replace(key.charValue(),
                oldValue,
                newValue);
    }

    @Override
    public boolean replace(char key, V oldValue, V newValue) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            V[] vals = values;
            if (nullableValueEquals(vals[index], (V) oldValue)) {
                vals[index] = newValue;
                return true;
            } else {
                return false;
            }
        } else {
            // key is absent
            return false;
        }
    }


    @Override
    public void replaceAll(
            BiFunction<? super Character, ? super V, ? extends V> function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    vals[i] = function.apply(key, vals[i]);
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    vals[i] = function.apply(key, vals[i]);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void replaceAll(CharObjFunction<? super V, ? extends V> function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    vals[i] = function.apply(key, vals[i]);
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    vals[i] = function.apply(key, vals[i]);
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }


    @Override
    public void clear() {
        int mc = modCount() + 1;
        super.clear();
        if (mc != modCount())
            throw new ConcurrentModificationException();
    }


    @Override
    void removeAt(int index) {
        incrementModCount();
        super.removeAt(index);
        values[index] = null;
        postRemoveHook();
    }

    @Override
    public V remove(Object key) {
        char k = (Character) key;
        char free, removed;
        if (k != (free = freeValue) && k != (removed = removedValue)) {
            char[] keys = set;
            int capacity, index;
            char cur;
            keyPresent:
            if ((cur = keys[index = SeparateKVCharKeyMixing.mix(k) % (capacity = keys.length)]) != k) {
                if (cur == free) {
                    // key is absent, free slot
                    return null;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = keys[bIndex]) == k) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return null;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = keys[fIndex]) == k) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return null;
                        }
                        step += 2;
                    }
                }
            }
            // key is present
            V[] vals = values;
            V val = vals[index];
            incrementModCount();
            keys[index] = removed;
            vals[index] = null;
            postRemoveHook();
            return val;
        } else {
            // key is absent
            return null;
        }
    }


    @Override
    public boolean justRemove(char key) {
        char free, removed;
        if (key != (free = freeValue) && key != (removed = removedValue)) {
            char[] keys = set;
            int capacity, index;
            char cur;
            keyPresent:
            if ((cur = keys[index = SeparateKVCharKeyMixing.mix(key) % (capacity = keys.length)]) != key) {
                if (cur == free) {
                    // key is absent, free slot
                    return false;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = keys[bIndex]) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return false;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = keys[fIndex]) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return false;
                        }
                        step += 2;
                    }
                }
            }
            // key is present
            incrementModCount();
            keys[index] = removed;
            values[index] = null;
            postRemoveHook();
            return true;
        } else {
            // key is absent
            return false;
        }
    }



    

    @Override
    public V remove(char key) {
        char free, removed;
        if (key != (free = freeValue) && key != (removed = removedValue)) {
            char[] keys = set;
            int capacity, index;
            char cur;
            keyPresent:
            if ((cur = keys[index = SeparateKVCharKeyMixing.mix(key) % (capacity = keys.length)]) != key) {
                if (cur == free) {
                    // key is absent, free slot
                    return null;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = keys[bIndex]) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return null;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = keys[fIndex]) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return null;
                        }
                        step += 2;
                    }
                }
            }
            // key is present
            V[] vals = values;
            V val = vals[index];
            incrementModCount();
            keys[index] = removed;
            vals[index] = null;
            postRemoveHook();
            return val;
        } else {
            // key is absent
            return null;
        }
    }



    @Override
    public boolean remove(Object key, Object value) {
        return remove(((Character) key).charValue(),
                value);
    }

    @Override
    public boolean remove(char key, Object value) {
        char free, removed;
        if (key != (free = freeValue) && key != (removed = removedValue)) {
            char[] keys = set;
            int capacity, index;
            char cur;
            keyPresent:
            if ((cur = keys[index = SeparateKVCharKeyMixing.mix(key) % (capacity = keys.length)]) != key) {
                if (cur == free) {
                    // key is absent, free slot
                    return false;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = keys[bIndex]) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return false;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = keys[fIndex]) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return false;
                        }
                        step += 2;
                    }
                }
            }
            // key is present
            V[] vals = values;
            if (nullableValueEquals(vals[index], (V) value)) {
                incrementModCount();
                keys[index] = removed;
                vals[index] = null;
                postRemoveHook();
                return true;
            } else {
                return false;
            }
        } else {
            // key is absent
            return false;
        }
    }


    @Override
    public boolean removeIf(CharObjPredicate<? super V> filter) {
        if (filter == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    if (filter.test(key, vals[i])) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    if (filter.test(key, vals[i])) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }




    // under this condition - operations, overridden from MutableSeparateKVCharQHashGO
    // when values are objects - in order to set values to null on removing (for garbage collection)
    // when algo is LHash - because shift deletion should shift values to

    @Override
    public boolean removeIf(Predicate<? super Character> filter) {
        if (filter == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    if (filter.test(key)) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    if (filter.test(key)) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }

    @Override
    public boolean removeIf(CharPredicate filter) {
        if (filter == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    if (filter.test(key)) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    if (filter.test(key)) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }

    @Override
    public boolean removeAll(@Nonnull HashCharSet thisC, @Nonnull Collection<?> c) {
        if (thisC == c)
            throw new IllegalArgumentException();
        if (isEmpty() || c.isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    if (c.contains(key)) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    if (c.contains(key)) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }

    @Override
    boolean removeAll(@Nonnull HashCharSet thisC, @Nonnull CharCollection c) {
        if (thisC == c)
            throw new IllegalArgumentException();
        if (isEmpty() || c.isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    if (c.contains(key)) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    if (c.contains(key)) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }


    @Override
    public boolean retainAll(@Nonnull HashCharSet thisC, @Nonnull Collection<?> c) {
        if (c instanceof CharCollection)
            return retainAll(thisC, (CharCollection) c);
        if (thisC == c)
            throw new IllegalArgumentException();
        if (isEmpty())
            return false;
        if (c.isEmpty()) {
            clear();
            return true;
        }
        boolean changed = false;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    if (!c.contains(key)) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    if (!c.contains(key)) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }

    private boolean retainAll(@Nonnull HashCharSet thisC, @Nonnull CharCollection c) {
        if (thisC == c)
            throw new IllegalArgumentException();
        if (isEmpty())
            return false;
        if (c.isEmpty()) {
            clear();
            return true;
        }
        boolean changed = false;
        int mc = modCount();
        char free = freeValue;
        char removed = removedValue;
        char[] keys = set;
        V[] vals = values;
        if (noRemoved()) {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    if (!c.contains(key)) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (int i = keys.length - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    if (!c.contains(key)) {
                        incrementModCount();
                        mc++;
                        keys[i] = removed;
                        vals[i] = null;
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return changed;
    }





    @Override
    public CharIterator iterator() {
        int mc = modCount();
        if (!noRemoved())
            return new SomeRemovedKeyIterator(mc);
        return new NoRemovedKeyIterator(mc);
    }

    @Override
    public CharCursor setCursor() {
        int mc = modCount();
        if (!noRemoved())
            return new SomeRemovedKeyCursor(mc);
        return new NoRemovedKeyCursor(mc);
    }


    class NoRemovedKeyIterator extends NoRemovedIterator {
        final V[] vals;

        private NoRemovedKeyIterator(int mc) {
            super(mc);
            vals = values;
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    keys[index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class NoRemovedKeyCursor extends NoRemovedCursor {
        final V[] vals;

        private NoRemovedKeyCursor(int mc) {
            super(mc);
            vals = values;
        }

        @Override
        public void remove() {
            char free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    int index;
                    keys[index = this.index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedKeyIterator extends SomeRemovedIterator {
        final V[] vals;

        private SomeRemovedKeyIterator(int mc) {
            super(mc);
            vals = values;
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    keys[index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedKeyCursor extends SomeRemovedCursor {
        final V[] vals;

        private SomeRemovedKeyCursor(int mc) {
            super(mc);
            vals = values;
        }

        @Override
        public void remove() {
            char free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    int index;
                    keys[index = this.index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }





    class EntryView extends AbstractSetView<Map.Entry<Character, V>>
            implements HashObjSet<Map.Entry<Character, V>>,
            InternalObjCollectionOps<Map.Entry<Character, V>> {

        @Nonnull
        @Override
        public Equivalence<Entry<Character, V>> equivalence() {
            return Equivalence.entryEquivalence(
                    Equivalence.<Character>defaultEquality()
                    ,
                    valueEquivalence()
            );
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return MutableQHashSeparateKVCharObjMapGO.this.hashConfig();
        }


        @Override
        public int size() {
            return MutableQHashSeparateKVCharObjMapGO.this.size();
        }

        @Override
        public double currentLoad() {
            return MutableQHashSeparateKVCharObjMapGO.this.currentLoad();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Character, V> e = (Map.Entry<Character, V>) o;
                return containsEntry(e.getKey(), e.getValue());
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        @Nonnull
        public final Object[] toArray() {
            int size = size();
            Object[] result = new Object[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        result[resultIndex++] = new MutableEntry(mc, i, key, vals[i]);
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        result[resultIndex++] = new MutableEntry(mc, i, key, vals[i]);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return result;
        }

        @Override
        @SuppressWarnings("unchecked")
        @Nonnull
        public final <T> T[] toArray(@Nonnull T[] a) {
            int size = size();
            if (a.length < size) {
                Class<?> elementType = a.getClass().getComponentType();
                a = (T[]) java.lang.reflect.Array.newInstance(elementType, size);
            }
            if (size == 0) {
                if (a.length > 0)
                    a[0] = null;
                return a;
            }
            int resultIndex = 0;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        a[resultIndex++] = (T) new MutableEntry(mc, i, key, vals[i]);
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        a[resultIndex++] = (T) new MutableEntry(mc, i, key, vals[i]);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public final void forEach(@Nonnull Consumer<? super Map.Entry<Character, V>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        action.accept(new MutableEntry(mc, i, key, vals[i]));
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        action.accept(new MutableEntry(mc, i, key, vals[i]));
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public boolean forEachWhile(@Nonnull  Predicate<? super Map.Entry<Character, V>> predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        if (!predicate.test(new MutableEntry(mc, i, key, vals[i]))) {
                            terminated = true;
                            break;
                        }
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        if (!predicate.test(new MutableEntry(mc, i, key, vals[i]))) {
                            terminated = true;
                            break;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return !terminated;
        }

        @Override
        @Nonnull
        public ObjIterator<Map.Entry<Character, V>> iterator() {
            int mc = modCount();
            if (!noRemoved())
                return new SomeRemovedEntryIterator(mc);
            return new NoRemovedEntryIterator(mc);
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Character, V>> cursor() {
            int mc = modCount();
            if (!noRemoved())
                return new SomeRemovedEntryCursor(mc);
            return new NoRemovedEntryCursor(mc);
        }

        @Override
        public final boolean containsAll(@Nonnull Collection<?> c) {
            return CommonObjCollectionOps.containsAll(this, c);
        }

        @Override
        public final boolean allContainingIn(ObjCollection<?> c) {
            if (isEmpty())
                return true;
            boolean containsAll = true;
            ReusableEntry e = new ReusableEntry();
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        if (!c.contains(e.with(key, vals[i]))) {
                            containsAll = false;
                            break;
                        }
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        if (!c.contains(e.with(key, vals[i]))) {
                            containsAll = false;
                            break;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return containsAll;
        }

        @Override
        public boolean reverseRemoveAllFrom(ObjSet<?> s) {
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            ReusableEntry e = new ReusableEntry();
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        changed |= s.remove(e.with(key, vals[i]));
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        changed |= s.remove(e.with(key, vals[i]));
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Character, V>> c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        changed |= c.add(new MutableEntry(mc, i, key, vals[i]));
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        changed |= c.add(new MutableEntry(mc, i, key, vals[i]));
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        public int hashCode() {
            return MutableQHashSeparateKVCharObjMapGO.this.hashCode();
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        sb.append(' ');
                        sb.append(key);
                        sb.append('=');
                        Object val = vals[i];
                        sb.append(val != this ? val : "(this Collection)");
                        sb.append(',');
                        if (++elementCount == 8) {
                            int expectedLength = sb.length() * (size() / 8);
                            sb.ensureCapacity(expectedLength + (expectedLength / 2));
                        }
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        sb.append(' ');
                        sb.append(key);
                        sb.append('=');
                        Object val = vals[i];
                        sb.append(val != this ? val : "(this Collection)");
                        sb.append(',');
                        if (++elementCount == 8) {
                            int expectedLength = sb.length() * (size() / 8);
                            sb.ensureCapacity(expectedLength + (expectedLength / 2));
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            sb.setCharAt(0, '[');
            sb.setCharAt(sb.length() - 1, ']');
            return sb.toString();
        }

        @Override
        public boolean shrink() {
            return MutableQHashSeparateKVCharObjMapGO.this.shrink();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Character, V> e = (Map.Entry<Character, V>) o;
                char key = e.getKey();
                V value = e.getValue();
                return MutableQHashSeparateKVCharObjMapGO.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        public final boolean removeIf(@Nonnull Predicate<? super Map.Entry<Character, V>> filter) {
            if (filter == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        if (filter.test(new MutableEntry(mc, i, key, vals[i]))) {
                            incrementModCount();
                            mc++;
                            keys[i] = removed;
                            vals[i] = null;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        if (filter.test(new MutableEntry(mc, i, key, vals[i]))) {
                            incrementModCount();
                            mc++;
                            keys[i] = removed;
                            vals[i] = null;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public final boolean removeAll(@Nonnull Collection<?> c) {
            if (c instanceof InternalObjCollectionOps) {
                InternalObjCollectionOps c2 = (InternalObjCollectionOps) c;
                if (equivalence().equals(c2.equivalence()) && c2.size() < this.size()) {
                    // noinspection unchecked
                    c2.reverseRemoveAllFrom(this);
                }
            }
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty() || c.isEmpty())
                return false;
            boolean changed = false;
            ReusableEntry e = new ReusableEntry();
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        if (c.contains(e.with(key, vals[i]))) {
                            incrementModCount();
                            mc++;
                            keys[i] = removed;
                            vals[i] = null;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        if (c.contains(e.with(key, vals[i]))) {
                            incrementModCount();
                            mc++;
                            keys[i] = removed;
                            vals[i] = null;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public final boolean retainAll(@Nonnull Collection<?> c) {
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty())
                return false;
            if (c.isEmpty()) {
                clear();
                return true;
            }
            boolean changed = false;
            ReusableEntry e = new ReusableEntry();
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        if (!c.contains(e.with(key, vals[i]))) {
                            incrementModCount();
                            mc++;
                            keys[i] = removed;
                            vals[i] = null;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        if (!c.contains(e.with(key, vals[i]))) {
                            incrementModCount();
                            mc++;
                            keys[i] = removed;
                            vals[i] = null;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public void clear() {
            MutableQHashSeparateKVCharObjMapGO.this.clear();
        }
    }


    abstract class CharObjEntry extends AbstractEntry<Character, V> {

        abstract char key();

        @Override
        public final Character getKey() {
            return key();
        }

        abstract V value();

        @Override
        public final V getValue() {
            return value();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            Map.Entry e2;
            char k2;
            V v2;
            try {
                e2 = (Map.Entry) o;
                k2 = (Character) e2.getKey();
                v2 = (V) e2.getValue();
                return key() == k2
                        
                        &&
                        nullableValueEquals(value(), v2);
            } catch (ClassCastException e) {
                return false;
            } catch (NullPointerException e) {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Primitives.hashCode(key())
                    
                    ^
                    nullableValueHashCode(value());
        }
    }


    class MutableEntry extends CharObjEntry {
        final int modCount;
        private final int index;
        final char key;
        private V value;

        MutableEntry(int modCount, int index, char key, V value) {
            this.modCount = modCount;
            this.index = index;
            this.key = key;
            this.value = value;
        }

        @Override
        public char key() {
            return key;
        }

        @Override
        public V value() {
            return value;
        }

        @Override
        public V setValue(V newValue) {
            if (modCount != modCount())
                throw new IllegalStateException();
            V oldValue = value;
            V unwrappedNewValue = newValue;
            value = unwrappedNewValue;
            updateValueInTable(unwrappedNewValue);
            return oldValue;
        }

        void updateValueInTable(V newValue) {
            values[index] = newValue;
        }
    }



    class ReusableEntry extends CharObjEntry {
        private char key;
        private V value;

        ReusableEntry with(char key, V value) {
            this.key = key;
            this.value = value;
            return this;
        }

        @Override
        public char key() {
            return key;
        }

        @Override
        public V value() {
            return value;
        }
    }


    class ValueView extends AbstractObjValueView<V> {

        @Override
        public Equivalence<V> equivalence() {
            return valueEquivalence();
        }

        @Override
        public int size() {
            return MutableQHashSeparateKVCharObjMapGO.this.size();
        }

        @Override
        public boolean shrink() {
            return MutableQHashSeparateKVCharObjMapGO.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return MutableQHashSeparateKVCharObjMapGO.this.containsValue(o);
        }



        @Override
        public void forEach(Consumer<? super V> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    if (keys[i] != free) {
                        action.accept(vals[i]);
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        action.accept(vals[i]);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }


        @Override
        public boolean forEachWhile(Predicate<? super V> predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    if (keys[i] != free) {
                        if (!predicate.test(vals[i])) {
                            terminated = true;
                            break;
                        }
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        if (!predicate.test(vals[i])) {
                            terminated = true;
                            break;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return !terminated;
        }

        @Override
        public boolean allContainingIn(ObjCollection<?> c) {
            if (isEmpty())
                return true;
            boolean containsAll = true;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    if (keys[i] != free) {
                        if (!c.contains(vals[i])) {
                            containsAll = false;
                            break;
                        }
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        if (!c.contains(vals[i])) {
                            containsAll = false;
                            break;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return containsAll;
        }


        @Override
        public boolean reverseAddAllTo(ObjCollection<? super V> c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    if (keys[i] != free) {
                        changed |= c.add(vals[i]);
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        changed |= c.add(vals[i]);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        @Override
        public boolean reverseRemoveAllFrom(ObjSet<?> s) {
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    if (keys[i] != free) {
                        changed |= s.remove(vals[i]);
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        changed |= s.remove(vals[i]);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }



        @Override
        @Nonnull
        public ObjIterator<V> iterator() {
            int mc = modCount();
            if (!noRemoved())
                return new SomeRemovedValueIterator(mc);
            return new NoRemovedValueIterator(mc);
        }

        @Nonnull
        @Override
        public ObjCursor<V> cursor() {
            int mc = modCount();
            if (!noRemoved())
                return new SomeRemovedValueCursor(mc);
            return new NoRemovedValueCursor(mc);
        }

        @Override
        @Nonnull
        public Object[] toArray() {
            int size = size();
            Object[] result = new Object[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    if (keys[i] != free) {
                        result[resultIndex++] = vals[i];
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        result[resultIndex++] = vals[i];
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return result;
        }

        @Override
        @SuppressWarnings("unchecked")
        @Nonnull
        public <T> T[] toArray(@Nonnull T[] a) {
            int size = size();
            if (a.length < size) {
                Class<?> elementType = a.getClass().getComponentType();
                a = (T[]) java.lang.reflect.Array.newInstance(elementType, size);
            }
            if (size == 0) {
                if (a.length > 0)
                    a[0] = null;
                return a;
            }
            int resultIndex = 0;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    if (keys[i] != free) {
                        a[resultIndex++] = (T) vals[i];
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        a[resultIndex++] = (T) vals[i];
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }



        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    if (keys[i] != free) {
                    V val;
                        sb.append(' ').append((val = vals[i]) != this ? val : "(this Collection)").append(',');
                        if (++elementCount == 8) {
                            int expectedLength = sb.length() * (size() / 8);
                            sb.ensureCapacity(expectedLength + (expectedLength / 2));
                        }
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                    V val;
                        sb.append(' ').append((val = vals[i]) != this ? val : "(this Collection)").append(',');
                        if (++elementCount == 8) {
                            int expectedLength = sb.length() * (size() / 8);
                            sb.ensureCapacity(expectedLength + (expectedLength / 2));
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            sb.setCharAt(0, '[');
            sb.setCharAt(sb.length() - 1, ']');
            return sb.toString();
        }


        @Override
        public boolean remove(Object o) {
            return removeValue(o);
        }



        @Override
        public void clear() {
            MutableQHashSeparateKVCharObjMapGO.this.clear();
        }

        @Override
        public boolean removeIf(Predicate<? super V> filter) {
            if (filter == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    if (keys[i] != free) {
                        if (filter.test(vals[i])) {
                            incrementModCount();
                            mc++;
                            keys[i] = removed;
                            vals[i] = null;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        if (filter.test(vals[i])) {
                            incrementModCount();
                            mc++;
                            keys[i] = removed;
                            vals[i] = null;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty() || c.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    if (keys[i] != free) {
                        if (c.contains(vals[i])) {
                            incrementModCount();
                            mc++;
                            keys[i] = removed;
                            vals[i] = null;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        if (c.contains(vals[i])) {
                            incrementModCount();
                            mc++;
                            keys[i] = removed;
                            vals[i] = null;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty())
                return false;
            if (c.isEmpty()) {
                clear();
                return true;
            }
            boolean changed = false;
            int mc = modCount();
            char free = freeValue;
            char removed = removedValue;
            char[] keys = set;
            V[] vals = values;
            if (noRemoved()) {
                for (int i = keys.length - 1; i >= 0; i--) {
                    if (keys[i] != free) {
                        if (!c.contains(vals[i])) {
                            incrementModCount();
                            mc++;
                            keys[i] = removed;
                            vals[i] = null;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = keys.length - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        if (!c.contains(vals[i])) {
                            incrementModCount();
                            mc++;
                            keys[i] = removed;
                            vals[i] = null;
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

    }



    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Character, V>> {
        final char[] keys;
        final V[] vals;
        final char free;
        final char removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        MutableEntry next;

        NoRemovedEntryIterator(int mc) {
            expectedModCount = mc;
            char[] keys = this.keys = set;
            V[] vals = this.vals = values;
            char free = this.free = freeValue;
            this.removed = removedValue;
            int nextI = keys.length;
            while (--nextI >= 0) {
                char key;
                if ((key = keys[nextI]) != free) {
                    next = new MutableEntry(mc, nextI, key, vals[nextI]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Character, V>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            char[] keys = this.keys;
            V[] vals = this.vals;
            char free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    action.accept(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public Map.Entry<Character, V> next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                int mc;
                if ((mc = expectedModCount) == modCount()) {
                    index = nextI;
                    char[] keys = this.keys;
                    char free = this.free;
                    MutableEntry prev = next;
                    while (--nextI >= 0) {
                        char key;
                        if ((key = keys[nextI]) != free) {
                            next = new MutableEntry(mc, nextI, key, vals[nextI]);
                            break;
                        }
                    }
                    nextIndex = nextI;
                    return prev;
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.util.NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    keys[index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Character, V>> {
        final char[] keys;
        final V[] vals;
        final char free;
        final char removed;
        int expectedModCount;
        int index;
        char curKey;
        V curValue;

        NoRemovedEntryCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            char free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Character, V>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            char[] keys = this.keys;
            V[] vals = this.vals;
            char free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    action.accept(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public Map.Entry<Character, V> elem() {
            char curKey;
            if ((curKey = this.curKey) != free) {
                return new MutableEntry(expectedModCount, index, curKey, curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                char[] keys = this.keys;
                char free = this.free;
                for (int i = index - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
                        return true;
                    }
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            char free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    int index;
                    keys[index = this.index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedEntryIterator implements ObjIterator<Map.Entry<Character, V>> {
        final char[] keys;
        final V[] vals;
        final char free;
        final char removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        MutableEntry next;

        SomeRemovedEntryIterator(int mc) {
            expectedModCount = mc;
            char[] keys = this.keys = set;
            V[] vals = this.vals = values;
            char free = this.free = freeValue;
            char removed = this.removed = removedValue;
            int nextI = keys.length;
            while (--nextI >= 0) {
                char key;
                if ((key = keys[nextI]) != free && key != removed) {
                    next = new MutableEntry(mc, nextI, key, vals[nextI]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Character, V>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            char[] keys = this.keys;
            V[] vals = this.vals;
            char free = this.free;
            char removed = this.removed;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    action.accept(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public Map.Entry<Character, V> next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                int mc;
                if ((mc = expectedModCount) == modCount()) {
                    index = nextI;
                    char[] keys = this.keys;
                    char free = this.free;
                    char removed = this.removed;
                    MutableEntry prev = next;
                    while (--nextI >= 0) {
                        char key;
                        if ((key = keys[nextI]) != free && key != removed) {
                            next = new MutableEntry(mc, nextI, key, vals[nextI]);
                            break;
                        }
                    }
                    nextIndex = nextI;
                    return prev;
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.util.NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    keys[index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedEntryCursor implements ObjCursor<Map.Entry<Character, V>> {
        final char[] keys;
        final V[] vals;
        final char free;
        final char removed;
        int expectedModCount;
        int index;
        char curKey;
        V curValue;

        SomeRemovedEntryCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            char free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Character, V>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            char[] keys = this.keys;
            V[] vals = this.vals;
            char free = this.free;
            char removed = this.removed;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    action.accept(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public Map.Entry<Character, V> elem() {
            char curKey;
            if ((curKey = this.curKey) != free) {
                return new MutableEntry(expectedModCount, index, curKey, curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                char[] keys = this.keys;
                char free = this.free;
                char removed = this.removed;
                for (int i = index - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
                        return true;
                    }
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            char free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    int index;
                    keys[index = this.index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }




    class NoRemovedValueIterator implements ObjIterator<V> {
        final char[] keys;
        final V[] vals;
        final char free;
        final char removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        V next;

        NoRemovedValueIterator(int mc) {
            expectedModCount = mc;
            char[] keys = this.keys = set;
            V[] vals = this.vals = values;
            char free = this.free = freeValue;
            this.removed = removedValue;
            int nextI = keys.length;
            while (--nextI >= 0) {
                if (keys[nextI] != free) {
                    next = vals[nextI];
                    break;
                }
            }
            nextIndex = nextI;
        }


        @Override
        public void forEachRemaining(Consumer<? super V> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            char[] keys = this.keys;
            V[] vals = this.vals;
            char free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(vals[i]);
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }


        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public V next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                if (expectedModCount == modCount()) {
                    index = nextI;
                    char[] keys = this.keys;
                    char free = this.free;
                    V prev = next;
                    while (--nextI >= 0) {
                        if (keys[nextI] != free) {
                            next = vals[nextI];
                            break;
                        }
                    }
                    nextIndex = nextI;
                    return prev;
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.util.NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    keys[index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class NoRemovedValueCursor implements ObjCursor<V> {
        final char[] keys;
        final V[] vals;
        final char free;
        final char removed;
        int expectedModCount;
        int index;
        char curKey;
        V curValue;

        NoRemovedValueCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            char free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super V> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            char[] keys = this.keys;
            V[] vals = this.vals;
            char free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(vals[i]);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public V elem() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                char[] keys = this.keys;
                char free = this.free;
                for (int i = index - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
                        return true;
                    }
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            char free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    int index;
                    keys[index = this.index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedValueIterator implements ObjIterator<V> {
        final char[] keys;
        final V[] vals;
        final char free;
        final char removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        V next;

        SomeRemovedValueIterator(int mc) {
            expectedModCount = mc;
            char[] keys = this.keys = set;
            V[] vals = this.vals = values;
            char free = this.free = freeValue;
            char removed = this.removed = removedValue;
            int nextI = keys.length;
            while (--nextI >= 0) {
                char key;
                if ((key = keys[nextI]) != free && key != removed) {
                    next = vals[nextI];
                    break;
                }
            }
            nextIndex = nextI;
        }


        @Override
        public void forEachRemaining(Consumer<? super V> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            char[] keys = this.keys;
            V[] vals = this.vals;
            char free = this.free;
            char removed = this.removed;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    action.accept(vals[i]);
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }


        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public V next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                if (expectedModCount == modCount()) {
                    index = nextI;
                    char[] keys = this.keys;
                    char free = this.free;
                    char removed = this.removed;
                    V prev = next;
                    while (--nextI >= 0) {
                        char key;
                        if ((key = keys[nextI]) != free && key != removed) {
                            next = vals[nextI];
                            break;
                        }
                    }
                    nextIndex = nextI;
                    return prev;
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.util.NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    keys[index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedValueCursor implements ObjCursor<V> {
        final char[] keys;
        final V[] vals;
        final char free;
        final char removed;
        int expectedModCount;
        int index;
        char curKey;
        V curValue;

        SomeRemovedValueCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            char free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super V> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            char[] keys = this.keys;
            V[] vals = this.vals;
            char free = this.free;
            char removed = this.removed;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    action.accept(vals[i]);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public V elem() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                char[] keys = this.keys;
                char free = this.free;
                char removed = this.removed;
                for (int i = index - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
                        return true;
                    }
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            char free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    int index;
                    keys[index = this.index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }



    class NoRemovedMapCursor implements CharObjCursor<V> {
        final char[] keys;
        final V[] vals;
        final char free;
        final char removed;
        int expectedModCount;
        int index;
        char curKey;
        V curValue;

        NoRemovedMapCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            char free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(CharObjConsumer<? super V> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            char[] keys = this.keys;
            V[] vals = this.vals;
            char free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free) {
                    action.accept(key, vals[i]);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public char key() {
            char curKey;
            if ((curKey = this.curKey) != free) {
                return curKey;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public V value() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(V value) {
            if (curKey != free) {
                if (expectedModCount == modCount()) {
                    vals[index] = value;
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                char[] keys = this.keys;
                char free = this.free;
                for (int i = index - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
                        return true;
                    }
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            char free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    int index;
                    keys[index = this.index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }

    class SomeRemovedMapCursor implements CharObjCursor<V> {
        final char[] keys;
        final V[] vals;
        final char free;
        final char removed;
        int expectedModCount;
        int index;
        char curKey;
        V curValue;

        SomeRemovedMapCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            char free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(CharObjConsumer<? super V> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            char[] keys = this.keys;
            V[] vals = this.vals;
            char free = this.free;
            char removed = this.removed;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                char key;
                if ((key = keys[i]) != free && key != removed) {
                    action.accept(key, vals[i]);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public char key() {
            char curKey;
            if ((curKey = this.curKey) != free) {
                return curKey;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public V value() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(V value) {
            if (curKey != free) {
                if (expectedModCount == modCount()) {
                    vals[index] = value;
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                char[] keys = this.keys;
                char free = this.free;
                char removed = this.removed;
                for (int i = index - 1; i >= 0; i--) {
                    char key;
                    if ((key = keys[i]) != free && key != removed) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
                        return true;
                    }
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            char free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    int index;
                    keys[index = this.index] = removed;
                    vals[index] = null;
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }
}
