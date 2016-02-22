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

import net.openhft.koloboke.collect.hash.ShortHashFactory;
import net.openhft.koloboke.collect.hash.HashConfig;
import net.openhft.koloboke.collect.impl.Primitives;

import java.util.Random;
import java.util.concurrent
     .ThreadLocalRandom;


abstract class ShortHashFactorySO extends AbstractHashFactory {

    final short lower, upper;
    final boolean randomFree, randomRemoved;
    final short freeValue, removedValue;

    ShortHashFactorySO(HashConfig hashConf, int defaultExpectedSize, short lower, short upper) {
        super(hashConf, defaultExpectedSize);
        this.lower = lower;
        this.upper = upper;
        if ((short) (lower - 1) == upper) {
            // free key = 0 by default vs. random free key:
            //
            // Assuming most hash table instances don't contain zero key during the lifetime,
            // we safe one table iteration with filling free key during hash table construction,
            // instead we sacrifice extra free key replacement if zero key is inserted
            // into the hash.
            //
            // So by choosing free key = 0 by default we speedup most hash instantiation sites,
            // and slowdown less frequent ones where zero key is very likely or always present.
            // In the latter cases someone can specify fictional keys domain, to force this method
            // to choose different free key.
            //
            // However, it requires from the library user to be familiar with this comment
            // and to be aware, that the library never throws IllegalStateException on insertion
            // a key out of the specified keys domain (Javadocs say that it is implementation choice
            // to throw an exception or silently ignore this situation.)
            randomFree = false;
            randomRemoved = true;
            freeValue = removedValue = (short) 0;
        } else {
            randomFree = false;
            if ((lower < upper && (lower > 0 || upper < 0)) ||
                    (upper < lower && (lower > 0 && upper < 0))) {
                freeValue = (short) 0;
            } else {
                freeValue = (short) (lower - 1);
            }
            if ((short) (lower - 2) == upper) {
                randomRemoved = true;
                removedValue = (short) 0;
            } else {
                randomRemoved = false;
                if (upper + 1 != 0) {
                    removedValue = (short) (upper + 1);
                } else {
                    removedValue = (short) (upper + 2);
                }
            }
        }
    }

    public final short getLowerKeyDomainBound() {
        return lower;
    }

    public final short getUpperKeyDomainBound() {
        return upper;
    }

    short getFree() {
        if (randomFree) {
            Random random = ThreadLocalRandom.current();
            return (short) random./* nextIntOrLong */nextInt/**/();
        } else {
            return freeValue;
        }
    }

    String keySpecialString() {
        return ",lowerKeyDomainBound=" + boundAsString(getLowerKeyDomainBound()) +
                ",upperKeyDomainBound=" + boundAsString(getUpperKeyDomainBound());
    }

    /**
     * To distinguish non-printable characters in debug output
     */
    private static String boundAsString(short bound) {
        return "" + bound;
    }

    int keySpecialHashCode(int hashCode) {
        hashCode = hashCode * 31 + Primitives.hashCode(getLowerKeyDomainBound());
        return hashCode * 31 + Primitives.hashCode(getUpperKeyDomainBound());
    }

    boolean keySpecialEquals(ShortHashFactory other) {
        return getLowerKeyDomainBound() == other.getLowerKeyDomainBound() &&
                getUpperKeyDomainBound() == other.getUpperKeyDomainBound();
    }
}
