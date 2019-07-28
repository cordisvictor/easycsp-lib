/*
 * Copyright 2011 Victor Cordis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Please contact the author ( cordis.victor@gmail.com ) if you need additional
 * information or have any questions.
 */
package net.sourceforge.easycsp;

/**
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.0
 * @since 1.0
 */
public class OverconstrainedCSPException extends Exception {

    /**
     * Constructs an instance with the specified detail message.
     *
     * @param msg the detail message.
     */
    public OverconstrainedCSPException(String msg) {
        super(msg);
    }
}
