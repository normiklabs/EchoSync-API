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

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Discovers and caches the runtime {@link EchoSyncServiceProvider} via {@link ServiceLoader}.
 *
 * <p>If {@code echosync-mod} is not present on the classpath, this loader logs a one-time
 * warning and returns a safe No-Op provider so third-party mods degrade gracefully without
 * crashing Minecraft.</p>
 */
@SuppressWarnings("unused")
public final class EchoSyncProvider {

    private static final Logger LOGGER = System.getLogger(EchoSyncProvider.class.getName());
    private static final Subscription NO_OP_SUBSCRIPTION = () -> {};
    private static final EchoSyncChannel NO_OP_CHANNEL = new NoOpEchoSyncChannel();
    private static final EchoSyncServiceProvider NO_OP_PROVIDER = (modNamespace, apiKey) -> {
        Objects.requireNonNull(modNamespace, "modNamespace must not be null");
        Objects.requireNonNull(apiKey, "apiKey must not be null");
        return NO_OP_CHANNEL;
    };

    private static volatile EchoSyncServiceProvider cachedProvider;

    private EchoSyncProvider() {}

    /**
     * Loads or returns the cached {@link EchoSyncServiceProvider}.
     *
     * @return the active runtime provider, or the safe No-Op fallback provider
     */
    public static EchoSyncServiceProvider load() {
        EchoSyncServiceProvider current = cachedProvider;
        if (current != null) {
            return current;
        }
        synchronized (EchoSyncProvider.class) {
            if (cachedProvider == null) {
                cachedProvider = discoverProvider(Thread.currentThread().getContextClassLoader());
            }
            return cachedProvider;
        }
    }

    static EchoSyncServiceProvider discoverProvider(ClassLoader classLoader) {
        ServiceLoader<EchoSyncServiceProvider> loader = ServiceLoader.load(EchoSyncServiceProvider.class, classLoader);
        return loader.findFirst().orElseGet(() -> {
            LOGGER.log(
                    Level.WARNING,
                    "echosync-mod runtime not found on classpath; returning safe No-Op EchoSyncServiceProvider fallback"
            );
            return NO_OP_PROVIDER;
        });
    }

    static void resetForTesting() {
        cachedProvider = null;
    }

    private static final class NoOpEchoSyncChannel implements EchoSyncChannel {

        @Override
        public CompletableFuture<Void> send(String topic, byte[] payload) {
            Objects.requireNonNull(topic, "topic must not be null");
            Objects.requireNonNull(payload, "payload must not be null");
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public Subscription subscribe(String topic, Consumer<EchoPacket> listener) {
            Objects.requireNonNull(topic, "topic must not be null");
            Objects.requireNonNull(listener, "listener must not be null");
            return NO_OP_SUBSCRIPTION;
        }

        @Override
        public ConnectionState state() {
            return ConnectionState.DISCONNECTED;
        }

        @Override
        public Subscription onStateChange(Consumer<ConnectionState> listener) {
            Objects.requireNonNull(listener, "listener must not be null");
            return NO_OP_SUBSCRIPTION;
        }
    }
}
