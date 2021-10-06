/*
 * Copyright (C) 2021 Nick Iacullo
 *
 * This file is part of BoomTrees.
 *
 * BoomTrees is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BoomTrees is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BoomTrees.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.lavabucket.boomtrees.valueproviders;

import java.util.Random;

import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

/**
 * An integer provider that attempts to closely resemble the given float by providing its floor and
 * ceiling values at rates proportionate to its decimal component. The values provided by this class
 * should average close to the float value provided.
 *
 * <p>For example,
 */
public class FloatEquivalentInt extends IntProvider {

    /** The minimum integer that this object can provide. */
    protected final int integralPart;
    /** The rate at which {@code integralPart + 1} is returned. */
    protected final float fractionalPart;

    /**
     * Constructs a new {@code FloatEquivalentInt} object that returns integers with values of
     * {@code integralPart} and {@code integralPart + 1}. The chance that {@code integralPart} is
     * returned is equal to {@code 1 - fractionalPart}.
     *
     * @param integralPart  the minimum number to provide
     * @param fractionalPart  the rate at which {@code integralPart + 1} is returned
     */
    protected FloatEquivalentInt(int integralPart, float fractionalPart) {
        this.integralPart = integralPart;
        this.fractionalPart = fractionalPart;
    }

    /**
     * Returns a new {@code FloatEquivalentInt} object that returns integers with values of
     * {@code integralPart} and {@code integralPart + 1}. The chance that {@code integralPart} is
     * returned is equal to {@code 1 - fractionalPart}.
     *
     * @param integralPart  the minimum number to provide
     * @param fractionalPart  the rate at which {@code integralPart + 1} is returned
     * @return the new {@code FloatEquivalentInt} object
     */
    public static FloatEquivalentInt of(int integralPart, float fractionalPart) {
        return new FloatEquivalentInt(integralPart, fractionalPart);
    }

    /**
     * Returns a new {@code FloatEquivalentInt} object that returns integers with values of the
     * floor and ceiling of {@code value} randomly at a rate that resembles an average of
     * {@code value}.
     *
     * @param value  the value to resemble
     * @return the new {@code FloatEquivalentInt} object
     */
    public static FloatEquivalentInt of(float value) {
        int integralPart = (int) value;
        float fractionalPart = value - integralPart;
        return new FloatEquivalentInt(integralPart, fractionalPart);
    }

    /**
     * {@return an integer sampled from the set of integers from this provider}
     * @param random  a random number generator used to provide randomness to the sample, if needed
    */
    @Override
    public int sample(Random random) {
        if (fractionalPart == 0.0F || random.nextFloat() >= fractionalPart) {
            return integralPart;
        } else {
            return integralPart + 1;
        }
    }

    /** {@return the minimum value this object can provide} */
    @Override
    public int getMinValue() {
        return integralPart;
    }

    /** {@return the maximum value this object can provide} */
    @Override
    public int getMaxValue() {
        return integralPart + (int) Math.ceil(fractionalPart);
    }

    /**
     * {@return null}. This provider's type should not be needed because none of the features that
     * use this should be registered.
     */
    @Override
    public IntProviderType<?> getType() {
        return null;
    }

}
