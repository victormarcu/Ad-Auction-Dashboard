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
import net.openhft.koloboke.function.FloatPredicate;
import net.openhft.koloboke.function.FloatObjConsumer;
import net.openhft.koloboke.function.FloatObjPredicate;
import net.openhft.koloboke.function.FloatObjFunction;
import net.openhft.koloboke.function.FloatFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import java.util.*;


public class UpdatableLHashSeparateKVFloatObjMapGO<V>
        extends UpdatableLHashSeparateKVFloatObjMapSO<V> {

    @Override
    final void copy(SeparateKVFloatObjLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    @Override
    final void move(SeparateKVFloatObjLHash hash) {
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
    public boolean containsEntry(float key, Object value) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            return nullableValueEquals(values[index], (V) value);
        } else {
            // key is absent
            return false;
        }
    }

    @Override
    public boolean containsEntry(int key, Object value) {
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
        int k = Float.floatToIntBits((Float) key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return null;
        }
    }

    

    @Override
    public V get(float key) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
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
        int k = Float.floatToIntBits((Float) key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public V getOrDefault(float key, V defaultValue) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public void forEach(BiConsumer<? super Float, ? super V> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        int[] keys = set;
        V[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                action.accept(Float.intBitsToFloat(key), vals[i]);
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void forEach(FloatObjConsumer<? super V> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        int[] keys = set;
        V[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                action.accept(Float.intBitsToFloat(key), vals[i]);
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public boolean forEachWhile(FloatObjPredicate<? super V> predicate) {
        if (predicate == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return true;
        boolean terminated = false;
        int mc = modCount();
        int[] keys = set;
        V[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                if (!predicate.test(Float.intBitsToFloat(key), vals[i])) {
                    terminated = true;
                    break;
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return !terminated;
    }

    @Nonnull
    @Override
    public FloatObjCursor<V> cursor() {
        int mc = modCount();
        return new NoRemovedMapCursor(mc);
    }


    @Override
    public boolean containsAllEntries(Map<?, ?> m) {
        return CommonFloatObjMapOps.containsAllEntries(this, m);
    }

    @Override
    public boolean allEntriesContainingIn(InternalFloatObjMapOps<?> m) {
        if (isEmpty())
            return true;
        boolean containsAll = true;
        int mc = modCount();
        int[] keys = set;
        V[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                if (!m.containsEntry(key, vals[i])) {
                    containsAll = false;
                    break;
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        return containsAll;
    }

    @Override
    public void reversePutAllTo(InternalFloatObjMapOps<? super V> m) {
        if (isEmpty())
            return;
        int mc = modCount();
        int[] keys = set;
        V[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                m.justPut(key, vals[i]);
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    @Nonnull
    public HashObjSet<Map.Entry<Float, V>> entrySet() {
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
        int[] keys = set;
        V[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                hashCode += key ^ nullableValueHashCode(vals[i]);
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
        int[] keys = set;
        V[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                sb.append(' ');
                sb.append(Float.intBitsToFloat(key));
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
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
        sb.setCharAt(0, '{');
        sb.setCharAt(sb.length() - 1, '}');
        return sb.toString();
    }


    @Override
    void rehash(int newCapacity) {
        int mc = modCount();
        int[] keys = set;
        V[] vals = values;
        initForRehash(newCapacity);
        mc++; // modCount is incremented in initForRehash()
        int[] newKeys = set;
        int capacityMask = newKeys.length - 1;
        V[] newVals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                int index;
                if (newKeys[index = SeparateKVFloatKeyMixing.mix(key) & capacityMask] != FREE_BITS) {
                    while (true) {
                        if (newKeys[(index = (index - 1) & capacityMask)] == FREE_BITS) {
                            break;
                        }
                    }
                }
                newKeys[index] = key;
                newVals[index] = vals[i];
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }


    @Override
    public V put(Float key, V value) {
        int k = Float.floatToIntBits(key);
        int index = insert(k, value);
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
    public V put(float key, V value) {
        int k = Float.floatToIntBits(key);
        int index = insert(k, value);
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
    public V putIfAbsent(Float key, V value) {
        int k = Float.floatToIntBits(key);
        int index = insert(k, value);
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            return values[index];
        }
    }

    @Override
    public V putIfAbsent(float key, V value) {
        int k = Float.floatToIntBits(key);
        int index = insert(k, value);
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            return values[index];
        }
    }

    @Override
    public void justPut(float key, V value) {
        int k = Float.floatToIntBits(key);
        int index = insert(k, value);
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
    public void justPut(int key, V value) {
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
    public V compute(Float key,
            BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
        int k = Float.floatToIntBits(key);
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int[] keys = set;
        V[] vals = values;
        int capacityMask, index;
        int cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVFloatKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) != k) {
            keyAbsent:
            if (cur != FREE_BITS) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        break keyPresent;
                    } else if (cur == FREE_BITS) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            V newValue = remappingFunction.apply(Float.intBitsToFloat(k), null);
            if (newValue != null) {
                incrementModCount();
                keys[index] = k;
                vals[index] = newValue;
                postInsertHook();
                return newValue;
            } else {
                return null;
            }
        }
        // key is present
        V newValue = remappingFunction.apply(Float.intBitsToFloat(k), vals[index]);
        if (newValue != null) {
            vals[index] = newValue;
            return newValue;
        } else {
            throw new java.lang.UnsupportedOperationException("Compute operation of updatable map doesn't support removals");
        }
    }


    @Override
    public V compute(float key, FloatObjFunction<? super V, ? extends V> remappingFunction) {
        int k = Float.floatToIntBits(key);
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int[] keys = set;
        V[] vals = values;
        int capacityMask, index;
        int cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVFloatKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) != k) {
            keyAbsent:
            if (cur != FREE_BITS) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        break keyPresent;
                    } else if (cur == FREE_BITS) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            V newValue = remappingFunction.apply(Float.intBitsToFloat(k), null);
            if (newValue != null) {
                incrementModCount();
                keys[index] = k;
                vals[index] = newValue;
                postInsertHook();
                return newValue;
            } else {
                return null;
            }
        }
        // key is present
        V newValue = remappingFunction.apply(Float.intBitsToFloat(k), vals[index]);
        if (newValue != null) {
            vals[index] = newValue;
            return newValue;
        } else {
            throw new java.lang.UnsupportedOperationException("Compute operation of updatable map doesn't support removals");
        }
    }


    @Override
    public V computeIfAbsent(Float key,
            Function<? super Float, ? extends V> mappingFunction) {
        int k = Float.floatToIntBits(key);
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        int[] keys = set;
        V[] vals = values;
        int capacityMask, index;
        int cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVFloatKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) != k) {
            keyAbsent:
            if (cur != FREE_BITS) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        break keyPresent;
                    } else if (cur == FREE_BITS) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            V value = mappingFunction.apply(Float.intBitsToFloat(k));
            if (value != null) {
                incrementModCount();
                keys[index] = k;
                vals[index] = value;
                postInsertHook();
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
            V value = mappingFunction.apply(Float.intBitsToFloat(k));
            if (value != null) {
                vals[index] = value;
                return value;
            } else {
                return null;
            }
        }
    }


    @Override
    public V computeIfAbsent(float key, FloatFunction<? extends V> mappingFunction) {
        int k = Float.floatToIntBits(key);
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        int[] keys = set;
        V[] vals = values;
        int capacityMask, index;
        int cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVFloatKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) != k) {
            keyAbsent:
            if (cur != FREE_BITS) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        break keyPresent;
                    } else if (cur == FREE_BITS) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            V value = mappingFunction.apply(Float.intBitsToFloat(k));
            if (value != null) {
                incrementModCount();
                keys[index] = k;
                vals[index] = value;
                postInsertHook();
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
            V value = mappingFunction.apply(Float.intBitsToFloat(k));
            if (value != null) {
                vals[index] = value;
                return value;
            } else {
                return null;
            }
        }
    }


    @Override
    public V computeIfPresent(Float key,
            BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
        int k = Float.floatToIntBits(key);
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(k);
        if (index >= 0) {
            // key is present
            V[] vals = values;
            V val;
            if ((val = vals[index]) != null) {
                V newValue = remappingFunction.apply(Float.intBitsToFloat(k), val);
                if (newValue != null) {
                    vals[index] = newValue;
                    return newValue;
                } else {
                    throw new java.lang.UnsupportedOperationException("ComputeIfPresent operation of updatable map doesn't support removals");
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
    public V computeIfPresent(float key, FloatObjFunction<? super V, ? extends V> remappingFunction) {
        int k = Float.floatToIntBits(key);
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(k);
        if (index >= 0) {
            // key is present
            V[] vals = values;
            V val;
            if ((val = vals[index]) != null) {
                V newValue = remappingFunction.apply(Float.intBitsToFloat(k), val);
                if (newValue != null) {
                    vals[index] = newValue;
                    return newValue;
                } else {
                    throw new java.lang.UnsupportedOperationException("ComputeIfPresent operation of updatable map doesn't support removals");
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
    public V merge(Float key, V value,
            BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        int k = Float.floatToIntBits(key);
        if (value == null)
            throw new java.lang.NullPointerException();
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int[] keys = set;
        V[] vals = values;
        int capacityMask, index;
        int cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVFloatKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) != k) {
            keyAbsent:
            if (cur != FREE_BITS) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        break keyPresent;
                    } else if (cur == FREE_BITS) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            incrementModCount();
            keys[index] = k;
            vals[index] = value;
            postInsertHook();
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
                throw new java.lang.UnsupportedOperationException("Merge operation of updatable map doesn't support removals");
            }
        } else {
            vals[index] = value;
            return value;
        }
    }


    @Override
    public V merge(float key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        int k = Float.floatToIntBits(key);
        if (value == null)
            throw new java.lang.NullPointerException();
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int[] keys = set;
        V[] vals = values;
        int capacityMask, index;
        int cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVFloatKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) != k) {
            keyAbsent:
            if (cur != FREE_BITS) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        break keyPresent;
                    } else if (cur == FREE_BITS) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            incrementModCount();
            keys[index] = k;
            vals[index] = value;
            postInsertHook();
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
                throw new java.lang.UnsupportedOperationException("Merge operation of updatable map doesn't support removals");
            }
        } else {
            vals[index] = value;
            return value;
        }
    }




    @Override
    public void putAll(@Nonnull Map<? extends Float, ? extends V> m) {
        CommonFloatObjMapOps.putAll(this, m);
    }


    @Override
    public V replace(Float key, V value) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
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
    public V replace(float key, V value) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
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
    public boolean replace(Float key, V oldValue, V newValue) {
        return replace(key.floatValue(),
                oldValue,
                newValue);
    }

    @Override
    public boolean replace(float key, V oldValue, V newValue) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
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
            BiFunction<? super Float, ? super V, ? extends V> function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        int[] keys = set;
        V[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                vals[i] = function.apply(Float.intBitsToFloat(key), vals[i]);
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void replaceAll(FloatObjFunction<? super V, ? extends V> function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        int[] keys = set;
        V[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                vals[i] = function.apply(Float.intBitsToFloat(key), vals[i]);
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
    public V remove(Object key) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public boolean justRemove(float key) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean justRemove(int key) {
        throw new java.lang.UnsupportedOperationException();
    }


    

    @Override
    public V remove(float key) {
        throw new java.lang.UnsupportedOperationException();
    }



    @Override
    public boolean remove(Object key, Object value) {
        return remove(((Float) key).floatValue(),
                value);
    }

    @Override
    public boolean remove(float key, Object value) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public boolean removeIf(FloatObjPredicate<? super V> filter) {
        throw new java.lang.UnsupportedOperationException();
    }






    class EntryView extends AbstractSetView<Map.Entry<Float, V>>
            implements HashObjSet<Map.Entry<Float, V>>,
            InternalObjCollectionOps<Map.Entry<Float, V>> {

        @Nonnull
        @Override
        public Equivalence<Entry<Float, V>> equivalence() {
            return Equivalence.entryEquivalence(
                    Equivalence.<Float>defaultEquality()
                    ,
                    valueEquivalence()
            );
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return UpdatableLHashSeparateKVFloatObjMapGO.this.hashConfig();
        }


        @Override
        public int size() {
            return UpdatableLHashSeparateKVFloatObjMapGO.this.size();
        }

        @Override
        public double currentLoad() {
            return UpdatableLHashSeparateKVFloatObjMapGO.this.currentLoad();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Float, V> e = (Map.Entry<Float, V>) o;
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
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    result[resultIndex++] = new MutableEntry(mc, i, key, vals[i]);
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
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    a[resultIndex++] = (T) new MutableEntry(mc, i, key, vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public final void forEach(@Nonnull Consumer<? super Map.Entry<Float, V>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    action.accept(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public boolean forEachWhile(@Nonnull  Predicate<? super Map.Entry<Float, V>> predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    if (!predicate.test(new MutableEntry(mc, i, key, vals[i]))) {
                        terminated = true;
                        break;
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return !terminated;
        }

        @Override
        @Nonnull
        public ObjIterator<Map.Entry<Float, V>> iterator() {
            int mc = modCount();
            return new NoRemovedEntryIterator(mc);
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Float, V>> cursor() {
            int mc = modCount();
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
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    if (!c.contains(e.with(key, vals[i]))) {
                        containsAll = false;
                        break;
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
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    changed |= s.remove(e.with(key, vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Float, V>> c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    changed |= c.add(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        public int hashCode() {
            return UpdatableLHashSeparateKVFloatObjMapGO.this.hashCode();
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    sb.append(' ');
                    sb.append(Float.intBitsToFloat(key));
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
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            sb.setCharAt(0, '[');
            sb.setCharAt(sb.length() - 1, ']');
            return sb.toString();
        }

        @Override
        public boolean shrink() {
            return UpdatableLHashSeparateKVFloatObjMapGO.this.shrink();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Float, V> e = (Map.Entry<Float, V>) o;
                float key = e.getKey();
                V value = e.getValue();
                return UpdatableLHashSeparateKVFloatObjMapGO.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        public final boolean removeIf(@Nonnull Predicate<? super Map.Entry<Float, V>> filter) {
            throw new java.lang.UnsupportedOperationException();
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
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public final boolean retainAll(@Nonnull Collection<?> c) {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public void clear() {
            UpdatableLHashSeparateKVFloatObjMapGO.this.clear();
        }
    }


    abstract class FloatObjEntry extends AbstractEntry<Float, V> {

        abstract int key();

        @Override
        public final Float getKey() {
            return Float.intBitsToFloat(key());
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
            int k2;
            V v2;
            try {
                e2 = (Map.Entry) o;
                k2 = Float.floatToIntBits((Float) e2.getKey());
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


    class MutableEntry extends FloatObjEntry {
        final int modCount;
        private final int index;
        final int key;
        private V value;

        MutableEntry(int modCount, int index, int key, V value) {
            this.modCount = modCount;
            this.index = index;
            this.key = key;
            this.value = value;
        }

        @Override
        public int key() {
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



    class ReusableEntry extends FloatObjEntry {
        private int key;
        private V value;

        ReusableEntry with(int key, V value) {
            this.key = key;
            this.value = value;
            return this;
        }

        @Override
        public int key() {
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
            return UpdatableLHashSeparateKVFloatObjMapGO.this.size();
        }

        @Override
        public boolean shrink() {
            return UpdatableLHashSeparateKVFloatObjMapGO.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return UpdatableLHashSeparateKVFloatObjMapGO.this.containsValue(o);
        }



        @Override
        public void forEach(Consumer<? super V> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    action.accept(vals[i]);
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
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    if (!predicate.test(vals[i])) {
                        terminated = true;
                        break;
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
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    if (!c.contains(vals[i])) {
                        containsAll = false;
                        break;
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
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    changed |= c.add(vals[i]);
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
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    changed |= s.remove(vals[i]);
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
            return new NoRemovedValueIterator(mc);
        }

        @Nonnull
        @Override
        public ObjCursor<V> cursor() {
            int mc = modCount();
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
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    result[resultIndex++] = vals[i];
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
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    a[resultIndex++] = (T) vals[i];
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
            int[] keys = set;
            V[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                V val;
                    sb.append(' ').append((val = vals[i]) != this ? val : "(this Collection)").append(',');
                    if (++elementCount == 8) {
                        int expectedLength = sb.length() * (size() / 8);
                        sb.ensureCapacity(expectedLength + (expectedLength / 2));
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
            UpdatableLHashSeparateKVFloatObjMapGO.this.clear();
        }

        @Override
        public boolean removeIf(Predicate<? super V> filter) {
            throw new java.lang.UnsupportedOperationException();
        }


        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            throw new java.lang.UnsupportedOperationException();
        }


        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            throw new java.lang.UnsupportedOperationException();
        }

    }



    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Float, V>> {
        final int[] keys;
        final V[] vals;
        int expectedModCount;
        int nextIndex;
        MutableEntry next;

        NoRemovedEntryIterator(int mc) {
            expectedModCount = mc;
            int[] keys = this.keys = set;
            V[] vals = this.vals = values;
            int nextI = keys.length;
            while (--nextI >= 0) {
                int key;
                if ((key = keys[nextI]) < FREE_BITS) {
                    next = new MutableEntry(mc, nextI, key, vals[nextI]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Float, V>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] keys = this.keys;
            V[] vals = this.vals;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    action.accept(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return nextIndex >= 0;
        }

        @Override
        public Map.Entry<Float, V> next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                int mc;
                if ((mc = expectedModCount) == modCount()) {
                    int[] keys = this.keys;
                    MutableEntry prev = next;
                    while (--nextI >= 0) {
                        int key;
                        if ((key = keys[nextI]) < FREE_BITS) {
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
            throw new java.lang.UnsupportedOperationException();
        }
    }


    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Float, V>> {
        final int[] keys;
        final V[] vals;
        int expectedModCount;
        int index;
        int curKey;
        V curValue;

        NoRemovedEntryCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            curKey = FREE_BITS;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Float, V>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] keys = this.keys;
            V[] vals = this.vals;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    action.accept(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = FREE_BITS;
        }

        @Override
        public Map.Entry<Float, V> elem() {
            int curKey;
            if ((curKey = this.curKey) != FREE_BITS) {
                return new MutableEntry(expectedModCount, index, curKey, curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                int[] keys = this.keys;
                for (int i = index - 1; i >= 0; i--) {
                    int key;
                    if ((key = keys[i]) < FREE_BITS) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
                        return true;
                    }
                }
                curKey = FREE_BITS;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }
    }




    class NoRemovedValueIterator implements ObjIterator<V> {
        final int[] keys;
        final V[] vals;
        int expectedModCount;
        int nextIndex;
        V next;

        NoRemovedValueIterator(int mc) {
            expectedModCount = mc;
            int[] keys = this.keys = set;
            V[] vals = this.vals = values;
            int nextI = keys.length;
            while (--nextI >= 0) {
                if (keys[nextI] < FREE_BITS) {
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
            int[] keys = this.keys;
            V[] vals = this.vals;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    action.accept(vals[i]);
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            nextIndex = -1;
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
                    int[] keys = this.keys;
                    V prev = next;
                    while (--nextI >= 0) {
                        if (keys[nextI] < FREE_BITS) {
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
            throw new java.lang.UnsupportedOperationException();
        }
    }


    class NoRemovedValueCursor implements ObjCursor<V> {
        final int[] keys;
        final V[] vals;
        int expectedModCount;
        int index;
        int curKey;
        V curValue;

        NoRemovedValueCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            curKey = FREE_BITS;
        }

        @Override
        public void forEachForward(Consumer<? super V> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] keys = this.keys;
            V[] vals = this.vals;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    action.accept(vals[i]);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = FREE_BITS;
        }

        @Override
        public V elem() {
            if (curKey != FREE_BITS) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                int[] keys = this.keys;
                for (int i = index - 1; i >= 0; i--) {
                    int key;
                    if ((key = keys[i]) < FREE_BITS) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
                        return true;
                    }
                }
                curKey = FREE_BITS;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }
    }



    class NoRemovedMapCursor implements FloatObjCursor<V> {
        final int[] keys;
        final V[] vals;
        int expectedModCount;
        int index;
        int curKey;
        V curValue;

        NoRemovedMapCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            curKey = FREE_BITS;
        }

        @Override
        public void forEachForward(FloatObjConsumer<? super V> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] keys = this.keys;
            V[] vals = this.vals;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    action.accept(Float.intBitsToFloat(key), vals[i]);
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = FREE_BITS;
        }

        @Override
        public float key() {
            int curKey;
            if ((curKey = this.curKey) != FREE_BITS) {
                return Float.intBitsToFloat(curKey);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public V value() {
            if (curKey != FREE_BITS) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(V value) {
            if (curKey != FREE_BITS) {
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
                int[] keys = this.keys;
                for (int i = index - 1; i >= 0; i--) {
                    int key;
                    if ((key = keys[i]) < FREE_BITS) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
                        return true;
                    }
                }
                curKey = FREE_BITS;
                index = -1;
                return false;
            } else {
                throw new java.util.ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }
    }
}
