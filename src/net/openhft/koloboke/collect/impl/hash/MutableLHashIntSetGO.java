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
import net.openhft.koloboke.collect.impl.*;
import net.openhft.koloboke.collect.set.hash.HashIntSet;
import javax.annotation.Nonnull;

import java.util.*;


public class MutableLHashIntSetGO extends MutableIntLHashSetSO
        implements HashIntSet, InternalIntCollectionOps {

    @Override
    final void copy(SeparateKVIntLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    @Override
    final void move(SeparateKVIntLHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    public int hashCode() {
        return setHashCode();
    }

    @Override
    public String toString() {
        return setToString();
    }

    @Override
    public boolean equals(Object obj) {
        return CommonSetOps.equals(this, obj);
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return CommonIntCollectionOps.containsAll(this, c);
    }

    @Nonnull
    @Override
    public IntCursor cursor() {
        return setCursor();
    }


    @Override
    public boolean add(Integer e) {
        return add(e.intValue());
    }

    @Override
    public boolean add(int key) {
        int free;
        if (key == (free = freeValue)) {
            free = changeFree();
        }
        int[] keys = set;
        int capacityMask, index;
        int cur;
        keyAbsent:
        if ((cur = keys[index = SeparateKVIntKeyMixing.mix(key) & (capacityMask = keys.length - 1)]) != free) {
            if (cur == key) {
                // key is present
                return false;
            } else {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == free) {
                        break keyAbsent;
                    } else if (cur == key) {
                        // key is present
                        return false;
                    }
                }
            }
        }
        // key is absent
        incrementModCount();
        keys[index] = key;
        postInsertHook();
        return true;
    }


    @Override
    public boolean addAll(@Nonnull Collection<? extends Integer> c) {
        return CommonIntCollectionOps.addAll(this, c);
    }

    @Override
    public boolean remove(Object key) {
        return removeInt(((Integer) key).intValue());
    }


    @Override
    boolean justRemove(int key) {
        return removeInt(key);
    }

    @Override
    public boolean removeInt(int key) {
        int free;
        if (key != (free = freeValue)) {
            int[] keys = set;
            int capacityMask = keys.length - 1;
            int index;
            int cur;
            keyPresent:
            if ((cur = keys[index = SeparateKVIntKeyMixing.mix(key) & capacityMask]) != key) {
                if (cur == free) {
                    // key is absent
                    return false;
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent
                            return false;
                        }
                    }
                }
            }
            // key is present
            incrementModCount();
            int indexToRemove = index;
            int indexToShift = indexToRemove;
            int shiftDistance = 1;
            while (true) {
                indexToShift = (indexToShift - 1) & capacityMask;
                int keyToShift;
                if ((keyToShift = keys[indexToShift]) == free) {
                    break;
                }
                if (((SeparateKVIntKeyMixing.mix(keyToShift) - indexToShift) & capacityMask) >= shiftDistance) {
                    keys[indexToRemove] = keyToShift;
                    indexToRemove = indexToShift;
                    shiftDistance = 1;
                } else {
                    shiftDistance++;
                    if (indexToShift == 1 + index) {
                        throw new java.util.ConcurrentModificationException();
                    }
                }
            }
            keys[indexToRemove] = free;
            postRemoveHook();
            return true;
        } else {
            // key is absent
            return false;
        }
    }



    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        if (c instanceof IntCollection) {
            if (c instanceof InternalIntCollectionOps) {
                InternalIntCollectionOps c2 = (InternalIntCollectionOps) c;
                if (c2.size() < this.size()) {
                    
                    return c2.reverseRemoveAllFrom(this);
                }
            }
            return removeAll(this, (IntCollection) c);
        }
        return removeAll(this, c);
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        return retainAll(this, c);
    }
}
