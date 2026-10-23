/*
 * Copyright 2026 The normik Authors
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

package org.normik.echosync.api;

/**
 * Handle representing an active topic or connection-state subscription.
 *
 * <p>Calling {@link #close()} unregisters the listener and, if no other listeners remain
 * on that topic for the mod slot, sends an {@code UNSUB} control frame to the relay.</p>
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface Subscription extends AutoCloseable {

    /**
     * Unregisters the associated listener. Idempotent and never throws checked exceptions.
     */
    @Override
    void close();
}
