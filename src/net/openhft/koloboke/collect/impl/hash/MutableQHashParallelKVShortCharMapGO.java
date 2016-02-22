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
import net.openhft.koloboke.function.ShortPredicate;
import net.openhft.koloboke.function.ShortCharConsumer;
import net.openhft.koloboke.function.ShortCharPredicate;
import net.openhft.koloboke.function.ShortCharToCharFunction;
import net.openhft.koloboke.function.ShortToCharFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.openhft.koloboke.function.CharBinaryOperator;
import net.openhft.koloboke.function.CharConsumer;
import net.openhft.koloboke.function.CharPredicate;

import javax.annotation.Nonnull;
import java.util.*;


public class MutableQHashParallelKVShortCharMapGO
        extends MutableQHashParallelKVShortCharMapSO {

    
    final void copy(ParallelKVShortCharQHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.copy(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }

    
    final void move(ParallelKVShortCharQHash hash) {
        int myMC = modCount(), hashMC = hash.modCount();
        super.move(hash);
        if (myMC != modCount() || hashMC != hash.modCount())
            throw new ConcurrentModificationException();
    }


    @Override
    public char defaultValue() {
        return (char) 0;
    }

    @Override
    public boolean containsEntry(short key, char value) {
        short free;
        if (key != (free = freeValue) && key != removedValue) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) == key) {
                // key is present
                return (char) (entry >>> 16) == value;
            } else {
                if (cur == free) {
                    // key is absent, free slot
                    return false;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == key) {
                            // key is present
                            return (char) (entry >>> 16) == value;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return false;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == key) {
                            // key is present
                            return (char) (entry >>> 16) == value;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return false;
                        }
                        step += 2;
                    }
                }
            }
        } else {
            // key is absent
            return false;
        }
    }


    @Override
    public Character get(Object key) {
        short k = (Short) key;
        short free;
        if (k != (free = freeValue) && k != removedValue) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(k) % (capacity = tab.length)])) == k) {
                // key is present
                return (char) (entry >>> 16);
            } else {
                if (cur == free) {
                    // key is absent, free slot
                    return null;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == k) {
                            // key is present
                            return (char) (entry >>> 16);
                        } else if (cur == free) {
                            // key is absent, free slot
                            return null;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == k) {
                            // key is present
                            return (char) (entry >>> 16);
                        } else if (cur == free) {
                            // key is absent, free slot
                            return null;
                        }
                        step += 2;
                    }
                }
            }
        } else {
            // key is absent
            return null;
        }
    }

    

    @Override
    public char get(short key) {
        short free;
        if (key != (free = freeValue) && key != removedValue) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) == key) {
                // key is present
                return (char) (entry >>> 16);
            } else {
                if (cur == free) {
                    // key is absent, free slot
                    return defaultValue();
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == key) {
                            // key is present
                            return (char) (entry >>> 16);
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue();
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == key) {
                            // key is present
                            return (char) (entry >>> 16);
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue();
                        }
                        step += 2;
                    }
                }
            }
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Character getOrDefault(Object key, Character defaultValue) {
        short k = (Short) key;
        short free;
        if (k != (free = freeValue) && k != removedValue) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(k) % (capacity = tab.length)])) == k) {
                // key is present
                return (char) (entry >>> 16);
            } else {
                if (cur == free) {
                    // key is absent, free slot
                    return defaultValue;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == k) {
                            // key is present
                            return (char) (entry >>> 16);
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == k) {
                            // key is present
                            return (char) (entry >>> 16);
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue;
                        }
                        step += 2;
                    }
                }
            }
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public char getOrDefault(short key, char defaultValue) {
        short free;
        if (key != (free = freeValue) && key != removedValue) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) == key) {
                // key is present
                return (char) (entry >>> 16);
            } else {
                if (cur == free) {
                    // key is absent, free slot
                    return defaultValue;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == key) {
                            // key is present
                            return (char) (entry >>> 16);
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == key) {
                            // key is present
                            return (char) (entry >>> 16);
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue;
                        }
                        step += 2;
                    }
                }
            }
        } else {
            // key is absent
            return defaultValue;
        }
    }

    @Override
    public void forEach(BiConsumer<? super Short, ? super Character> action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        int entry;
        if (noRemoved()) {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    action.accept(key, (char) (entry >>> 16));
                }
            }
        } else {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    action.accept(key, (char) (entry >>> 16));
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void forEach(ShortCharConsumer action) {
        if (action == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        int entry;
        if (noRemoved()) {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    action.accept(key, (char) (entry >>> 16));
                }
            }
        } else {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    action.accept(key, (char) (entry >>> 16));
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public boolean forEachWhile(ShortCharPredicate predicate) {
        if (predicate == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return true;
        boolean terminated = false;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        int entry;
        if (noRemoved()) {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    if (!predicate.test(key, (char) (entry >>> 16))) {
                        terminated = true;
                        break;
                    }
                }
            }
        } else {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    if (!predicate.test(key, (char) (entry >>> 16))) {
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
    public ShortCharCursor cursor() {
        int mc = modCount();
        if (!noRemoved())
            return new SomeRemovedMapCursor(mc);
        return new NoRemovedMapCursor(mc);
    }


    @Override
    public boolean containsAllEntries(Map<?, ?> m) {
        return CommonShortCharMapOps.containsAllEntries(this, m);
    }

    @Override
    public boolean allEntriesContainingIn(InternalShortCharMapOps m) {
        if (isEmpty())
            return true;
        boolean containsAll = true;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        int entry;
        if (noRemoved()) {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    if (!m.containsEntry(key, (char) (entry >>> 16))) {
                        containsAll = false;
                        break;
                    }
                }
            }
        } else {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    if (!m.containsEntry(key, (char) (entry >>> 16))) {
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
    public void reversePutAllTo(InternalShortCharMapOps m) {
        if (isEmpty())
            return;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        int entry;
        if (noRemoved()) {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    m.justPut(key, (char) (entry >>> 16));
                }
            }
        } else {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    m.justPut(key, (char) (entry >>> 16));
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    @Nonnull
    public HashObjSet<Map.Entry<Short, Character>> entrySet() {
        return new EntryView();
    }

    @Override
    @Nonnull
    public CharCollection values() {
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
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        int entry;
        if (noRemoved()) {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    hashCode += key ^ (char) (entry >>> 16);
                }
            }
        } else {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    hashCode += key ^ (char) (entry >>> 16);
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
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        int entry;
        if (noRemoved()) {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    sb.append(' ');
                    sb.append(key);
                    sb.append('=');
                    sb.append((char) (entry >>> 16));
                    sb.append(',');
                    if (++elementCount == 8) {
                        int expectedLength = sb.length() * (size() / 8);
                        sb.ensureCapacity(expectedLength + (expectedLength / 2));
                    }
                }
            }
        } else {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    sb.append(' ');
                    sb.append(key);
                    sb.append('=');
                    sb.append((char) (entry >>> 16));
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
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        int entry;
        initForRehash(newCapacity);
        mc++; // modCount is incremented in initForRehash()
        int[] newTab = table;
        int capacity = newTab.length;
        if (noRemoved()) {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    int index;
                    if (U.getShort(newTab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index = ParallelKVShortKeyMixing.mix(key) % capacity)) << INT_SCALE_SHIFT)) != free) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if (U.getShort(newTab, INT_BASE + SHORT_KEY_OFFSET + (((long) (bIndex)) << INT_SCALE_SHIFT)) == free) {
                                index = bIndex;
                                break;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if (U.getShort(newTab, INT_BASE + SHORT_KEY_OFFSET + (((long) (fIndex)) << INT_SCALE_SHIFT)) == free) {
                                index = fIndex;
                                break;
                            }
                            step += 2;
                        }
                    }
                    newTab[index] = entry;
                }
            }
        } else {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    int index;
                    if (U.getShort(newTab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index = ParallelKVShortKeyMixing.mix(key) % capacity)) << INT_SCALE_SHIFT)) != free) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if (U.getShort(newTab, INT_BASE + SHORT_KEY_OFFSET + (((long) (bIndex)) << INT_SCALE_SHIFT)) == free) {
                                index = bIndex;
                                break;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if (U.getShort(newTab, INT_BASE + SHORT_KEY_OFFSET + (((long) (fIndex)) << INT_SCALE_SHIFT)) == free) {
                                index = fIndex;
                                break;
                            }
                            step += 2;
                        }
                    }
                    newTab[index] = entry;
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }


    @Override
    public Character put(Short key, Character value) {
        short k = key;
        short free;
        short removed = removedValue;
        if (k == (free = freeValue)) {
            free = changeFree();
        } else if (k == removed) {
            removed = changeRemoved();
        }
        int[] tab = table;
        int capacity, index;
        short cur;
        int entry;
        if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(k) % (capacity = tab.length)])) == free) {
            // key is absent, free slot
            incrementModCount();
            tab[index] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
            postFreeSlotInsertHook();
            return null;
        } else {
            keyPresent:
            if (cur != k) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = (short) (entry = tab[bIndex])) == free) {
                                // key is absent, free slot
                                incrementModCount();
                                tab[bIndex] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                                postFreeSlotInsertHook();
                                return null;
                            } else if (cur == k) {
                                index = bIndex;
                                break keyPresent;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = (short) (entry = tab[fIndex])) == free) {
                                // key is absent, free slot
                                incrementModCount();
                                tab[fIndex] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                                postFreeSlotInsertHook();
                                return null;
                            } else if (cur == k) {
                                index = fIndex;
                                break keyPresent;
                            }
                            step += 2;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                int bIndex = index, fIndex = index, step = 1;
                while (true) {
                    if ((bIndex -= step) < 0) bIndex += capacity;
                    if ((cur = (short) (entry = tab[bIndex])) == free) {
                        if (firstRemoved < 0) {
                            // key is absent, free slot
                            incrementModCount();
                            tab[bIndex] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                            postFreeSlotInsertHook();
                            return null;
                        } else {
                            // key is absent, removed slot
                            incrementModCount();
                            tab[firstRemoved] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                            postRemovedSlotInsertHook();
                            return null;
                        }
                    } else if (cur == k) {
                        index = bIndex;
                        break keyPresent;
                    } else if (cur == removed && firstRemoved < 0) {
                        firstRemoved = bIndex;
                    }
                    int t;
                    if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                    if ((cur = (short) (entry = tab[fIndex])) == free) {
                        if (firstRemoved < 0) {
                            // key is absent, free slot
                            incrementModCount();
                            tab[fIndex] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                            postFreeSlotInsertHook();
                            return null;
                        } else {
                            // key is absent, removed slot
                            incrementModCount();
                            tab[firstRemoved] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                            postRemovedSlotInsertHook();
                            return null;
                        }
                    } else if (cur == k) {
                        index = fIndex;
                        break keyPresent;
                    } else if (cur == removed && firstRemoved < 0) {
                        firstRemoved = fIndex;
                    }
                    step += 2;
                }
            }
            // key is present
            char prevValue = (char) (entry >>> 16);
            U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), value);
            return prevValue;
        }
    }

    @Override
    public char put(short key, char value) {
        short free;
        short removed = removedValue;
        if (key == (free = freeValue)) {
            free = changeFree();
        } else if (key == removed) {
            removed = changeRemoved();
        }
        int[] tab = table;
        int capacity, index;
        short cur;
        int entry;
        if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) == free) {
            // key is absent, free slot
            incrementModCount();
            tab[index] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
            postFreeSlotInsertHook();
            return defaultValue();
        } else {
            keyPresent:
            if (cur != key) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = (short) (entry = tab[bIndex])) == free) {
                                // key is absent, free slot
                                incrementModCount();
                                tab[bIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                                postFreeSlotInsertHook();
                                return defaultValue();
                            } else if (cur == key) {
                                index = bIndex;
                                break keyPresent;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = (short) (entry = tab[fIndex])) == free) {
                                // key is absent, free slot
                                incrementModCount();
                                tab[fIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                                postFreeSlotInsertHook();
                                return defaultValue();
                            } else if (cur == key) {
                                index = fIndex;
                                break keyPresent;
                            }
                            step += 2;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                int bIndex = index, fIndex = index, step = 1;
                while (true) {
                    if ((bIndex -= step) < 0) bIndex += capacity;
                    if ((cur = (short) (entry = tab[bIndex])) == free) {
                        if (firstRemoved < 0) {
                            // key is absent, free slot
                            incrementModCount();
                            tab[bIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postFreeSlotInsertHook();
                            return defaultValue();
                        } else {
                            // key is absent, removed slot
                            incrementModCount();
                            tab[firstRemoved] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postRemovedSlotInsertHook();
                            return defaultValue();
                        }
                    } else if (cur == key) {
                        index = bIndex;
                        break keyPresent;
                    } else if (cur == removed && firstRemoved < 0) {
                        firstRemoved = bIndex;
                    }
                    int t;
                    if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                    if ((cur = (short) (entry = tab[fIndex])) == free) {
                        if (firstRemoved < 0) {
                            // key is absent, free slot
                            incrementModCount();
                            tab[fIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postFreeSlotInsertHook();
                            return defaultValue();
                        } else {
                            // key is absent, removed slot
                            incrementModCount();
                            tab[firstRemoved] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postRemovedSlotInsertHook();
                            return defaultValue();
                        }
                    } else if (cur == key) {
                        index = fIndex;
                        break keyPresent;
                    } else if (cur == removed && firstRemoved < 0) {
                        firstRemoved = fIndex;
                    }
                    step += 2;
                }
            }
            // key is present
            char prevValue = (char) (entry >>> 16);
            U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), value);
            return prevValue;
        }
    }

    @Override
    public Character putIfAbsent(Short key, Character value) {
        short k = key;
        short free;
        short removed = removedValue;
        if (k == (free = freeValue)) {
            free = changeFree();
        } else if (k == removed) {
            removed = changeRemoved();
        }
        int[] tab = table;
        int capacity, index;
        short cur;
        int entry;
        if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(k) % (capacity = tab.length)])) == free) {
            // key is absent, free slot
            incrementModCount();
            tab[index] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
            postFreeSlotInsertHook();
            return null;
        } else {
            if (cur == k) {
                // key is present
                return (char) (entry >>> 16);
            } else {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = (short) (entry = tab[bIndex])) == free) {
                                // key is absent, free slot
                                incrementModCount();
                                tab[bIndex] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                                postFreeSlotInsertHook();
                                return null;
                            } else if (cur == k) {
                                // key is present
                                return (char) (entry >>> 16);
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = (short) (entry = tab[fIndex])) == free) {
                                // key is absent, free slot
                                incrementModCount();
                                tab[fIndex] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                                postFreeSlotInsertHook();
                                return null;
                            } else if (cur == k) {
                                // key is present
                                return (char) (entry >>> 16);
                            }
                            step += 2;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                int bIndex = index, fIndex = index, step = 1;
                while (true) {
                    if ((bIndex -= step) < 0) bIndex += capacity;
                    if ((cur = (short) (entry = tab[bIndex])) == free) {
                        if (firstRemoved < 0) {
                            // key is absent, free slot
                            incrementModCount();
                            tab[bIndex] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                            postFreeSlotInsertHook();
                            return null;
                        } else {
                            // key is absent, removed slot
                            incrementModCount();
                            tab[firstRemoved] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                            postRemovedSlotInsertHook();
                            return null;
                        }
                    } else if (cur == k) {
                        // key is present
                        return (char) (entry >>> 16);
                    } else if (cur == removed && firstRemoved < 0) {
                        firstRemoved = bIndex;
                    }
                    int t;
                    if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                    if ((cur = (short) (entry = tab[fIndex])) == free) {
                        if (firstRemoved < 0) {
                            // key is absent, free slot
                            incrementModCount();
                            tab[fIndex] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                            postFreeSlotInsertHook();
                            return null;
                        } else {
                            // key is absent, removed slot
                            incrementModCount();
                            tab[firstRemoved] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                            postRemovedSlotInsertHook();
                            return null;
                        }
                    } else if (cur == k) {
                        // key is present
                        return (char) (entry >>> 16);
                    } else if (cur == removed && firstRemoved < 0) {
                        firstRemoved = fIndex;
                    }
                    step += 2;
                }
            }
        }
    }

    @Override
    public char putIfAbsent(short key, char value) {
        short free;
        short removed = removedValue;
        if (key == (free = freeValue)) {
            free = changeFree();
        } else if (key == removed) {
            removed = changeRemoved();
        }
        int[] tab = table;
        int capacity, index;
        short cur;
        int entry;
        if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) == free) {
            // key is absent, free slot
            incrementModCount();
            tab[index] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
            postFreeSlotInsertHook();
            return defaultValue();
        } else {
            if (cur == key) {
                // key is present
                return (char) (entry >>> 16);
            } else {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = (short) (entry = tab[bIndex])) == free) {
                                // key is absent, free slot
                                incrementModCount();
                                tab[bIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                                postFreeSlotInsertHook();
                                return defaultValue();
                            } else if (cur == key) {
                                // key is present
                                return (char) (entry >>> 16);
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = (short) (entry = tab[fIndex])) == free) {
                                // key is absent, free slot
                                incrementModCount();
                                tab[fIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                                postFreeSlotInsertHook();
                                return defaultValue();
                            } else if (cur == key) {
                                // key is present
                                return (char) (entry >>> 16);
                            }
                            step += 2;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                int bIndex = index, fIndex = index, step = 1;
                while (true) {
                    if ((bIndex -= step) < 0) bIndex += capacity;
                    if ((cur = (short) (entry = tab[bIndex])) == free) {
                        if (firstRemoved < 0) {
                            // key is absent, free slot
                            incrementModCount();
                            tab[bIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postFreeSlotInsertHook();
                            return defaultValue();
                        } else {
                            // key is absent, removed slot
                            incrementModCount();
                            tab[firstRemoved] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postRemovedSlotInsertHook();
                            return defaultValue();
                        }
                    } else if (cur == key) {
                        // key is present
                        return (char) (entry >>> 16);
                    } else if (cur == removed && firstRemoved < 0) {
                        firstRemoved = bIndex;
                    }
                    int t;
                    if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                    if ((cur = (short) (entry = tab[fIndex])) == free) {
                        if (firstRemoved < 0) {
                            // key is absent, free slot
                            incrementModCount();
                            tab[fIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postFreeSlotInsertHook();
                            return defaultValue();
                        } else {
                            // key is absent, removed slot
                            incrementModCount();
                            tab[firstRemoved] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postRemovedSlotInsertHook();
                            return defaultValue();
                        }
                    } else if (cur == key) {
                        // key is present
                        return (char) (entry >>> 16);
                    } else if (cur == removed && firstRemoved < 0) {
                        firstRemoved = fIndex;
                    }
                    step += 2;
                }
            }
        }
    }

    @Override
    public void justPut(short key, char value) {
        int index = insert(key, value);
        if (index < 0) {
            // key was absent
            return;
        } else {
            // key is present
            U.putChar(table, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), value);
            return;
        }
    }


    @Override
    public Character compute(Short key,
            BiFunction<? super Short, ? super Character, ? extends Character> remappingFunction) {
        short k = key;
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        short free;
        short removed = removedValue;
        if (k == (free = freeValue)) {
            free = changeFree();
        } else if (k == removed) {
            removed = changeRemoved();
        }
        int[] tab = table;
        int capacity, index;
        short cur;
        int entry;
        keyPresent:
        if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(k) % (capacity = tab.length)])) != k) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = (short) (entry = tab[bIndex])) == k) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = (short) (entry = tab[fIndex])) == k) {
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
                        if ((cur = (short) (entry = tab[bIndex])) == k) {
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
                        if ((cur = (short) (entry = tab[fIndex])) == k) {
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
                Character newValue = remappingFunction.apply(k, null);
                if (newValue != null) {
                    incrementModCount();
                    tab[firstRemoved] = ((((int) k) & SHORT_MASK) | (((int) newValue) << 16));
                    postRemovedSlotInsertHook();
                    return newValue;
                } else {
                    return null;
                }
            }
            // key is absent, free slot
            Character newValue = remappingFunction.apply(k, null);
            if (newValue != null) {
                incrementModCount();
                tab[index] = ((((int) k) & SHORT_MASK) | (((int) newValue) << 16));
                postFreeSlotInsertHook();
                return newValue;
            } else {
                return null;
            }
        }
        // key is present
        Character newValue = remappingFunction.apply(k, (char) (entry >>> 16));
        if (newValue != null) {
            U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), newValue);
            return newValue;
        } else {
            incrementModCount();
            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
            postRemoveHook();
            return null;
        }
    }


    @Override
    public char compute(short key, ShortCharToCharFunction remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        short free;
        short removed = removedValue;
        if (key == (free = freeValue)) {
            free = changeFree();
        } else if (key == removed) {
            removed = changeRemoved();
        }
        int[] tab = table;
        int capacity, index;
        short cur;
        int entry;
        keyPresent:
        if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) != key) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = (short) (entry = tab[bIndex])) == key) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = (short) (entry = tab[fIndex])) == key) {
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
                        if ((cur = (short) (entry = tab[bIndex])) == key) {
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
                        if ((cur = (short) (entry = tab[fIndex])) == key) {
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
                char newValue = remappingFunction.applyAsChar(key, defaultValue());
                incrementModCount();
                tab[firstRemoved] = ((((int) key) & SHORT_MASK) | (((int) newValue) << 16));
                postRemovedSlotInsertHook();
                return newValue;
            }
            // key is absent, free slot
            char newValue = remappingFunction.applyAsChar(key, defaultValue());
            incrementModCount();
            tab[index] = ((((int) key) & SHORT_MASK) | (((int) newValue) << 16));
            postFreeSlotInsertHook();
            return newValue;
        }
        // key is present
        char newValue = remappingFunction.applyAsChar(key, (char) (entry >>> 16));
        U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), newValue);
        return newValue;
    }


    @Override
    public Character computeIfAbsent(Short key,
            Function<? super Short, ? extends Character> mappingFunction) {
        short k = key;
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        short free;
        short removed = removedValue;
        if (k == (free = freeValue)) {
            free = changeFree();
        } else if (k == removed) {
            removed = changeRemoved();
        }
        int[] tab = table;
        int capacity, index;
        short cur;
        int entry;
        if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(k) % (capacity = tab.length)])) == k) {
            // key is present
            return (char) (entry >>> 16);
        } else {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = (short) (entry = tab[bIndex])) == k) {
                                // key is present
                                return (char) (entry >>> 16);
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = (short) (entry = tab[fIndex])) == k) {
                                // key is present
                                return (char) (entry >>> 16);
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
                        if ((cur = (short) (entry = tab[bIndex])) == k) {
                            // key is present
                            return (char) (entry >>> 16);
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
                        if ((cur = (short) (entry = tab[fIndex])) == k) {
                            // key is present
                            return (char) (entry >>> 16);
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
                Character value = mappingFunction.apply(k);
                if (value != null) {
                    incrementModCount();
                    tab[firstRemoved] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                    postRemovedSlotInsertHook();
                    return value;
                } else {
                    return null;
                }
            }
            // key is absent, free slot
            Character value = mappingFunction.apply(k);
            if (value != null) {
                incrementModCount();
                tab[index] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                postFreeSlotInsertHook();
                return value;
            } else {
                return null;
            }
        }
    }


    @Override
    public char computeIfAbsent(short key, ShortToCharFunction mappingFunction) {
        if (mappingFunction == null)
            throw new java.lang.NullPointerException();
        short free;
        short removed = removedValue;
        if (key == (free = freeValue)) {
            free = changeFree();
        } else if (key == removed) {
            removed = changeRemoved();
        }
        int[] tab = table;
        int capacity, index;
        short cur;
        int entry;
        if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) == key) {
            // key is present
            return (char) (entry >>> 16);
        } else {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = (short) (entry = tab[bIndex])) == key) {
                                // key is present
                                return (char) (entry >>> 16);
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = (short) (entry = tab[fIndex])) == key) {
                                // key is present
                                return (char) (entry >>> 16);
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
                        if ((cur = (short) (entry = tab[bIndex])) == key) {
                            // key is present
                            return (char) (entry >>> 16);
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
                        if ((cur = (short) (entry = tab[fIndex])) == key) {
                            // key is present
                            return (char) (entry >>> 16);
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
                char value = mappingFunction.applyAsChar(key);
                incrementModCount();
                tab[firstRemoved] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                postRemovedSlotInsertHook();
                return value;
            }
            // key is absent, free slot
            char value = mappingFunction.applyAsChar(key);
            incrementModCount();
            tab[index] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
            postFreeSlotInsertHook();
            return value;
        }
    }


    @Override
    public Character computeIfPresent(Short key,
            BiFunction<? super Short, ? super Character, ? extends Character> remappingFunction) {
        short k = key;
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        short free, removed;
        if (k != (free = freeValue) && k != (removed = removedValue)) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            keyPresent:
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(k) % (capacity = tab.length)])) != k) {
                if (cur == free) {
                    // key is absent, free slot
                    return null;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == k) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return null;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == k) {
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
            Character newValue = remappingFunction.apply(k, (char) (entry >>> 16));
            if (newValue != null) {
                U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), newValue);
                return newValue;
            } else {
                incrementModCount();
                U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
                postRemoveHook();
                return null;
            }
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public char computeIfPresent(short key, ShortCharToCharFunction remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        short free;
        if (key != (free = freeValue) && key != removedValue) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            keyPresent:
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) != key) {
                if (cur == free) {
                    // key is absent, free slot
                    return defaultValue();
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue();
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue();
                        }
                        step += 2;
                    }
                }
            }
            // key is present
            char newValue = remappingFunction.applyAsChar(key, (char) (entry >>> 16));
            U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), newValue);
            return newValue;
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public Character merge(Short key, Character value,
            BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
        short k = key;
        if (value == null)
            throw new java.lang.NullPointerException();
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        short free;
        short removed = removedValue;
        if (k == (free = freeValue)) {
            free = changeFree();
        } else if (k == removed) {
            removed = changeRemoved();
        }
        int[] tab = table;
        int capacity, index;
        short cur;
        int entry;
        keyPresent:
        if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(k) % (capacity = tab.length)])) != k) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = (short) (entry = tab[bIndex])) == k) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = (short) (entry = tab[fIndex])) == k) {
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
                        if ((cur = (short) (entry = tab[bIndex])) == k) {
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
                        if ((cur = (short) (entry = tab[fIndex])) == k) {
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
                tab[firstRemoved] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
                postRemovedSlotInsertHook();
                return value;
            }
            // key is absent, free slot
            incrementModCount();
            tab[index] = ((((int) k) & SHORT_MASK) | (((int) value) << 16));
            postFreeSlotInsertHook();
            return value;
        }
        // key is present
        Character newValue = remappingFunction.apply((char) (entry >>> 16), value);
        if (newValue != null) {
            U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), newValue);
            return newValue;
        } else {
            incrementModCount();
            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
            postRemoveHook();
            return null;
        }
    }


    @Override
    public char merge(short key, char value, CharBinaryOperator remappingFunction) {
        if (remappingFunction == null)
            throw new java.lang.NullPointerException();
        short free;
        short removed = removedValue;
        if (key == (free = freeValue)) {
            free = changeFree();
        } else if (key == removed) {
            removed = changeRemoved();
        }
        int[] tab = table;
        int capacity, index;
        short cur;
        int entry;
        keyPresent:
        if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) != key) {
            keyAbsentFreeSlot:
            if (cur != free) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = (short) (entry = tab[bIndex])) == key) {
                                index = bIndex;
                                break keyPresent;
                            } else if (cur == free) {
                                index = bIndex;
                                break keyAbsentFreeSlot;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = (short) (entry = tab[fIndex])) == key) {
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
                        if ((cur = (short) (entry = tab[bIndex])) == key) {
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
                        if ((cur = (short) (entry = tab[fIndex])) == key) {
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
                tab[firstRemoved] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                postRemovedSlotInsertHook();
                return value;
            }
            // key is absent, free slot
            incrementModCount();
            tab[index] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
            postFreeSlotInsertHook();
            return value;
        }
        // key is present
        char newValue = remappingFunction.applyAsChar((char) (entry >>> 16), value);
        U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), newValue);
        return newValue;
    }


    @Override
    public char addValue(short key, char value) {
        short free;
        short removed = removedValue;
        if (key == (free = freeValue)) {
            free = changeFree();
        } else if (key == removed) {
            removed = changeRemoved();
        }
        int[] tab = table;
        int capacity, index;
        short cur;
        int entry;
        if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) == free) {
            // key is absent, free slot
            incrementModCount();
            tab[index] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
            postFreeSlotInsertHook();
            return value;
        } else {
            keyPresent:
            if (cur != key) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = (short) (entry = tab[bIndex])) == free) {
                                // key is absent, free slot
                                incrementModCount();
                                tab[bIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                                postFreeSlotInsertHook();
                                return value;
                            } else if (cur == key) {
                                index = bIndex;
                                break keyPresent;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = (short) (entry = tab[fIndex])) == free) {
                                // key is absent, free slot
                                incrementModCount();
                                tab[fIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                                postFreeSlotInsertHook();
                                return value;
                            } else if (cur == key) {
                                index = fIndex;
                                break keyPresent;
                            }
                            step += 2;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                int bIndex = index, fIndex = index, step = 1;
                while (true) {
                    if ((bIndex -= step) < 0) bIndex += capacity;
                    if ((cur = (short) (entry = tab[bIndex])) == free) {
                        if (firstRemoved < 0) {
                            // key is absent, free slot
                            incrementModCount();
                            tab[bIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postFreeSlotInsertHook();
                            return value;
                        } else {
                            // key is absent, removed slot
                            incrementModCount();
                            tab[firstRemoved] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postRemovedSlotInsertHook();
                            return value;
                        }
                    } else if (cur == key) {
                        index = bIndex;
                        break keyPresent;
                    } else if (cur == removed && firstRemoved < 0) {
                        firstRemoved = bIndex;
                    }
                    int t;
                    if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                    if ((cur = (short) (entry = tab[fIndex])) == free) {
                        if (firstRemoved < 0) {
                            // key is absent, free slot
                            incrementModCount();
                            tab[fIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postFreeSlotInsertHook();
                            return value;
                        } else {
                            // key is absent, removed slot
                            incrementModCount();
                            tab[firstRemoved] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postRemovedSlotInsertHook();
                            return value;
                        }
                    } else if (cur == key) {
                        index = fIndex;
                        break keyPresent;
                    } else if (cur == removed && firstRemoved < 0) {
                        firstRemoved = fIndex;
                    }
                    step += 2;
                }
            }
            // key is present
            char newValue = (char) ((char) (entry >>> 16) + value);
            U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), newValue);
            return newValue;
        }
    }

    @Override
    public char addValue(short key, char addition, char defaultValue) {
        char value = (char) (defaultValue + addition);
        short free;
        short removed = removedValue;
        if (key == (free = freeValue)) {
            free = changeFree();
        } else if (key == removed) {
            removed = changeRemoved();
        }
        int[] tab = table;
        int capacity, index;
        short cur;
        int entry;
        if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) == free) {
            // key is absent, free slot
            incrementModCount();
            tab[index] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
            postFreeSlotInsertHook();
            return value;
        } else {
            keyPresent:
            if (cur != key) {
                int firstRemoved;
                if (cur != removed) {
                    if (noRemoved()) {
                        int bIndex = index, fIndex = index, step = 1;
                        while (true) {
                            if ((bIndex -= step) < 0) bIndex += capacity;
                            if ((cur = (short) (entry = tab[bIndex])) == free) {
                                // key is absent, free slot
                                incrementModCount();
                                tab[bIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                                postFreeSlotInsertHook();
                                return value;
                            } else if (cur == key) {
                                index = bIndex;
                                break keyPresent;
                            }
                            int t;
                            if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                            if ((cur = (short) (entry = tab[fIndex])) == free) {
                                // key is absent, free slot
                                incrementModCount();
                                tab[fIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                                postFreeSlotInsertHook();
                                return value;
                            } else if (cur == key) {
                                index = fIndex;
                                break keyPresent;
                            }
                            step += 2;
                        }
                    } else {
                        firstRemoved = -1;
                    }
                } else {
                    firstRemoved = index;
                }
                int bIndex = index, fIndex = index, step = 1;
                while (true) {
                    if ((bIndex -= step) < 0) bIndex += capacity;
                    if ((cur = (short) (entry = tab[bIndex])) == free) {
                        if (firstRemoved < 0) {
                            // key is absent, free slot
                            incrementModCount();
                            tab[bIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postFreeSlotInsertHook();
                            return value;
                        } else {
                            // key is absent, removed slot
                            incrementModCount();
                            tab[firstRemoved] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postRemovedSlotInsertHook();
                            return value;
                        }
                    } else if (cur == key) {
                        index = bIndex;
                        break keyPresent;
                    } else if (cur == removed && firstRemoved < 0) {
                        firstRemoved = bIndex;
                    }
                    int t;
                    if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                    if ((cur = (short) (entry = tab[fIndex])) == free) {
                        if (firstRemoved < 0) {
                            // key is absent, free slot
                            incrementModCount();
                            tab[fIndex] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postFreeSlotInsertHook();
                            return value;
                        } else {
                            // key is absent, removed slot
                            incrementModCount();
                            tab[firstRemoved] = ((((int) key) & SHORT_MASK) | (((int) value) << 16));
                            postRemovedSlotInsertHook();
                            return value;
                        }
                    } else if (cur == key) {
                        index = fIndex;
                        break keyPresent;
                    } else if (cur == removed && firstRemoved < 0) {
                        firstRemoved = fIndex;
                    }
                    step += 2;
                }
            }
            // key is present
            char newValue = (char) ((char) (entry >>> 16) + addition);
            U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), newValue);
            return newValue;
        }
    }


    @Override
    public void putAll(@Nonnull Map<? extends Short, ? extends Character> m) {
        CommonShortCharMapOps.putAll(this, m);
    }


    @Override
    public Character replace(Short key, Character value) {
        short k = key;
        short free;
        if (k != (free = freeValue) && k != removedValue) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            keyPresent:
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(k) % (capacity = tab.length)])) != k) {
                if (cur == free) {
                    // key is absent, free slot
                    return null;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == k) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return null;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == k) {
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
            char oldValue = (char) (entry >>> 16);
            U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), value);
            return oldValue;
        } else {
            // key is absent
            return null;
        }
    }

    @Override
    public char replace(short key, char value) {
        short free;
        if (key != (free = freeValue) && key != removedValue) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            keyPresent:
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) != key) {
                if (cur == free) {
                    // key is absent, free slot
                    return defaultValue();
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue();
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue();
                        }
                        step += 2;
                    }
                }
            }
            // key is present
            char oldValue = (char) (entry >>> 16);
            U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), value);
            return oldValue;
        } else {
            // key is absent
            return defaultValue();
        }
    }

    @Override
    public boolean replace(Short key, Character oldValue, Character newValue) {
        return replace(key.shortValue(),
                oldValue.charValue(),
                newValue.charValue());
    }

    @Override
    public boolean replace(short key, char oldValue, char newValue) {
        short free;
        if (key != (free = freeValue) && key != removedValue) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            keyPresent:
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) != key) {
                if (cur == free) {
                    // key is absent, free slot
                    return false;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return false;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == key) {
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
            if ((char) (entry >>> 16) == oldValue) {
                U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), newValue);
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
            BiFunction<? super Short, ? super Character, ? extends Character> function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        int entry;
        if (noRemoved()) {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), function.apply(key, (char) (entry >>> 16)));
                }
            }
        } else {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), function.apply(key, (char) (entry >>> 16)));
                }
            }
        }
        if (mc != modCount())
            throw new java.util.ConcurrentModificationException();
    }

    @Override
    public void replaceAll(ShortCharToCharFunction function) {
        if (function == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        int entry;
        if (noRemoved()) {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), function.applyAsChar(key, (char) (entry >>> 16)));
                }
            }
        } else {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), function.applyAsChar(key, (char) (entry >>> 16)));
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
        postRemoveHook();
    }

    @Override
    public Character remove(Object key) {
        short k = (Short) key;
        short free, removed;
        if (k != (free = freeValue) && k != (removed = removedValue)) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            keyPresent:
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(k) % (capacity = tab.length)])) != k) {
                if (cur == free) {
                    // key is absent, free slot
                    return null;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == k) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return null;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == k) {
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
            char val = (char) (entry >>> 16);
            incrementModCount();
            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
            postRemoveHook();
            return val;
        } else {
            // key is absent
            return null;
        }
    }


    @Override
    public boolean justRemove(short key) {
        short free, removed;
        if (key != (free = freeValue) && key != (removed = removedValue)) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            keyPresent:
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) != key) {
                if (cur == free) {
                    // key is absent, free slot
                    return false;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return false;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == key) {
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
            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
            postRemoveHook();
            return true;
        } else {
            // key is absent
            return false;
        }
    }



    

    @Override
    public char remove(short key) {
        short free, removed;
        if (key != (free = freeValue) && key != (removed = removedValue)) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            keyPresent:
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) != key) {
                if (cur == free) {
                    // key is absent, free slot
                    return defaultValue();
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue();
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == key) {
                            index = fIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return defaultValue();
                        }
                        step += 2;
                    }
                }
            }
            // key is present
            char val = (char) (entry >>> 16);
            incrementModCount();
            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
            postRemoveHook();
            return val;
        } else {
            // key is absent
            return defaultValue();
        }
    }



    @Override
    public boolean remove(Object key, Object value) {
        return remove(((Short) key).shortValue(),
                ((Character) value).charValue()
                );
    }

    @Override
    public boolean remove(short key, char value) {
        short free, removed;
        if (key != (free = freeValue) && key != (removed = removedValue)) {
            int[] tab = table;
            int capacity, index;
            short cur;
            int entry;
            keyPresent:
            if ((cur = (short) (entry = tab[index = ParallelKVShortKeyMixing.mix(key) % (capacity = tab.length)])) != key) {
                if (cur == free) {
                    // key is absent, free slot
                    return false;
                } else {
                    int bIndex = index, fIndex = index, step = 1;
                    while (true) {
                        if ((bIndex -= step) < 0) bIndex += capacity;
                        if ((cur = (short) (entry = tab[bIndex])) == key) {
                            index = bIndex;
                            break keyPresent;
                        } else if (cur == free) {
                            // key is absent, free slot
                            return false;
                        }
                        int t;
                        if ((t = (fIndex += step) - capacity) >= 0) fIndex = t;
                        if ((cur = (short) (entry = tab[fIndex])) == key) {
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
            if ((char) (entry >>> 16) == value) {
                incrementModCount();
                U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
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
    public boolean removeIf(ShortCharPredicate filter) {
        if (filter == null)
            throw new java.lang.NullPointerException();
        if (isEmpty())
            return false;
        boolean changed = false;
        int mc = modCount();
        short free = freeValue;
        short removed = removedValue;
        int[] tab = table;
        int entry;
        if (noRemoved()) {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    if (filter.test(key, (char) (entry >>> 16))) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
                        postRemoveHook();
                        changed = true;
                    }
                }
            }
        } else {
            for (int i = tab.length - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    if (filter.test(key, (char) (entry >>> 16))) {
                        incrementModCount();
                        mc++;
                        U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
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






    class EntryView extends AbstractSetView<Map.Entry<Short, Character>>
            implements HashObjSet<Map.Entry<Short, Character>>,
            InternalObjCollectionOps<Map.Entry<Short, Character>> {

        @Nonnull
        @Override
        public Equivalence<Entry<Short, Character>> equivalence() {
            return Equivalence.entryEquivalence(
                    Equivalence.<Short>defaultEquality()
                    ,
                    Equivalence.<Character>defaultEquality()
                    
            );
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return MutableQHashParallelKVShortCharMapGO.this.hashConfig();
        }


        @Override
        public int size() {
            return MutableQHashParallelKVShortCharMapGO.this.size();
        }

        @Override
        public double currentLoad() {
            return MutableQHashParallelKVShortCharMapGO.this.currentLoad();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Short, Character> e = (Map.Entry<Short, Character>) o;
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
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        result[resultIndex++] = new MutableEntry(mc, i, key, (char) (entry >>> 16));
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        result[resultIndex++] = new MutableEntry(mc, i, key, (char) (entry >>> 16));
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
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        a[resultIndex++] = (T) new MutableEntry(mc, i, key, (char) (entry >>> 16));
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        a[resultIndex++] = (T) new MutableEntry(mc, i, key, (char) (entry >>> 16));
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
        public final void forEach(@Nonnull Consumer<? super Map.Entry<Short, Character>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        action.accept(new MutableEntry(mc, i, key, (char) (entry >>> 16)));
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        action.accept(new MutableEntry(mc, i, key, (char) (entry >>> 16)));
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public boolean forEachWhile(@Nonnull  Predicate<? super Map.Entry<Short, Character>> predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        if (!predicate.test(new MutableEntry(mc, i, key, (char) (entry >>> 16)))) {
                            terminated = true;
                            break;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        if (!predicate.test(new MutableEntry(mc, i, key, (char) (entry >>> 16)))) {
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
        public ObjIterator<Map.Entry<Short, Character>> iterator() {
            int mc = modCount();
            if (!noRemoved())
                return new SomeRemovedEntryIterator(mc);
            return new NoRemovedEntryIterator(mc);
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Short, Character>> cursor() {
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
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        if (!c.contains(e.with(key, (char) (entry >>> 16)))) {
                            containsAll = false;
                            break;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        if (!c.contains(e.with(key, (char) (entry >>> 16)))) {
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
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        changed |= s.remove(e.with(key, (char) (entry >>> 16)));
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        changed |= s.remove(e.with(key, (char) (entry >>> 16)));
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }

        @Override
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Short, Character>> c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        changed |= c.add(new MutableEntry(mc, i, key, (char) (entry >>> 16)));
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        changed |= c.add(new MutableEntry(mc, i, key, (char) (entry >>> 16)));
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        public int hashCode() {
            return MutableQHashParallelKVShortCharMapGO.this.hashCode();
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        sb.append(' ');
                        sb.append(key);
                        sb.append('=');
                        sb.append((char) (entry >>> 16));
                        sb.append(',');
                        if (++elementCount == 8) {
                            int expectedLength = sb.length() * (size() / 8);
                            sb.ensureCapacity(expectedLength + (expectedLength / 2));
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        sb.append(' ');
                        sb.append(key);
                        sb.append('=');
                        sb.append((char) (entry >>> 16));
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
            return MutableQHashParallelKVShortCharMapGO.this.shrink();
        }


        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Short, Character> e = (Map.Entry<Short, Character>) o;
                short key = e.getKey();
                char value = e.getValue();
                return MutableQHashParallelKVShortCharMapGO.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }


        @Override
        public final boolean removeIf(@Nonnull Predicate<? super Map.Entry<Short, Character>> filter) {
            if (filter == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        if (filter.test(new MutableEntry(mc, i, key, (char) (entry >>> 16)))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        if (filter.test(new MutableEntry(mc, i, key, (char) (entry >>> 16)))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
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
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        if (c.contains(e.with(key, (char) (entry >>> 16)))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        if (c.contains(e.with(key, (char) (entry >>> 16)))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
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
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        if (!c.contains(e.with(key, (char) (entry >>> 16)))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        if (!c.contains(e.with(key, (char) (entry >>> 16)))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
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
            MutableQHashParallelKVShortCharMapGO.this.clear();
        }
    }


    abstract class ShortCharEntry extends AbstractEntry<Short, Character> {

        abstract short key();

        @Override
        public final Short getKey() {
            return key();
        }

        abstract char value();

        @Override
        public final Character getValue() {
            return value();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            Map.Entry e2;
            short k2;
            char v2;
            try {
                e2 = (Map.Entry) o;
                k2 = (Short) e2.getKey();
                v2 = (Character) e2.getValue();
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


    class MutableEntry extends ShortCharEntry {
        final int modCount;
        private final int index;
        final short key;
        private char value;

        MutableEntry(int modCount, int index, short key, char value) {
            this.modCount = modCount;
            this.index = index;
            this.key = key;
            this.value = value;
        }

        @Override
        public short key() {
            return key;
        }

        @Override
        public char value() {
            return value;
        }

        @Override
        public Character setValue(Character newValue) {
            if (modCount != modCount())
                throw new IllegalStateException();
            char oldValue = value;
            char unwrappedNewValue = newValue;
            value = unwrappedNewValue;
            updateValueInTable(unwrappedNewValue);
            return oldValue;
        }

        void updateValueInTable(char newValue) {
            U.putChar(
                    table, INT_BASE + CHAR_VALUE_OFFSET + (((long) index) << INT_SCALE_SHIFT),
                    newValue);
        }
    }



    class ReusableEntry extends ShortCharEntry {
        private short key;
        private char value;

        ReusableEntry with(short key, char value) {
            this.key = key;
            this.value = value;
            return this;
        }

        @Override
        public short key() {
            return key;
        }

        @Override
        public char value() {
            return value;
        }
    }


    class ValueView extends AbstractCharValueView {


        @Override
        public int size() {
            return MutableQHashParallelKVShortCharMapGO.this.size();
        }

        @Override
        public boolean shrink() {
            return MutableQHashParallelKVShortCharMapGO.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return MutableQHashParallelKVShortCharMapGO.this.containsValue(o);
        }

        @Override
        public boolean contains(char v) {
            return MutableQHashParallelKVShortCharMapGO.this.containsValue(v);
        }



        @Override
        public void forEach(Consumer<? super Character> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        action.accept((char) (entry >>> 16));
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        action.accept((char) (entry >>> 16));
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public void forEach(CharConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        action.accept((char) (entry >>> 16));
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        action.accept((char) (entry >>> 16));
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
        }

        @Override
        public boolean forEachWhile(CharPredicate predicate) {
            if (predicate == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return true;
            boolean terminated = false;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        if (!predicate.test((char) (entry >>> 16))) {
                            terminated = true;
                            break;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        if (!predicate.test((char) (entry >>> 16))) {
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
        public boolean allContainingIn(CharCollection c) {
            if (isEmpty())
                return true;
            boolean containsAll = true;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        if (!c.contains((char) (entry >>> 16))) {
                            containsAll = false;
                            break;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        if (!c.contains((char) (entry >>> 16))) {
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
        public boolean reverseAddAllTo(CharCollection c) {
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        changed |= c.add((char) (entry >>> 16));
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        changed |= c.add((char) (entry >>> 16));
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }


        @Override
        public boolean reverseRemoveAllFrom(CharSet s) {
            if (isEmpty() || s.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        changed |= s.removeChar((char) (entry >>> 16));
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        changed |= s.removeChar((char) (entry >>> 16));
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return changed;
        }



        @Override
        @Nonnull
        public CharIterator iterator() {
            int mc = modCount();
            if (!noRemoved())
                return new SomeRemovedValueIterator(mc);
            return new NoRemovedValueIterator(mc);
        }

        @Nonnull
        @Override
        public CharCursor cursor() {
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
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        result[resultIndex++] = (char) (entry >>> 16);
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        result[resultIndex++] = (char) (entry >>> 16);
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
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        a[resultIndex++] = (T) Character.valueOf((char) (entry >>> 16));
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        a[resultIndex++] = (T) Character.valueOf((char) (entry >>> 16));
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
        public char[] toCharArray() {
            int size = size();
            char[] result = new char[size];
            if (size == 0)
                return result;
            int resultIndex = 0;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        result[resultIndex++] = (char) (entry >>> 16);
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        result[resultIndex++] = (char) (entry >>> 16);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            return result;
        }

        @Override
        public char[] toArray(char[] a) {
            int size = size();
            if (a.length < size)
                a = new char[size];
            if (size == 0) {
                if (a.length > 0)
                    a[0] = (char) 0;
                return a;
            }
            int resultIndex = 0;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        a[resultIndex++] = (char) (entry >>> 16);
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        a[resultIndex++] = (char) (entry >>> 16);
                    }
                }
            }
            if (mc != modCount())
                throw new java.util.ConcurrentModificationException();
            if (a.length > resultIndex)
                a[resultIndex] = (char) 0;
            return a;
        }


        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        sb.append(' ').append((char) (entry >>> 16)).append(',');
                        if (++elementCount == 8) {
                            int expectedLength = sb.length() * (size() / 8);
                            sb.ensureCapacity(expectedLength + (expectedLength / 2));
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        sb.append(' ').append((char) (entry >>> 16)).append(',');
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
            return removeChar(( Character ) o);
        }

        @Override
        public boolean removeChar(char v) {
            return removeValue(v);
        }



        @Override
        public void clear() {
            MutableQHashParallelKVShortCharMapGO.this.clear();
        }

        
        public boolean removeIf(Predicate<? super Character> filter) {
            if (filter == null)
                throw new java.lang.NullPointerException();
            if (isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        if (filter.test((char) (entry >>> 16))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        if (filter.test((char) (entry >>> 16))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
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
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        if (filter.test((char) (entry >>> 16))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        if (filter.test((char) (entry >>> 16))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
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
            if (c instanceof CharCollection)
                return removeAll((CharCollection) c);
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty() || c.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        if (c.contains((char) (entry >>> 16))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        if (c.contains((char) (entry >>> 16))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
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

        private boolean removeAll(CharCollection c) {
            if (this == c)
                throw new IllegalArgumentException();
            if (isEmpty() || c.isEmpty())
                return false;
            boolean changed = false;
            int mc = modCount();
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        if (c.contains((char) (entry >>> 16))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        if (c.contains((char) (entry >>> 16))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
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
            if (c instanceof CharCollection)
                return retainAll((CharCollection) c);
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
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        if (!c.contains((char) (entry >>> 16))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        if (!c.contains((char) (entry >>> 16))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
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

        private boolean retainAll(CharCollection c) {
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
            short free = freeValue;
            short removed = removedValue;
            int[] tab = table;
            int entry;
            if (noRemoved()) {
                for (int i = tab.length - 1; i >= 0; i--) {
                    if ((short) (entry = tab[i]) != free) {
                        if (!c.contains((char) (entry >>> 16))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
                            postRemoveHook();
                            changed = true;
                        }
                    }
                }
            } else {
                for (int i = tab.length - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        if (!c.contains((char) (entry >>> 16))) {
                            incrementModCount();
                            mc++;
                            U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (i)) << INT_SCALE_SHIFT), removed);
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



    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Short, Character>> {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        MutableEntry next;

        NoRemovedEntryIterator(int mc) {
            expectedModCount = mc;
            int[] tab = this.tab = table;
            short free = this.free = freeValue;
            this.removed = removedValue;
            int nextI = tab.length;
            int entry;
            while (--nextI >= 0) {
                short key;
                if ((key = (short) (entry = tab[nextI])) != free) {
                    next = new MutableEntry(mc, nextI, key, (char) (entry >>> 16));
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Short, Character>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            int entry;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    action.accept(new MutableEntry(mc, i, key, (char) (entry >>> 16)));
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
        public Map.Entry<Short, Character> next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                int mc;
                if ((mc = expectedModCount) == modCount()) {
                    index = nextI;
                    int[] tab = this.tab;
                    short free = this.free;
                    MutableEntry prev = next;
                    int entry;
                    while (--nextI >= 0) {
                        short key;
                        if ((key = (short) (entry = tab[nextI])) != free) {
                            next = new MutableEntry(mc, nextI, key, (char) (entry >>> 16));
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
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Short, Character>> {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index;
        short curKey;
        char curValue;

        NoRemovedEntryCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            short free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Short, Character>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            int entry;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    action.accept(new MutableEntry(mc, i, key, (char) (entry >>> 16)));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public Map.Entry<Short, Character> elem() {
            short curKey;
            if ((curKey = this.curKey) != free) {
                return new MutableEntry(expectedModCount, index, curKey, curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                int[] tab = this.tab;
                short free = this.free;
                int entry;
                for (int i = index - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        index = i;
                        curKey = key;
                        curValue = (char) (entry >>> 16);
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
            short free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedEntryIterator implements ObjIterator<Map.Entry<Short, Character>> {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        MutableEntry next;

        SomeRemovedEntryIterator(int mc) {
            expectedModCount = mc;
            int[] tab = this.tab = table;
            short free = this.free = freeValue;
            short removed = this.removed = removedValue;
            int nextI = tab.length;
            int entry;
            while (--nextI >= 0) {
                short key;
                if ((key = (short) (entry = tab[nextI])) != free && key != removed) {
                    next = new MutableEntry(mc, nextI, key, (char) (entry >>> 16));
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull Consumer<? super Map.Entry<Short, Character>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            short removed = this.removed;
            int entry;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    action.accept(new MutableEntry(mc, i, key, (char) (entry >>> 16)));
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
        public Map.Entry<Short, Character> next() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                int mc;
                if ((mc = expectedModCount) == modCount()) {
                    index = nextI;
                    int[] tab = this.tab;
                    short free = this.free;
                    short removed = this.removed;
                    MutableEntry prev = next;
                    int entry;
                    while (--nextI >= 0) {
                        short key;
                        if ((key = (short) (entry = tab[nextI])) != free && key != removed) {
                            next = new MutableEntry(mc, nextI, key, (char) (entry >>> 16));
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
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedEntryCursor implements ObjCursor<Map.Entry<Short, Character>> {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index;
        short curKey;
        char curValue;

        SomeRemovedEntryCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            short free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Short, Character>> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            short removed = this.removed;
            int entry;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    action.accept(new MutableEntry(mc, i, key, (char) (entry >>> 16)));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public Map.Entry<Short, Character> elem() {
            short curKey;
            if ((curKey = this.curKey) != free) {
                return new MutableEntry(expectedModCount, index, curKey, curValue);
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                int[] tab = this.tab;
                short free = this.free;
                short removed = this.removed;
                int entry;
                for (int i = index - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        index = i;
                        curKey = key;
                        curValue = (char) (entry >>> 16);
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
            short free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }




    class NoRemovedValueIterator implements CharIterator {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        char next;

        NoRemovedValueIterator(int mc) {
            expectedModCount = mc;
            int[] tab = this.tab = table;
            short free = this.free = freeValue;
            this.removed = removedValue;
            int nextI = tab.length;
            int entry;
            while (--nextI >= 0) {
                if ((short) (entry = tab[nextI]) != free) {
                    next = (char) (entry >>> 16);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public char nextChar() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                if (expectedModCount == modCount()) {
                    index = nextI;
                    int[] tab = this.tab;
                    short free = this.free;
                    char prev = next;
                    int entry;
                    while (--nextI >= 0) {
                        if ((short) (entry = tab[nextI]) != free) {
                            next = (char) (entry >>> 16);
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
        public void forEachRemaining(Consumer<? super Character> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            int entry;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if ((short) (entry = tab[i]) != free) {
                    action.accept((char) (entry >>> 16));
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }

        @Override
        public void forEachRemaining(CharConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            int entry;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if ((short) (entry = tab[i]) != free) {
                    action.accept((char) (entry >>> 16));
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
        public Character next() {
            return nextChar();
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class NoRemovedValueCursor implements CharCursor {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index;
        short curKey;
        char curValue;

        NoRemovedValueCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            short free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(CharConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            int entry;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                if ((short) (entry = tab[i]) != free) {
                    action.accept((char) (entry >>> 16));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public char elem() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                int[] tab = this.tab;
                short free = this.free;
                int entry;
                for (int i = index - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        index = i;
                        curKey = key;
                        curValue = (char) (entry >>> 16);
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
            short free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedValueIterator implements CharIterator {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index = -1;
        int nextIndex;
        char next;

        SomeRemovedValueIterator(int mc) {
            expectedModCount = mc;
            int[] tab = this.tab = table;
            short free = this.free = freeValue;
            short removed = this.removed = removedValue;
            int nextI = tab.length;
            int entry;
            while (--nextI >= 0) {
                short key;
                if ((key = (short) (entry = tab[nextI])) != free && key != removed) {
                    next = (char) (entry >>> 16);
                    break;
                }
            }
            nextIndex = nextI;
        }

        @Override
        public char nextChar() {
            int nextI;
            if ((nextI = nextIndex) >= 0) {
                if (expectedModCount == modCount()) {
                    index = nextI;
                    int[] tab = this.tab;
                    short free = this.free;
                    short removed = this.removed;
                    char prev = next;
                    int entry;
                    while (--nextI >= 0) {
                        short key;
                        if ((key = (short) (entry = tab[nextI])) != free && key != removed) {
                            next = (char) (entry >>> 16);
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
        public void forEachRemaining(Consumer<? super Character> action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            short removed = this.removed;
            int entry;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    action.accept((char) (entry >>> 16));
                }
            }
            if (nextI != nextIndex || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            index = nextIndex = -1;
        }

        @Override
        public void forEachRemaining(CharConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            short removed = this.removed;
            int entry;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    action.accept((char) (entry >>> 16));
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
        public Character next() {
            return nextChar();
        }

        @Override
        public void remove() {
            int index;
            if ((index = this.index) >= 0) {
                if (expectedModCount++ == modCount()) {
                    this.index = -1;
                    incrementModCount();
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }


    class SomeRemovedValueCursor implements CharCursor {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index;
        short curKey;
        char curValue;

        SomeRemovedValueCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            short free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(CharConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            short removed = this.removed;
            int entry;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    action.accept((char) (entry >>> 16));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public char elem() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if (expectedModCount == modCount()) {
                int[] tab = this.tab;
                short free = this.free;
                short removed = this.removed;
                int entry;
                for (int i = index - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        index = i;
                        curKey = key;
                        curValue = (char) (entry >>> 16);
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
            short free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }



    class NoRemovedMapCursor implements ShortCharCursor {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index;
        short curKey;
        char curValue;

        NoRemovedMapCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            short free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(ShortCharConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            int entry;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free) {
                    action.accept(key, (char) (entry >>> 16));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public short key() {
            short curKey;
            if ((curKey = this.curKey) != free) {
                return curKey;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public char value() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(char value) {
            if (curKey != free) {
                if (expectedModCount == modCount()) {
                    U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), value);
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
                int[] tab = this.tab;
                short free = this.free;
                int entry;
                for (int i = index - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free) {
                        index = i;
                        curKey = key;
                        curValue = (char) (entry >>> 16);
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
            short free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
                    postRemoveHook();
                } else {
                    throw new java.util.ConcurrentModificationException();
                }
            } else {
                throw new java.lang.IllegalStateException();
            }
        }
    }

    class SomeRemovedMapCursor implements ShortCharCursor {
        final int[] tab;
        final short free;
        final short removed;
        int expectedModCount;
        int index;
        short curKey;
        char curValue;

        SomeRemovedMapCursor(int mc) {
            expectedModCount = mc;
            this.tab = table;
            index = tab.length;
            short free = this.free = freeValue;
            this.removed = removedValue;
            curKey = free;
        }

        @Override
        public void forEachForward(ShortCharConsumer action) {
            if (action == null)
                throw new java.lang.NullPointerException();
            int mc = expectedModCount;
            int[] tab = this.tab;
            short free = this.free;
            short removed = this.removed;
            int entry;
            int index = this.index;
            for (int i = index - 1; i >= 0; i--) {
                short key;
                if ((key = (short) (entry = tab[i])) != free && key != removed) {
                    action.accept(key, (char) (entry >>> 16));
                }
            }
            if (index != this.index || mc != modCount()) {
                throw new java.util.ConcurrentModificationException();
            }
            this.index = -1;
            curKey = free;
        }

        @Override
        public short key() {
            short curKey;
            if ((curKey = this.curKey) != free) {
                return curKey;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }

        @Override
        public char value() {
            if (curKey != free) {
                return curValue;
            } else {
                throw new java.lang.IllegalStateException();
            }
        }


        @Override
        public void setValue(char value) {
            if (curKey != free) {
                if (expectedModCount == modCount()) {
                    U.putChar(tab, INT_BASE + CHAR_VALUE_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), value);
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
                int[] tab = this.tab;
                short free = this.free;
                short removed = this.removed;
                int entry;
                for (int i = index - 1; i >= 0; i--) {
                    short key;
                    if ((key = (short) (entry = tab[i])) != free && key != removed) {
                        index = i;
                        curKey = key;
                        curValue = (char) (entry >>> 16);
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
            short free;
            if (curKey != (free = this.free)) {
                if (expectedModCount++ == modCount()) {
                    this.curKey = free;
                    incrementModCount();
                    U.putShort(tab, INT_BASE + SHORT_KEY_OFFSET + (((long) (index)) << INT_SCALE_SHIFT), removed);
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
