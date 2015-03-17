/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/index.html
 */
package org.esigate.cas.session;


/**
 * Stores the mapping between sessions and keys to be retrieved later.
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.1
 *
 */
public interface SessionMappingStorage extends org.jasig.cas.client.session.SessionMappingStorage {

    public final String SERVICES = "ServicesUrls";

    void addServiceForSessionId(String sessionId, Service service);

}
