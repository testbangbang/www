/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onyx.android.sdk.sync;

import com.onyx.android.sdk.data.cms.OnyxCmsAggregatedData;
import com.onyx.android.sdk.data.cms.OnyxPosition;
import com.onyx.android.sdk.data.cms.OnyxMetadata;
import com.onyx.android.sdk.data.cms.OnyxBookmark;
import com.onyx.android.sdk.data.cms.OnyxAnnotation;

/**
 * Example of defining an interface for calling on to a remote service
 * (running in another process).
 */
interface IOnyxSyncService {
    /**
     * Often you want to allow a service to call back to its clients.
     * This shows how to do so, by registering a callback interface with
     * the service.
     */
	int sync(String application, String isbn);
	boolean cancel(int syncId);
}
