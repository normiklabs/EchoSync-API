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
 * Service Provider Interface (SPI) implemented by {@code echosync-mod} and discovered
 * via {@link java.util.ServiceLoader}.
 */
@SuppressWarnings("unused")
public interface EchoSyncServiceProvider {

    /**
     * Opens or returns an existing multiplexed channel for the given mod namespace and API key.
     *
     * @param modNamespace the mod project namespace
     * @param apiKey       the Publishable ({@code epk_}) or Secret ({@code esk_}) API key
     * @return the {@link EchoSyncChannel}
     */
    EchoSyncChannel openChannel(String modNamespace, String apiKey);
}
