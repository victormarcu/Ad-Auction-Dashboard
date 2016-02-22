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
import net.openhft.koloboke.function.ByteConsumer;
import net.openhft.koloboke.function.BytePredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.openhft.koloboke.collect.impl.*;
import net.openhft.koloboke.collect.set.ByteSet;
import net.openhft.koloboke.collect.set.hash.HashByteSet;
import javax.annotation.Nonnull;

import java.util.*;


public abstract class ImmutableLHashSeparateKVByteKeyMap
        extends ImmutableSeparateKVByteLHashGO {



    public final boolean containsKey(Object key) {
        return contains(key);
    }

    public boolean containsKey(byte key) {
        return contains(key);
    }


    @Nonnull
    public HashByteSet keySet() {
        return new KeyView();
    }


    abstract boolean justRemove(byte key);


    class KeyView extends AbstractByteKeyView
            implements HashByteSet, InternalByteCollectionOps, SeparateKVByteLHash {


        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return ImmutableLHashSeparateKVByteKeyMap.this.hashConfig();
        }

        @Override
        public HashConfigWrapper configWrapper() {
            return ImmutableLHashSeparateKVByteKeyMap.this.configWrapper();
        }

        @Override
        public int size() {
            return ImmutableLHashSeparateKVByteKeyMap.this.size();
        }

        @Override
        public double currentLoad() {
            return ImmutableLHashSeparateKVByteKeyMap.this.currentLoad();
        }

        @Override
        public byte freeValue() {
            return ImmutableLHashSeparateKVByteKeyMap.this.freeValue();
        }

        @Override
        public boolean supportRemoved() {
            return ImmutableLHashSeparateKVByteKeyMap.this.supportRemoved();
        }

        @Override
        public byte removedValue() {
            return ImmutableLHashSeparateKVByteKeyMap.this.removedValue();
        }

        @Nonnull
        @Override
        public byte[] keys() {
            return ImmutableLHashSeparateKVByteKeyMap.this.keys();
        }

        @Override
        public int capacity() {
            return ImmutableLHashSeparateKVByteKeyMap.this.capacity();
        }

        @Override
        public int freeSlots() {
            return ImmutableLHashSeparateKVByteKeyMap.this.freeSlots();
        }

        @Override
        public boolean noRemoved() {
            return ImmutableLHashSeparateKVByteKeyMap.this.noRemoved();
        }

        @Override
        public int removedSlots() {
            return ImmutableLHashSeparateKVByteKeyMap.this.removedSlots();
        }

        @Override
        public int modCount() {
            return ImmutableLHashSeparateKVByteKeyMap.this.modCount();
        }

        @Override
        public final boolean contains(Object o) {
            return ImmutableLHashSeparateKVByteKeyMap.this.contains(o);
        }

        @Override
        public boolean contains(byte key) {
            return ImmutableLHashSeparateKVByteKeyMap.this.contains(key);
        }



        @Override
        public void forEach(Consumer<? super Byte> action) {
            ImmutableLHashSeparateKVByteKeyMap.this.forEach(action);
        }

        @Override
        public void forEach(ByteConsumer action) {
            ImmutableLHashSeparateKVByteKeyMap.this.forEach(action);
        }

        @Override
        public boolean forEachWhile(BytePredicate
                predicate) {
            return ImmutableLHashSeparateKVByteKeyMap.this.forEachWhile(predicate);
        }

        @Override
        public boolean allContainingIn(ByteCollection c) {
            return ImmutableLHashSeparateKVByteKeyMap.this.allContainingIn(c);
        }

        @Override
        public boolean reverseAddAllTo(ByteCollection c) {
            return ImmutableLHashSeparateKVByteKeyMap.this.reverseAddAllTo(c);
        }

        @Override
        public boolean reverseRemoveAllFrom(ByteSet s) {
            return ImmutableLHashSeparateKVByteKeyMap.this.reverseRemoveAllFrom(s);
        }

        @Override
        @Nonnull
        public ByteIterator iterator() {
            return ImmutableLHashSeparateKVByteKeyMap.this.iterator();
        }

        @Override
        @Nonnull
        public ByteCursor cursor() {
            return setCursor();
        }

        @Override
        @Nonnull
        public Object[] toArray() {
            return ImmutableLHashSeparateKVByteKeyMap.this.toArray();
        }

        @Override
        @Nonnull
        public <T> T[] toArray(@Nonnull T[] a) {
            return ImmutableLHashSeparateKVByteKeyMap.this.toArray(a);
        }

        @Override
        public byte[] toByteArray() {
            return ImmutableLHashSeparateKVByteKeyMap.this.toByteArray();
        }

        @Override
        public byte[] toArray(byte[] a) {
            return ImmutableLHashSeparateKVByteKeyMap.this.toArray(a);
        }


        @Override
        public int hashCode() {
            return setHashCode();
        }

        @Override
        public String toString() {
            return setToString();
        }


        @Override
        public boolean shrink() {
            return ImmutableLHashSeparateKVByteKeyMap.this.shrink();
        }


        @Override
        public final boolean remove(Object o) {
            return justRemove((Byte) o);
        }

        @Override
        public boolean removeByte(byte v) {
            return justRemove(v);
        }



        @Override
        public boolean removeIf(Predicate<? super Byte> filter) {
            return ImmutableLHashSeparateKVByteKeyMap.this.removeIf(filter);
        }

        @Override
        public boolean removeIf(BytePredicate filter) {
            return ImmutableLHashSeparateKVByteKeyMap.this.removeIf(filter);
        }

        @Override
        public boolean removeAll(@Nonnull Collection<?> c) {
            if (c instanceof ByteCollection) {
                if (c instanceof InternalByteCollectionOps) {
                    InternalByteCollectionOps c2 = (InternalByteCollectionOps) c;
                    if (c2.size() < this.size()) {
                        
                        return c2.reverseRemoveAllFrom(this);
                    }
                }
                return ImmutableLHashSeparateKVByteKeyMap.this.removeAll(this, (ByteCollection) c);
            }
            return ImmutableLHashSeparateKVByteKeyMap.this.removeAll(this, c);
        }

        @Override
        public boolean retainAll(@Nonnull Collection<?> c) {
            return ImmutableLHashSeparateKVByteKeyMap.this.retainAll(this, c);
        }

        @Override
        public void clear() {
            ImmutableLHashSeparateKVByteKeyMap.this.clear();
        }
    }
}
