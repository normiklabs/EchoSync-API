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
 * Primary entrypoint for third-party Minecraft mods to open multiplexed real-time
 * synchronization channels over the EchoSync network.
 *
 * <p>All channels opened via {@link #channel(String, String)} share a single physical
 * WebSocket connection per Minecraft instance managed by {@code echosync-mod}. If
 * {@code echosync-mod} is not installed at runtime, a safe No-Op channel is returned
 * so the calling mod never crashes Minecraft startup.</p>
 */
@SuppressWarnings("unused")
public interface EchoSync {

    /**
     * Opens or retrieves the multiplexed {@link EchoSyncChannel} for the given mod namespace
     * and API key.
     *
     * @param modNamespace the registered Java-style project namespace (e.g., {@code de.devknochen.mymod}, matching {@code ^[a-z][a-z0-9_-]*(\.[a-z0-9_-]+)+$})
     * @param apiKey       a Publishable Client Key ({@code epk_live_...}) or Secret Server Key ({@code esk_live_...})
     * @return the thread-safe {@link EchoSyncChannel} instance
     */
    static EchoSyncChannel channel(String modNamespace, String apiKey) {
        return EchoSyncProvider.load().openChannel(modNamespace, apiKey);
    }
}
