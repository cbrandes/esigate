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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * HashMap backed implementation of SessionMappingStorage.
 *
 * @author Scott Battaglia
 * @author Cedric Brandes
 * @version $Revision$ $Date$
 * @since 5.0-beta-3
 *
 */
public final class HashMapBackedSessionMappingStorage implements SessionMappingStorage {

    /**
     * Maps the Session ID to the key from the CAS Server.
     */
    private final Map<String, String> ID_TO_SESSION_KEY_MAPPING = new HashMap<String, String>();

    private final Log log = LogFactory.getLog(getClass());
    /**
     * Maps the ID from the CAS server to the Session.
     */
    private final Map<String, HttpSession> MANAGED_SESSIONS = new HashMap<String, HttpSession>();

    @Override
    public synchronized void addServiceForSessionId(String sessionId, Service service) {
        final String key = ID_TO_SESSION_KEY_MAPPING.get(sessionId);
        final HttpSession session = MANAGED_SESSIONS.get(key);

        if (session != null) {
            Set<Service> services = (Set<Service>) session.getAttribute(SERVICES);
            if (services == null) {
                services = new HashSet<Service>();
            }
            services.add(service);
            session.setAttribute(SERVICES, services);
            MANAGED_SESSIONS.put(key, session);
        }
    }

    public synchronized void addSessionById(String mappingId, HttpSession session) {
        ID_TO_SESSION_KEY_MAPPING.put(session.getId(), mappingId);
        MANAGED_SESSIONS.put(mappingId, session);

    }

    public synchronized void removeBySessionById(String sessionId) {
        if (log.isDebugEnabled()) {
            log.debug("Attempting to remove Session=[" + sessionId + "]");
        }
        final String key = ID_TO_SESSION_KEY_MAPPING.get(sessionId);

        if (log.isDebugEnabled()) {
            if (key != null) {
                log.debug("Found mapping for session.  Session Removed.");
            } else {
                log.debug("No mapping for session found.  Ignoring.");
            }
        }
        MANAGED_SESSIONS.remove(key);
        ID_TO_SESSION_KEY_MAPPING.remove(sessionId);

    }

    public synchronized HttpSession removeSessionByMappingId(String mappingId) {
        final HttpSession session = MANAGED_SESSIONS.get(mappingId);

        if (session != null) {
            removeBySessionById(session.getId());
        }

        return session;
    }
}
