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

package dk.martinu.kofi;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * Signals that an error occurred while parsing data to a {@link Document}.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class ParseException extends Exception implements Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs a new exception with the specified message.
     */
    @Contract(pure = true)
    public ParseException(@NotNull final String msg) {
        super(msg);
    }

    /**
     * Constructs a new exception with the specified, line, message and cause.
     */
    // TODO retain line/column information or implement better constructors
    @Contract(pure = true)
    public ParseException(final int line, @NotNull final String msg, @NotNull final Throwable cause) {
        super("line " + line + " {" + msg + "}", cause);
    }
}
