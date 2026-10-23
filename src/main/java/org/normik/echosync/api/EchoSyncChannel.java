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

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Represents a multiplexed logical communication channel bound to a specific mod namespace
 * and API key.
 *
 * <p>All methods on this interface are non-blocking and safe to invoke from the Minecraft
 * client render thread, server tick thread, or background worker threads.</p>
 */
@SuppressWarnings({"unused", "SameReturnValue"})
public interface EchoSyncChannel {

    /**
     * Asynchronously publishes a binary payload to the specified topic.
     *
     * <p>The topic must start with {@code pub:}, {@code srv:}, or {@code priv:<playerUuid>:}
     * and must not exceed 64 characters. Total encoded frame size must not exceed 64 KB.</p>
     *
     * @param topic   the target topic (e.g., {@code "pub:chunk_0_0"})
     * @param payload raw binary payload bytes
     * @return a future completing when the frame is written to the relay socket, or failing if disconnected/invalid
     */
    CompletableFuture<Void> send(String topic, byte[] payload);

    /**
     * Subscribes a listener to receive incoming {@link EchoPacket} frames on the given topic.
     *
     * <p>If the relay connection drops and reconnects to a fallback relay, active subscriptions
     * are replayed automatically until the returned {@link Subscription} is closed.</p>
     *
     * @param topic    the topic to subscribe to (e.g., {@code "pub:chunk_0_0"})
     * @param listener callback invoked asynchronously when a packet arrives on the topic
     * @return a handle to unsubscribe the listener
     */
    Subscription subscribe(String topic, Consumer<EchoPacket> listener);

    /**
     * Returns the current lifecycle state of this channel.
     *
     * @return the current {@link ConnectionState}
     */
    ConnectionState state();

    /**
     * Registers a listener invoked whenever this channel's {@link ConnectionState} transitions.
     *
     * @param listener callback receiving the new {@link ConnectionState}
     * @return a handle to unregister the state listener
     */
    Subscription onStateChange(Consumer<ConnectionState> listener);
}
