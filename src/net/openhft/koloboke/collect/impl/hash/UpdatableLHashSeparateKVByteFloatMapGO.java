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
import net.openhft.koloboke.function.BytePredicate;
import net.openhft.koloboke.function.ByteFloatConsumer;
import net.openhft.koloboke.function.ByteFloatPredicate;
import net.openhft.koloboke.function.ByteFloatToFloatFunction;
import net.openhft.koloboke.function.ByteToFloatFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.openhft.koloboke.function.FloatBinaryOperator;
import net.openhft.koloboke.function.FloatConsumer;
import net.openhft.koloboke.function.FloatPredicate;

import javax.annotation.Nonnull;
import java.util.*;


public class UpdatableLHashSeparateKVByteFloatMapGO
        extends UpdatableLHashSeparateKVByteFloatMapSO {

    @Override
    final void copy(SeparateKVByteFloatLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    @Override
    final void move(SeparateKVByteFloatLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }


    @Override
    public float defaultValue() {
        return 0.0f;
    }

    @Override
    public boolean containsEntry(byte key, float value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return values[index] == Float.floatToIntBits(value);
        } else {
            // key is absent
            return false;
        }
    }

    @Override
    public boolean containsEntry(byte key, int value) {
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
    public Float get(Object key) {
        int index = index((Byte) key);
        if (index >= 0) {
            // key is present
            return Float.intBitsToFloat(values[index]);
        } else {
            // key is absent
            return null;
        }
    }

    

    @Override
    public float get(byte key) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return Float.intBitsToFloat(values[index]);
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Float getOrDefault(Object key, Float defaultValue) {
        int index = index((Byte) key);
        if (index >= 0) {
            // key is present
            return Float.intBitsToFloat(values[index]);
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public float getOrDefault(byte key, float defaultValue) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            return Float.intBitsToFloat(values[index]);
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public void forEach(BiConsumer<? super Byte, ? super Float> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                action.accept(key, Float.intBitsToFloat(vals[i]));
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void forEach(ByteFloatConsumer action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                action.accept(key, Float.intBitsToFloat(vals[i]));
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public boolean forEachWhile(ByteFloatPredicate predicate) {
        if (predicate == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return true;
        boolean terminated = false;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                if (!predicate.test(key, Float.intBitsToFloat(vals[i]))) {
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
    public ByteFloatCursor cursor() {
        int mc = modCount();
        return new NoRemovedMapCursor(mc);
    }


    @Override
    public boolean containsAllEntries(Map<?, ?> m) {
        return CommonByteFloatMapOps.containsAllEntries(this, m);
    }

    @Override
    public boolean allEntriesContainingIn(InternalByteFloatMapOps m) {
        if (isEmpty())
            return true;
        boolean containsAll = true;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
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
    public void reversePutAllTo(InternalByteFloatMapOps m) {
        if (isEmpty())
            return;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                m.justPut(key, vals[i]);
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    @Nonnull
    public HashObjSet<Map.Entry<Byte, Float>> entrySet() {
        return new EntryView();
    }

    @Override
    @Nonnull
    public FloatCollection values() {
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
        byte free = freeValue;
        byte[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                hashCode += key ^ vals[i];
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
        byte free = freeValue;
        byte[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                sb.append(' ');
                sb.append(key);
                sb.append('=');
                sb.append(Float.intBitsToFloat(vals[i]));
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
        byte free = freeValue;
        byte[] keys = set;
        int[] vals = values;
        initForRehash(newCapacity);
        mc++; // modCount is incremented in initForRehash()
        byte[] newKeys = set;
        int capacityMask = newKeys.length - 1;
        int[] newVals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                int index;
                if (newKeys[index = SeparateKVByteKeyMixing.mix(key) & capacityMask] != free) {
                    while (true) {
                        if (newKeys[(index = (index - 1) & capacityMask)] == free) {
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
    public Float put(Byte key, Float value) {
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            int[] vals = values;
            float prevValue = Float.intBitsToFloat(vals[index]);
            vals[index] = Float.floatToIntBits(value);
            return prevValue;
        }
    }

    @Override
    public float put(byte key, float value) {
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return defaultValue();
        } else {
            // key is present
            int[] vals = values;
            float prevValue = Float.intBitsToFloat(vals[index]);
            vals[index] = Float.floatToIntBits(value);
            return prevValue;
        }
    }

    @Override
    public Float putIfAbsent(Byte key, Float value) {
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return null;
        } else {
            // key is present
            return Float.intBitsToFloat(values[index]);
        }
    }

    @Override
    public float putIfAbsent(byte key, float value) {
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return defaultValue();
        } else {
            // key is present
            return Float.intBitsToFloat(values[index]);
        }
    }

    @Override
    public void justPut(byte key, float value) {
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return;
        } else {
            // key is present
            values[index] = Float.floatToIntBits(value);
            return;
        }
    }

    @Override
    public void justPut(byte key, int value) {
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
    public Float compute(Byte key,
            BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
        byte k = key;
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        byte free;
        if (k == (free = freeValue)) {
            free = changeFree();
        }
        byte[] keys = set;
        int[] vals = values;
        int capacityMask, index;
        byte cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVByteKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) != k) {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        break keyPresent;
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            Float newValue = remappingFunction.apply(k, null);
            if (newValue != null) {
                incrementModCount();
                keys[index] = k;
                vals[index] = Float.floatToIntBits(newValue);
                postInsertHook();
                return newValue;
            } else {
                return null;
            }
        }
        // key is present
        Float newValue = remappingFunction.apply(k, Float.intBitsToFloat(vals[index]));
        if (newValue != null) {
            vals[index] = Float.floatToIntBits(newValue);
            return newValue;
        } else {
            throw new java.lang.UnsupportedOperationException("Compute operation of updatable map doesn't support removals");
        }
    }


    @Override
    public float compute(byte key, ByteFloatToFloatFunction remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        byte free;
        if (key == (free = freeValue)) {
            free = changeFree();
        }
        byte[] keys = set;
        int[] vals = values;
        int capacityMask, index;
        byte cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVByteKeyMixing.mix(key) & (capacityMask = keys.length - 1)]) != key) {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                        break keyPresent;
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            float newValue = remappingFunction.applyAsFloat(key, defaultValue());
            incrementModCount();
            keys[index] = key;
            vals[index] = Float.floatToIntBits(newValue);
            postInsertHook();
            return newValue;
        }
        // key is present
        float newValue = remappingFunction.applyAsFloat(key, Float.intBitsToFloat(vals[index]));
        vals[index] = Float.floatToIntBits(newValue);
        return newValue;
    }


    @Override
    public Float computeIfAbsent(Byte key,
            Function<? super Byte, ? extends Float> mappingFunction) {
        byte k = key;
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        byte free;
        if (k == (free = freeValue)) {
            free = changeFree();
        }
        byte[] keys = set;
        int[] vals = values;
        int capacityMask, index;
        byte cur;
        if ((cur = keys[index = SeparateKVByteKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) == k) {
            // key is present
            return Float.intBitsToFloat(vals[index]);
        } else {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        // key is present
                        return Float.intBitsToFloat(vals[index]);
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            Float value = mappingFunction.apply(k);
            if (value != null) {
                incrementModCount();
                keys[index] = k;
                vals[index] = Float.floatToIntBits(value);
                postInsertHook();
                return value;
            } else {
                return null;
            }
        }
    }


    @Override
    public float computeIfAbsent(byte key, ByteToFloatFunction mappingFunction) {
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        byte free;
        if (key == (free = freeValue)) {
            free = changeFree();
        }
        byte[] keys = set;
        int[] vals = values;
        int capacityMask, index;
        byte cur;
        if ((cur = keys[index = SeparateKVByteKeyMixing.mix(key) & (capacityMask = keys.length - 1)]) == key) {
            // key is present
            return Float.intBitsToFloat(vals[index]);
        } else {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                        // key is present
                        return Float.intBitsToFloat(vals[index]);
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            float value = mappingFunction.applyAsFloat(key);
            incrementModCount();
            keys[index] = key;
            vals[index] = Float.floatToIntBits(value);
            postInsertHook();
            return value;
        }
    }


    @Override
    public Float computeIfPresent(Byte key,
            BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
        byte k = key;
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(k);
        if (index >= 0) {
            // key is present
            int[] vals = values;
            Float newValue = remappingFunction.apply(k, Float.intBitsToFloat(vals[index]));
            if (newValue != null) {
                vals[index] = Float.floatToIntBits(newValue);
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
    public float computeIfPresent(byte key, ByteFloatToFloatFunction remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        int index = index(key);
        if (index >= 0) {
            // key is present
            int[] vals = values;
            float newValue = remappingFunction.applyAsFloat(key, Float.intBitsToFloat(vals[index]));
            vals[index] = Float.floatToIntBits(newValue);
            return newValue;
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Float merge(Byte key, Float value,
            BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        byte k = key;
        if (value == null)
            throw new java.lang.NullPointerException();
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        byte free;
        if (k == (free = freeValue)) {
            free = changeFree();
        }
        byte[] keys = set;
        int[] vals = values;
        int capacityMask, index;
        byte cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVByteKeyMixing.mix(k) & (capacityMask = keys.length - 1)]) != k) {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                        break keyPresent;
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            incrementModCount();
            keys[index] = k;
            vals[index] = Float.floatToIntBits(value);
            postInsertHook();
            return value;
        }
        // key is present
        Float newValue = remappingFunction.apply(Float.intBitsToFloat(vals[index]), value);
        if (newValue != null) {
            vals[index] = Float.floatToIntBits(newValue);
            return newValue;
        } else {
            throw new java.lang.UnsupportedOperationException("Merge operation of updatable map doesn't support removals");
        }
    }


    @Override
    public float merge(byte key, float value, FloatBinaryOperator remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        byte free;
        if (key == (free = freeValue)) {
            free = changeFree();
        }
        byte[] keys = set;
        int[] vals = values;
        int capacityMask, index;
        byte cur;
        keyPresent:
        if ((cur = keys[index = SeparateKVByteKeyMixing.mix(key) & (capacityMask = keys.length - 1)]) != key) {
            keyAbsent:
            if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                        break keyPresent;
                    } else if (cur == free) {
                        break keyAbsent;
                    }
                }
            }
            // key is absent
            incrementModCount();
            keys[index] = key;
            vals[index] = Float.floatToIntBits(value);
            postInsertHook();
            return value;
        }
        // key is present
        float newValue = remappingFunction.applyAsFloat(Float.intBitsToFloat(vals[index]), value);
        vals[index] = Float.floatToIntBits(newValue);
        return newValue;
    }


    @Override
    public float addValue(byte key, float value) {
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return value;
        } else {
            // key is present
            int[] vals = values;
            float newValue = Float.intBitsToFloat(vals[index]) + value;
            vals[index] = Float.floatToIntBits(newValue);
            return newValue;
        }
    }

    @Override
    public float addValue(byte key, float addition, float defaultValue) {
        float value = defaultValue + addition;
        int index = insert(key, Float.floatToIntBits(value));
        if (index < 0) {
            // key was absent
            return value;
        } else {
            // key is present
            int[] vals = values;
            float newValue = Float.intBitsToFloat(vals[index]) + addition;
            vals[index] = Float.floatToIntBits(newValue);
            return newValue;
        }
    }


    @Override
    public void putAll(@Nonnull Map<? extends Byte, ? extends Float> m) {
        CommonByteFloatMapOps.putAll(this, m);
    }


    @Override
    public Float replace(Byte key, Float value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            int[] vals = values;
            float oldValue = Float.intBitsToFloat(vals[index]);
            vals[index] = Float.floatToIntBits(value);
            return oldValue;
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public float replace(byte key, float value) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            int[] vals = values;
            float oldValue = Float.intBitsToFloat(vals[index]);
            vals[index] = Float.floatToIntBits(value);
            return oldValue;
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public boolean replace(Byte key, Float oldValue, Float newValue) {
        return replace(key.byteValue(),
                oldValue.floatValue(),
                newValue.floatValue());
    }

    @Override
    public boolean replace(byte key, float oldValue, float newValue) {
        int index = index(key);
        if (index >= 0) {
            // key is present
            int[] vals = values;
            if (vals[index] == Float.floatToIntBits(oldValue)) {
                vals[index] = Float.floatToIntBits(newValue);
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
            BiFunction<? super Byte, ? super Float, ? extends Float> function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                vals[i] = Float.floatToIntBits(function.apply(key, Float.intBitsToFloat(vals[i])));
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void replaceAll(ByteFloatToFloatFunction function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        byte free = freeValue;
        byte[] keys = set;
        int[] vals = values;
        for (int i = keys.length - 1; i >= 0; i--) {
            byte key;
            if ((key = keys[i]) != free) {
                vals[i] = Float.floatToIntBits(function.applyAsFloat(key, Float.intBitsToFloat(vals[i])));
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
    public Float remove(Object key) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public boolean justRemove(byte key) {
        throw new java.lang.UnsupportedOperationException();
    }



    

    @Override
    public float remove(byte key) {
        throw new java.lang.UnsupportedOperationException();
    }



    @Override
    public boolean remove(Object key, Object value) {
        return remove(((Byte) key).byteValue(),
                ((Float) value).floatValue()
                );
    }

    @Override
    public boolean remove(byte key, float value) {
        throw new java.lang.UnsupportedOperationException();
    }


    @Override
    public boolean removeIf(ByteFloatPredicate filter) {
        throw new java.lang.UnsupportedOperationException();
    }






    class EntryView extends AbstractSetView<Map.Entry<Byte, Float>>
            implements HashObjSet<Map.Entry<Byte, Float>>,
            InternalObjCollectionOps<Map.Entry<Byte, Float>> {

        @Nonnull
        @Override
        public Equivalence<Entry<Byte, Float>> equivalence() {
            return Equivalence.entryEquivalence(
                    Equivalence.<Byte>defaultEquality()
                    ,
                    Equivalence.<Float>defaultEquality()
                    
            );
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return UpdatableLHashSeparateKVByteFloatMapGO.this.hashConfig();
        }


        @Override
        public int size() {
            return UpdatableLHashSeparateKVByteFloatMapGO.this.size();
        }

        @Override
        public double currentLoad() {
            return UpdatableLHashSeparateKVByteFloatMapGO.this.currentLoad();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Byte, Float> e = (Map.Entry<Byte, Float>) o;
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
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
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
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
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
        public final void forEach(@Nonnull Consumer<? super Map.Entry<Byte, Float>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
                    action.accept(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public boolean forEachWhile(@Nonnull  Predicate<? super Map.Entry<Byte, Float>> predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
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
        public ObjIterator<Map.Entry<Byte, Float>> iterator() {
            int mc = modCount();
            return new NoRemovedEntryIterator(mc);
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Byte, Float>> cursor() {
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
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
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
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
                    changed |= s.remove(e.with(key, vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Byte, Float>> c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
                    changed |= c.add(new MutableEntry(mc, i, key, vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        public int hashCode() {
            return UpdatableLHashSeparateKVByteFloatMapGO.this.hashCode();
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
                    sb.append(' ');
                    sb.append(key);
                    sb.append('=');
                    sb.append(Float.intBitsToFloat(vals[i]));
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
            return UpdatableLHashSeparateKVByteFloatMapGO.this.shrink();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Byte, Float> e = (Map.Entry<Byte, Float>) o;
                byte key = e.getKey();
                float value = e.getValue();
                return UpdatableLHashSeparateKVByteFloatMapGO.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        public final boolean removeIf(@Nonnull Predicate<? super Map.Entry<Byte, Float>> filter) {
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
            UpdatableLHashSeparateKVByteFloatMapGO.this.clear();
        }
    }


    abstract class ByteFloatEntry extends AbstractEntry<Byte, Float> {

        abstract byte key();

        @Override
        public final Byte getKey() {
            return key();
        }

        abstract int value();

        @Override
        public final Float getValue() {
            return Float.intBitsToFloat(value());
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            Map.Entry e2;
            byte k2;
            int v2;
            try {
                e2 = (Map.Entry) o;
                k2 = (Byte) e2.getKey();
                v2 = Float.floatToIntBits((Float) e2.getValue());
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


    class MutableEntry extends ByteFloatEntry {
        final int modCount;
        private final int index;
        final byte key;
        private int value;

        MutableEntry(int modCount, int index, byte key, int value) {
            this.modCount = modCount;
            this.index = index;
            this.key = key;
            this.value = value;
        }

        @Override
        public byte key() {
            return key;
        }

        @Override
        public int value() {
            return value;
        }

        @Override
        public Float setValue(Float newValue) {
            if (modCount != modCount())
                throw new IllegalStateException();
            float oldValue = Float.intBitsToFloat(value);
            int unwrappedNewValue = Float.floatToIntBits(newValue);
            value = unwrappedNewValue;
            updateValueInTable(unwrappedNewValue);
            return oldValue;
        }

        void updateValueInTable(int newValue) {
            values[index] = newValue;
        }
    }



    class ReusableEntry extends ByteFloatEntry {
        private byte key;
        private int value;

        ReusableEntry with(byte key, int value) {
            this.key = key;
            this.value = value;
            return this;
        }

        @Override
        public byte key() {
            return key;
        }

        @Override
        public int value() {
            return value;
        }
    }


    class ValueView extends AbstractFloatValueView {


        @Override
        public int size() {
            return UpdatableLHashSeparateKVByteFloatMapGO.this.size();
        }

        @Override
        public boolean shrink() {
            return UpdatableLHashSeparateKVByteFloatMapGO.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return UpdatableLHashSeparateKVByteFloatMapGO.this.containsValue(o);
        }

        @Override
        public boolean contains(float v) {
            return UpdatableLHashSeparateKVByteFloatMapGO.this.containsValue(v);
        }

        @Override
        public boolean contains(int bits) {
            return UpdatableLHashSeparateKVByteFloatMapGO.this.containsValue(bits);
        }


        @Override
        public void forEach(Consumer<? super Float> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(Float.intBitsToFloat(vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public void forEach(FloatConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(Float.intBitsToFloat(vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public boolean forEachWhile(FloatPredicate predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    if (!predicate.test(Float.intBitsToFloat(vals[i]))) {
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
        public boolean allContainingIn(FloatCollection c) {
            if (c instanceof InternalFloatCollectionOps)
                return allContainingIn((InternalFloatCollectionOps) c);
            if (isEmpty())
                return true;
            boolean containsAll = true;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    if (!c.contains(Float.intBitsToFloat(vals[i]))) {
                        containsAll = false;
                        break;
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return containsAll;
        }

        private boolean allContainingIn(InternalFloatCollectionOps c) {
            if (isEmpty())
                return true;
            boolean containsAll = true;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
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
        public boolean reverseAddAllTo(FloatCollection c) {
            if (c instanceof InternalFloatCollectionOps)
                return reverseAddAllTo((InternalFloatCollectionOps) c);
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    changed |= c.add(Float.intBitsToFloat(vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        private boolean reverseAddAllTo(InternalFloatCollectionOps c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    changed |= c.add(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public boolean reverseRemoveAllFrom(FloatSet s) {
            if (s instanceof InternalFloatCollectionOps)
                return reverseRemoveAllFrom((InternalFloatCollectionOps) s);
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    changed |= s.removeFloat(Float.intBitsToFloat(vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        private boolean reverseRemoveAllFrom(InternalFloatCollectionOps s) {
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    changed |= s.removeFloat(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        @Override
        @Nonnull
        public FloatIterator iterator() {
            int mc = modCount();
            return new NoRemovedValueIterator(mc);
        }

        @Nonnull
        @Override
        public FloatCursor cursor() {
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
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    result[resultIndex++] = Float.intBitsToFloat(vals[i]);
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
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    a[resultIndex++] = (T) Float.valueOf(Float.intBitsToFloat(vals[i]));
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = null;
            return a;
        }

        @Override
        public float[] toFloatArray() {
            int size = size();
            float[] result = new float[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    result[resultIndex++] = Float.intBitsToFloat(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return result;
        }

        @Override
        public float[] toArray(float[] a) {
            int size = size();
            if (a.length < size)
                a = new float[size];
            if (size == 0) {
                if (a.length > 0)
                    a[0] = 0.0f;
                return a;
            }
            int resultIndex = 0;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    a[resultIndex++] = Float.intBitsToFloat(vals[i]);
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = 0.0f;
            return a;
        }


        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            byte free = freeValue;
            byte[] keys = set;
            int[] vals = values;
            for (int i = keys.length - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    sb.append(' ').append(Float.intBitsToFloat(vals[i])).append(',');
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
            return removeFloat(( Float ) o);
        }

        @Override
        public boolean removeFloat(float v) {
            return removeValue(v);
        }

        @Override
        public boolean removeFloat(int bits) {
            return removeValue(bits);
        }


        @Override
        public void clear() {
            UpdatableLHashSeparateKVByteFloatMapGO.this.clear();
        }

        
        public boolean removeIf(Predicate<? super Float> filter) {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(FloatPredicate filter) {
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



    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Byte, Float>> {
        final byte[] keys;
        final int[] vals;
        final byte free;
        int expectedModCount;
        int nextIndex;
        MutableEntry next;

        NoRemovedEntryIterator(int mc) {
            expectedModCount = mc;
            byte[] keys = this.keys = set;
            int[] vals = this.vals = values;
            byte free = this.free = freeValue;
            int nextI = keys.length;
            while (--nextI >= 0) {
                byte key;
                if ((key = keys[nextI]) != free) {
                    next = new MutableEntry(mc, nextI, key, vals[nextI]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Byte, Float>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            byte[] keys = this.keys;
            int[] vals = this.vals;
            byte free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
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
        public Map.Entry<Byte, Float> next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                int mc;
                if ((mc = expectedModCount) == modCount()) {
                    byte[] keys = this.keys;
                    byte free = this.free;
                    MutableEntry prev = next;
                    while (--nextI >= 0) {
                        byte key;
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
            throw new java.lang.UnsupportedOperationException();
        }
    }


    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Byte, Float>> {
        final byte[] keys;
        final int[] vals;
        final byte free;
        int expectedModCount;
        int index;
        byte curKey;
        int curValue;

        NoRemovedEntryCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            byte free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Byte, Float>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            byte[] keys = this.keys;
            int[] vals = this.vals;
            byte free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                byte key;
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
        public Map.Entry<Byte, Float> elem() {
            byte curKey;
            if ((curKey = this.curKey) != free) {
                return new MutableEntry(expectedModCount, index, curKey, curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                byte[] keys = this.keys;
                byte free = this.free;
                for (int i = index - 1; i >= 0; i--) {
                    byte key;
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
            throw new java.lang.UnsupportedOperationException();
        }
    }




    class NoRemovedValueIterator implements FloatIterator {
        final byte[] keys;
        final int[] vals;
        final byte free;
        int expectedModCount;
        int nextIndex;
        float next;

        NoRemovedValueIterator(int mc) {
            expectedModCount = mc;
            byte[] keys = this.keys = set;
            int[] vals = this.vals = values;
            byte free = this.free = freeValue;
            int nextI = keys.length;
            while (--nextI >= 0) {
                if (keys[nextI] != free) {
                    next = Float.intBitsToFloat(vals[nextI]);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public float nextFloat() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                if (expectedModCount == modCount()) {
                    byte[] keys = this.keys;
                    byte free = this.free;
                    float prev = next;
                    while (--nextI >= 0) {
                        if (keys[nextI] != free) {
                            next = Float.intBitsToFloat(vals[nextI]);
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
        public void forEachRemaining(Consumer<? super Float> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            byte[] keys = this.keys;
            int[] vals = this.vals;
            byte free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(Float.intBitsToFloat(vals[i]));
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            nextIndex = -1;
        }

        @Override
        public void forEachRemaining(FloatConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            byte[] keys = this.keys;
            int[] vals = this.vals;
            byte free = this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(Float.intBitsToFloat(vals[i]));
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
        public Float next() {
            return nextFloat();
        }

        @Override
        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }
    }


    class NoRemovedValueCursor implements FloatCursor {
        final byte[] keys;
        final int[] vals;
        final byte free;
        int expectedModCount;
        int index;
        byte curKey;
        int curValue;

        NoRemovedValueCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            byte free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(FloatConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            byte[] keys = this.keys;
            int[] vals = this.vals;
            byte free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                if (keys[i] != free) {
                    action.accept(Float.intBitsToFloat(vals[i]));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public float elem() {
            if (curKey != free) {
                return Float.intBitsToFloat(curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                byte[] keys = this.keys;
                byte free = this.free;
                for (int i = index - 1; i >= 0; i--) {
                    byte key;
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
            throw new java.lang.UnsupportedOperationException();
        }
    }



    class NoRemovedMapCursor implements ByteFloatCursor {
        final byte[] keys;
        final int[] vals;
        final byte free;
        int expectedModCount;
        int index;
        byte curKey;
        int curValue;

        NoRemovedMapCursor(int mc) {
            expectedModCount = mc;
            this.keys = set;
            index = keys.length;
            vals = values;
            byte free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(ByteFloatConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            byte[] keys = this.keys;
            int[] vals = this.vals;
            byte free = this.free;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                byte key;
                if ((key = keys[i]) != free) {
                    action.accept(key, Float.intBitsToFloat(vals[i]));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public byte key() {
            byte curKey;
            if ((curKey = this.curKey) != free) {
                return curKey;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public float value() {
            if (curKey != free) {
                return Float.intBitsToFloat(curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(float value) {
            if (curKey != free) {
                if (expectedModCount == modCount()) {
                    vals[index] = Float.floatToIntBits(value);
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
                byte[] keys = this.keys;
                byte free = this.free;
                for (int i = index - 1; i >= 0; i--) {
                    byte key;
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
            throw new java.lang.UnsupportedOperationException();
        }
    }
}
