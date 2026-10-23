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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EchoSyncApiTest {

    @BeforeEach
    void setUp() {
        EchoSyncProvider.resetForTesting();
    }

    @Test
    void customChannelImplementationShouldSupportConnectedState() {
        EchoSyncChannel connectedChannel = new EchoSyncChannel() {
            @Override
            public CompletableFuture<Void> send(String topic, byte[] payload) {
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public Subscription subscribe(String topic, Consumer<EchoPacket> listener) {
                return () -> {};
            }

            @Override
            public ConnectionState state() {
                return ConnectionState.CONNECTED;
            }

            @Override
            public Subscription onStateChange(Consumer<ConnectionState> listener) {
                return () -> {};
            }
        };
        assertEquals(ConnectionState.CONNECTED, connectedChannel.state());
    }

    @Test
    void echoPacketShouldDefensivelyClonePayloadAndEnforceNullGuards() {
        UUID sender = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        byte[] original = new byte[] {1, 2, 3, 4};

        EchoPacket packet = new EchoPacket("pub:voice", sender, original, 1700000000000L);

        // Mutating caller array must not mutate packet
        original[0] = 99;
        assertArrayEquals(new byte[] {1, 2, 3, 4}, packet.payload());

        // Mutating returned payload array must not mutate packet
        byte[] returned = packet.payload();
        returned[1] = 88;
        assertArrayEquals(new byte[] {1, 2, 3, 4}, packet.payload());

        // Value equality & hashCode on byte[] content
        EchoPacket identical = new EchoPacket("pub:voice", sender, new byte[] {1, 2, 3, 4}, 1700000000000L);
        EchoPacket different = new EchoPacket("pub:voice", sender, new byte[] {1, 2, 3, 5}, 1700000000000L);
        assertEquals(packet, identical);
        assertEquals(packet.hashCode(), identical.hashCode());
        assertNotEquals(packet, different);

        assertThrows(NullPointerException.class, () -> new EchoPacket(null, sender, new byte[0], 0L));
        assertThrows(NullPointerException.class, () -> new EchoPacket("pub:test", null, new byte[0], 0L));
        assertThrows(NullPointerException.class, () -> new EchoPacket("pub:test", sender, null, 0L));
    }

    @Test
    @SuppressWarnings("resource")
    void serviceProviderShouldFallbackToSafeNoOpChannelWhenModRuntimeMissing() {
        EchoSyncChannel channel = EchoSync.channel("de.devknochen.testmod", "epk_live_0123456789abcdef0123456789abcdef");
        assertNotNull(channel);
        assertEquals(ConnectionState.DISCONNECTED, channel.state());

        CompletableFuture<Void> sendFuture = channel.send("pub:ping", new byte[] {0x01});
        assertDoesNotThrow(sendFuture::join);

        try (Subscription sub = channel.subscribe("pub:ping", Objects::requireNonNull);
                Subscription stateSub = channel.onStateChange(Objects::requireNonNull)) {
            assertNotNull(sub);
            assertNotNull(stateSub);
        }

        assertThrows(NullPointerException.class, () -> EchoSync.channel(null, "epk_live_00000000000000000000000000000001"));
        assertThrows(NullPointerException.class, () -> EchoSync.channel("de.devknochen.testmod", null));
        assertThrows(NullPointerException.class, () -> channel.send(null, new byte[0]));
        assertThrows(NullPointerException.class, () -> channel.send("pub:ping", null));
        assertThrows(NullPointerException.class, () -> channel.subscribe(null, Objects::requireNonNull));
        assertThrows(NullPointerException.class, () -> channel.subscribe("pub:ping", null));
        assertThrows(NullPointerException.class, () -> channel.onStateChange(null));
    }
}
