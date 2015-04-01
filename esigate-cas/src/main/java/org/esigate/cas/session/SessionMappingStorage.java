/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.esigate.cas.session;

/**
 * Stores the mapping between sessions and keys to be retrieved later.
 *
 * @author Scott Battaglia
 * @author Cedric Brandes
 * @version $Revision$ $Date$
 * @since 5.0-beta-3
 *
 */
public interface SessionMappingStorage extends org.jasig.cas.client.session.SessionMappingStorage {

    public final String SERVICES = "ServicesUrls";

    void addServiceForSessionId(String sessionId, Service service);

}
