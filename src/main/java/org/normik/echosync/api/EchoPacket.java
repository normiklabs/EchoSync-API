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

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable data packet delivered by an EchoSync relay.
 *
 * <p>Both the compact constructor and {@link #payload()} accessor defensively clone the
 * underlying byte array so one subscriber cannot corrupt payload data for another subscriber.</p>
 *
 * @param topic       the validated topic name (e.g., {@code "pub:chunk_0_0"})
 * @param senderUuid  the authoritative player UUID (verified via Mojang) or server UUID ({@code 00000000-...}) stamped by the relay
 * @param payload     the raw binary payload bytes
 * @param timestampMs the UTC epoch millisecond timestamp stamped by the relay
 */
@SuppressWarnings("unused")
public record EchoPacket(String topic, UUID senderUuid, byte[] payload, long timestampMs) {

    /**
     * Constructs an immutable {@link EchoPacket}, defensively cloning the payload array.
     */
    public EchoPacket {
        Objects.requireNonNull(topic, "topic must not be null");
        Objects.requireNonNull(senderUuid, "senderUuid must not be null");
        Objects.requireNonNull(payload, "payload must not be null");
        payload = payload.clone();
    }

    /**
     * Returns a defensive copy of the binary payload.
     *
     * @return cloned payload byte array
     */
    @Override
    public byte[] payload() {
        return payload.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EchoPacket that)) return false;
        return timestampMs == that.timestampMs
                && topic.equals(that.topic)
                && senderUuid.equals(that.senderUuid)
                && Arrays.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(topic, senderUuid, timestampMs);
        result = 31 * result + Arrays.hashCode(payload);
        return result;
    }
}
