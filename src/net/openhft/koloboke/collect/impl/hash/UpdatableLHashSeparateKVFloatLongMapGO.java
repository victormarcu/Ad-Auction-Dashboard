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
import net.openhft.koloboke.function.FloatLongConsumer;
import net.openhft.koloboke.function.FloatLongPredicate;
import net.openhft.koloboke.function.FloatLongToLongFunction;
import net.openhft.koloboke.function.FloatToLongFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;

import javax.annotation.Nonnull;
import java.util.*;


public class UpdatableLHashSeparateKVFloatLongMapGO
        extends UpdatableLHashSeparateKVFloatLongMapSO {

    @Override
    final void copy(SeparateKVFloatLongLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    @Override
    final void move(SeparateKVFloatLongLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }


    @Override
    public long defaultValue() {
        return 0L;
    }

    @Override
    public boolean containsEntry(float key, long value) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            return values[index] == value;
        } else {
            // key is absent
            return false;
        }
    }

    @Override
    public boolean containsEntry(int key, long value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return values[index] == value;
        } else {
            // key is absent
            return false;
        }
    }

    @Override
    public Long get(Object key) {
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
    public long get(float key) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            return values[index];
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Long getOrDefault(Object key, Long defaultValue) {
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
    public long getOrDefault(float key, long defaultValue) {
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
    public void forEach(BiConsumer<? super Float, ? super Long> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        int[] keys = set;
        long[] vals = values;
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
    public void forEach(FloatLongConsumer action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        int[] keys = set;
        long[] vals = values;
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
    public boolean forEachWhile(FloatLongPredicate predicate) {
        if (predicate == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return true;
        boolean terminated = false;
        int mc = modCount();
        int[] keys = set;
        long[] vals = values;
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
    public FloatLongCursor cursor() {
        int mc = modCount();
        return new NoRemovedMapCursor(mc);
    }


    @Override
    public boolean containsAllEntries(Map<?, ?> m) {
        return CommonFloatLongMapOps.containsAllEntries(this, m);
    }

    @Override
    public boolean allEntriesContainingIn(InternalFloatLongMapOps m) {
        if (isEmpty())
            return true;
        boolean containsAll = true;
        int mc = modCount();
        int[] keys = set;
        long[] vals = values;
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
    public void reversePutAllTo(InternalFloatLongMapOps m) {
        if (isEmpty())
            return;
        int mc = modCount();
        int[] keys = set;
        long[] vals = values;
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
    public HashObjSet<Map.Entry<Float, Long>> entrySet() {
        return new EntryView();
    }

    @Override
    @Nonnull
    public LongCollection values() {
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
        long[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
            long val;
                hashCode += key ^ ((int) ((val = vals[i]) ^ (val >>> 32)));
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
        long[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                sb.append(' ');
                sb.append(Float.intBitsToFloat(key));
                sb.append('=');
                sb.append(vals[i]);
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
        long[] vals = values;
        initForRehash(newCapacity);
        mc++; // modCount is incremented in initForRehash()
        int[] newKeys = set;
        int capacityMask = newKeys.length - 1;
        long[] newVals = values;
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
    public Long put(Float key, Long value) {
        int k = Float.floatToIntBits(key);
        int index = insert(k, value);
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            long[] vals = values;
            long prevValue = vals[index];
            vals[index] = value;
            return prevValue;
        }
    }

    @Override
    public long put(float key, long value) {
        int k = Float.floatToIntBits(key);
        int index = insert(k, value);
        if (index < 0) {
            // key was absent
            return defaultValue();
        } else {
            // key is present
            long[] vals = values;
            long prevValue = vals[index];
            vals[index] = value;
            return prevValue;
        }
    }

    @Override
    public Long putIfAbsent(Float key, Long value) {
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
    public long putIfAbsent(float key, long value) {
        int k = Float.floatToIntBits(key);
        int index = insert(k, value);
        if (index < 0) {
            // key was absent
            return defaultValue();
        } else {
            // key is present
            return values[index];
        }
    }

    @Override
    public void justPut(float key, long value) {
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
    public void justPut(int key, long value) {
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
    public Long compute(Float key,
            BiFunction<? super Float, ? super Long, ? extends Long> remappingFunction) {
        int k = Float.floatToIntBits(key);
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int[] keys = set;
        long[] vals = values;
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
            Long newValue = remappingFunction.apply(Float.intBitsToFloat(k), null);
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
        Long newValue = remappingFunction.apply(Float.intBitsToFloat(k), vals[index]);
        if (newValue != null) {
            vals[index] = newValue;
            return newValue;
        } else {
            throw new java.lang.UnsupportedOperationException("Compute operation of updatable map doesn't support removals");
        }
    }


    @Override
    public long compute(float key, FloatLongToLongFunction remappingFunction) {
        int k = Float.floatToIntBits(key);
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int[] keys = set;
        long[] vals = values;
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
            long newValue = remappingFunction.applyAsLong(Float.intBitsToFloat(k), defaultValue());
            incrementModCount();
            keys[index] = k;
            vals[index] = newValue;
            postInsertHook();
            return newValue;
        }
        // key is present
        long newValue = remappingFunction.applyAsLong(Float.intBitsToFloat(k), vals[index]);
        vals[index] = newValue;
        return newValue;
    }


    @Override
    public Long computeIfAbsent(Float key,
            Function<? super Float, ? extends Long> mappingFunction) {
        int k = Float.floatToIntBits(key);
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        int[] keys = set;
        long[] vals = values;
        int capacityMask, index;
        int cur;
        if ((cur = keys[index = SeparateKVFloatKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) == k) {
            // key is present
            return vals[index];
        } else {
            keyAbsent:
            if (cur != FREE_BITS) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        // key is present
                        return vals[index];
                    } else if (cur == FREE_BITS) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            Long value = mappingFunction.apply(Float.intBitsToFloat(k));
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
    }


    @Override
    public long computeIfAbsent(float key, FloatToLongFunction mappingFunction) {
        int k = Float.floatToIntBits(key);
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        int[] keys = set;
        long[] vals = values;
        int capacityMask, index;
        int cur;
        if ((cur = keys[index = SeparateKVFloatKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) == k) {
            // key is present
            return vals[index];
        } else {
            keyAbsent:
            if (cur != FREE_BITS) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        // key is present
                        return vals[index];
                    } else if (cur == FREE_BITS) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            long value = mappingFunction.applyAsLong(Float.intBitsToFloat(k));
            incrementModCount();
            keys[index] = k;
            vals[index] = value;
            postInsertHook();
            return value;
        }
    }


    @Override
    public Long computeIfPresent(Float key,
            BiFunction<? super Float, ? super Long, ? extends Long> remappingFunction) {
        int k = Float.floatToIntBits(key);
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(k);
        if (index >= 0) {
            // key is present
            long[] vals = values;
            Long newValue = remappingFunction.apply(Float.intBitsToFloat(k), vals[index]);
            if (newValue != null) {
                vals[index] = newValue;
                return newValue;
            } else {
                throw new java.lang.UnsupportedOperationException("ComputeIfPresent operation of updatable map doesn't support removals");
            }
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public long computeIfPresent(float key, FloatLongToLongFunction remappingFunction) {
        int k = Float.floatToIntBits(key);
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(k);
        if (index >= 0) {
            // key is present
            long[] vals = values;
            long newValue = remappingFunction.applyAsLong(Float.intBitsToFloat(k), vals[index]);
            vals[index] = newValue;
            return newValue;
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Long merge(Float key, Long value,
            BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        int k = Float.floatToIntBits(key);
        if (value == null)
            throw new java.lang.NullPointerException();
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int[] keys = set;
        long[] vals = values;
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
        Long newValue = remappingFunction.apply(vals[index], value);
        if (newValue != null) {
            vals[index] = newValue;
            return newValue;
        } else {
            throw new java.lang.UnsupportedOperationException("Merge operation of updatable map doesn't support removals");
        }
    }


    @Override
    public long merge(float key, long value, LongBinaryOperator remappingFunction) {
        int k = Float.floatToIntBits(key);
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int[] keys = set;
        long[] vals = values;
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
        long newValue = remappingFunction.applyAsLong(vals[index], value);
        vals[index] = newValue;
        return newValue;
    }


    @Override
    public long addValue(float key, long value) {
        int k = Float.floatToIntBits(key);
        int index = insert(k, value);
        if (index < 0) {
            // key was absent
            return value;
        } else {
            // key is present
            long[] vals = values;
            long newValue = vals[index] + value;
            vals[index] = newValue;
            return newValue;
        }
    }

    @Override
    public long addValue(float key, long addition, long defaultValue) {
        int k = Float.floatToIntBits(key);
        long value = defaultValue + addition;
        int index = insert(k, value);
        if (index < 0) {
            // key was absent
            return value;
        } else {
            // key is present
            long[] vals = values;
            long newValue = vals[index] + addition;
            vals[index] = newValue;
            return newValue;
        }
    }


    @Override
    public void putAll(@Nonnull Map<? extends Float, ? extends Long> m) {
        CommonFloatLongMapOps.putAll(this, m);
    }


    @Override
    public Long replace(Float key, Long value) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            long[] vals = values;
            long oldValue = vals[index];
            vals[index] = value;
            return oldValue;
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public long replace(float key, long value) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            long[] vals = values;
            long oldValue = vals[index];
            vals[index] = value;
            return oldValue;
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public boolean replace(Float key, Long oldValue, Long newValue) {
        return replace(key.floatValue(),
                oldValue.longValue(),
                newValue.longValue());
    }

    @Override
    public boolean replace(float key, long oldValue, long newValue) {
        int k = Float.floatToIntBits(key);
        int index = index(k);
        if (index >= 0) {
            // key is present
            long[] vals = values;
            if (vals[index] == oldValue) {
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
            BiFunction<? super Float, ? super Long, ? extends Long> function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        int[] keys = set;
        long[] vals = values;
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
    public void replaceAll(FloatLongToLongFunction function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        int[] keys = set;
        long[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            int key;
            if ((key = keys[i]) < FREE_BITS) {
                vals[i] = function.applyAsLong(Float.intBitsToFloat(key), vals[i]);
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
    public Long remove(Object key) {
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
    public long remove(float key) {
        throw new java.lang.UnsupportedOperationException();
    }



    @Override
    public boolean remove(Object key, Object value) {
        return remove(((Float) key).floatValue(),
                ((Long) value).longValue()
                );
    }

    @Override
    public boolean remove(float key, long value) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public boolean removeIf(FloatLongPredicate filter) {
        throw new java.lang.UnsupportedOperationException();
    }






    class EntryView extends AbstractSetView<Map.Entry<Float, Long>>
            implements HashObjSet<Map.Entry<Float, Long>>,
            InternalObjCollectionOps<Map.Entry<Float, Long>> {

        @Nonnull
        @Override
        public Equivalence<Entry<Float, Long>> equivalence() {
            return Equivalence.entryEquivalence(
                    Equivalence.<Float>defaultEquality()
                    ,
                    Equivalence.<Long>defaultEquality()
                    
            );
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return UpdatableLHashSeparateKVFloatLongMapGO.this.hashConfig();
        }


        @Override
        public int size() {
            return UpdatableLHashSeparateKVFloatLongMapGO.this.size();
        }

        @Override
        public double currentLoad() {
            return UpdatableLHashSeparateKVFloatLongMapGO.this.currentLoad();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Float, Long> e = (Map.Entry<Float, Long>) o;
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
            long[] vals = values;
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
            long[] vals = values;
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
        public final void forEach(@Nonnull Consumer<? super Map.Entry<Float, Long>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            int[] keys = set;
            long[] vals = values;
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
        public boolean forEachWhile(@Nonnull  Predicate<? super Map.Entry<Float, Long>> predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            int[] keys = set;
            long[] vals = values;
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
        public ObjIterator<Map.Entry<Float, Long>> iterator() {
            int mc = modCount();
            return new NoRemovedEntryIterator(mc);
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Float, Long>> cursor() {
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
            long[] vals = values;
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
            long[] vals = values;
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
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Float, Long>> c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            int[] keys = set;
            long[] vals = values;
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
            return UpdatableLHashSeparateKVFloatLongMapGO.this.hashCode();
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            int[] keys = set;
            long[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                int key;
                if ((key = keys[i]) < FREE_BITS) {
                    sb.append(' ');
                    sb.append(Float.intBitsToFloat(key));
                    sb.append('=');
                    sb.append(vals[i]);
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
            return UpdatableLHashSeparateKVFloatLongMapGO.this.shrink();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Float, Long> e = (Map.Entry<Float, Long>) o;
                float key = e.getKey();
                long value = e.getValue();
                return UpdatableLHashSeparateKVFloatLongMapGO.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        public final boolean removeIf(@Nonnull Predicate<? super Map.Entry<Float, Long>> filter) {
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
            UpdatableLHashSeparateKVFloatLongMapGO.this.clear();
        }
    }


    abstract class FloatLongEntry extends AbstractEntry<Float, Long> {

        abstract int key();

        @Override
        public final Float getKey() {
            return Float.intBitsToFloat(key());
        }

        abstract long value();

        @Override
        public final Long getValue() {
            return value();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            Map.Entry e2;
            int k2;
            long v2;
            try {
                e2 = (Map.Entry) o;
                k2 = Float.floatToIntBits((Float) e2.getKey());
                v2 = (Long) e2.getValue();
                return key() == k2
                        
                        &&
                        value() == v2
                        ;
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
                    Primitives.hashCode(value())
                    ;
        }
    }


    class MutableEntry extends FloatLongEntry {
        final int modCount;
        private final int index;
        final int key;
        private long value;

        MutableEntry(int modCount, int index, int key, long value) {
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
        public long value() {
            return value;
        }

        @Override
        public Long setValue(Long newValue) {
            if (modCount != modCount())
                throw new IllegalStateException();
            long oldValue = value;
            long unwrappedNewValue = newValue;
            value = unwrappedNewValue;
            updateValueInTable(unwrappedNewValue);
            return oldValue;
        }

        void updateValueInTable(long newValue) {
            values[index] = newValue;
        }
    }



    class ReusableEntry extends FloatLongEntry {
        private int key;
        private long value;

        ReusableEntry with(int key, long value) {
            this.key = key;
            this.value = value;
            return this;
        }

        @Override
        public int key() {
            return key;
        }

        @Override
        public long value() {
            return value;
        }
    }


    class ValueView extends AbstractLongValueView {


        @Override
        public int size() {
            return UpdatableLHashSeparateKVFloatLongMapGO.this.size();
        }

        @Override
        public boolean shrink() {
            return UpdatableLHashSeparateKVFloatLongMapGO.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return UpdatableLHashSeparateKVFloatLongMapGO.this.containsValue(o);
        }

        @Override
        public boolean contains(long v) {
            return UpdatableLHashSeparateKVFloatLongMapGO.this.containsValue(v);
        }



        @Override
        public void forEach(Consumer<? super Long> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            int[] keys = set;
            long[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    action.accept(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public void forEach(LongConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            int[] keys = set;
            long[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    action.accept(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public boolean forEachWhile(LongPredicate predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            int[] keys = set;
            long[] vals = values;
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
        public boolean allContainingIn(LongCollection c) {
            if (isEmpty())
                return true;
            boolean containsAll = true;
            int mc = modCount();
            int[] keys = set;
            long[] vals = values;
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
        public boolean reverseAddAllTo(LongCollection c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            int[] keys = set;
            long[] vals = values;
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
        public boolean reverseRemoveAllFrom(LongSet s) {
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            int[] keys = set;
            long[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    changed |= s.removeLong(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }



        @Override
        @Nonnull
        public LongIterator iterator() {
            int mc = modCount();
            return new NoRemovedValueIterator(mc);
        }

        @Nonnull
        @Override
        public LongCursor cursor() {
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
            long[] vals = values;
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
            long[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    a[resultIndex++] = (T) Long.valueOf(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public long[] toLongArray() {
            int size = size();
            long[] result = new long[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            int mc = modCount();
            int[] keys = set;
            long[] vals = values;
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
        public long[] toArray(long[] a) {
            int size = size();
            if (a.length < size)
                a = new long[size];
            if (size == 0) {
                if (a.length > 0)
                    a[0] = 0L;
                return a;
            }
            int resultIndex = 0;
            int mc = modCount();
            int[] keys = set;
            long[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    a[resultIndex++] = vals[i];
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = 0L;
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
            long[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] < FREE_BITS) {
                    sb.append(' ').append(vals[i]).append(',');
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
            return removeLong(( Long ) o);
        }

        @Override
        public boolean removeLong(long v) {
            return removeValue(v);
        }



        @Override
        public void clear() {
            UpdatableLHashSeparateKVFloatLongMapGO.this.clear();
        }

        
        public boolean removeIf(Predicate<? super Long> filter) {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(LongPredicate filter) {
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



    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Float, Long>> {
        final int[] keys;
        final long[] vals;
        int expectedModCount;
        int nextIndex;
        MutableEntry next;

        NoRemovedEntryIterator(int mc) {
            expectedModCount = mc;
            int[] keys = this.keys = set;
            long[] vals = this.vals = values;
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
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Float, Long>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] keys = this.keys;
            long[] vals = this.vals;
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
        public Map.Entry<Float, Long> next() {
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


    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Float, Long>> {
        final int[] keys;
        final long[] vals;
        int expectedModCount;
        int index;
        int curKey;
        long curValue;

        NoRemovedEntryCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            curKey = FREE_BITS;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Float, Long>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] keys = this.keys;
            long[] vals = this.vals;
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
        public Map.Entry<Float, Long> elem() {
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




    class NoRemovedValueIterator implements LongIterator {
        final int[] keys;
        final long[] vals;
        int expectedModCount;
        int nextIndex;
        long next;

        NoRemovedValueIterator(int mc) {
            expectedModCount = mc;
            int[] keys = this.keys = set;
            long[] vals = this.vals = values;
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
        public long nextLong() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                if (expectedModCount == modCount()) {
                    int[] keys = this.keys;
                    long prev = next;
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
        public void forEachRemaining(Consumer<? super Long> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] keys = this.keys;
            long[] vals = this.vals;
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
        public void forEachRemaining(LongConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] keys = this.keys;
            long[] vals = this.vals;
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
        public Long next() {
            return nextLong();
        }

        @Override
        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }
    }


    class NoRemovedValueCursor implements LongCursor {
        final int[] keys;
        final long[] vals;
        int expectedModCount;
        int index;
        int curKey;
        long curValue;

        NoRemovedValueCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            curKey = FREE_BITS;
        }

        @Override
        public void forEachForward(LongConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] keys = this.keys;
            long[] vals = this.vals;
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
        public long elem() {
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



    class NoRemovedMapCursor implements FloatLongCursor {
        final int[] keys;
        final long[] vals;
        int expectedModCount;
        int index;
        int curKey;
        long curValue;

        NoRemovedMapCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            curKey = FREE_BITS;
        }

        @Override
        public void forEachForward(FloatLongConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] keys = this.keys;
            long[] vals = this.vals;
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
        public long value() {
            if (curKey != FREE_BITS) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(long value) {
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
