/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate.cache;

import java.util.Properties;

import org.apache.http.impl.client.cache.BasicHttpCacheStorage;
import org.apache.http.impl.client.cache.CacheConfig;

public class BasicCacheStorage extends CacheStorage {

    @Override
    public void init(Properties properties) {
        CacheConfig cacheConfig = CacheConfigHelper.createCacheConfig(properties);
        setImpl(new BasicHttpCacheStorage(cacheConfig));
    }

}
