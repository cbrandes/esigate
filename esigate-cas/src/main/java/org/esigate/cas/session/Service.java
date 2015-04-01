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
 * Object used to maintain a correspondance between CAS ticket ID and remote application. Used in single sign out.
 *
 * @author Cedric Brandes
 * @version $Revision$ $Date$
 * @since 5.0-beta-3
 *
 */
public class Service {
    private final String remoteUrl;
    private final String ticketId;

    public Service(String remoteUrl, String ticketId) {
        super();
        this.remoteUrl = remoteUrl;
        this.ticketId = ticketId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Service)) {
            return false;
        }
        return remoteUrl.equals(((Service) obj).remoteUrl);
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public String getTicketId() {
        return ticketId;
    }

}
