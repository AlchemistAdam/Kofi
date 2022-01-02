/*
 * Copyright (c) 2021, Adam Martinu. All rights reserved. Altering or
 * removing copyright notices or this file header is not allowed.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package dk.martinu.kofi.properties;

import org.jetbrains.annotations.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import dk.martinu.kofi.Property;

/**
 * A {@link Property} that holds a float value.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class FloatProperty extends Property<Float> implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs a new property with the specified key and value. If
     * {@code value} is {@code null}, then the property value will default to
     * {@code 0.0f}.
     *
     * @param key   The property key
     * @param value The property value, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @Contract(pure = true)
    public FloatProperty(@NotNull final String key, @Nullable final Float value) {
        super(key, Objects.requireNonNullElse(value, 0.0f));
    }

    /**
     * Returns a copy of this property with the same property key and value.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public FloatProperty clone() {
        return new FloatProperty(key, value);
    }

    /**
     * Returns {@code Float.class}.
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public Class<Float> getValueClass() {
        return Float.class;
    }

    /**
     * Returns a string representation of this property's value. The returned
     * string is equal to:
     * <pre>
     *     Float.toString(<i>value</i>) + 'f'
     * </pre>
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public String getValueString() {
        //noinspection ConstantConditions
        return Float.toString(value) + 'f';
    }
}
