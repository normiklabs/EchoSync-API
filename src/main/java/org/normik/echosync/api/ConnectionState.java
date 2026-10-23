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
 * Lifecycle state of an {@link EchoSyncChannel}.
 */
@SuppressWarnings("unused")
public enum ConnectionState {
    /** Channel is not connected or was rejected due to invalid credentials. */
    DISCONNECTED,
    /** Querying {@code echosync-master} for recommended regional relay endpoints. */
    DISCOVERING_RELAY,
    /** Opening the physical WebSocket connection to the selected relay node. */
    CONNECTING,
    /** Completing the Mojang Yggdrasil challenge-response handshake and binding the mod slot. */
    AUTHENTICATING,
    /** Bound to an active {@code appSlot} and ready to send and receive binary frames. */
    CONNECTED,
    /** Connection dropped; failing over to the next relay with jittered exponential backoff. */
    RECONNECTING,
    /** The project's monthly packet quota has been exhausted. */
    QUOTA_EXCEEDED
}
