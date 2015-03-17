/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/index.html
 */
package org.esigate.cas.session;

import java.io.IOException;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.esigate.cas.util.HttpClient;
import org.jasig.cas.client.util.AbstractConfigurationFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.XmlUtils;

/**
 * Implements the Single Sign Out protocol. It handles registering the session and destroying the session.
 * 
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.1
 */
public final class SingleSignOutFilter extends AbstractConfigurationFilter {

    private static HttpClient httpClient = new HttpClient();

    private static Log log = LogFactory.getLog(SingleSignOutFilter.class);
    private static SessionMappingStorage SESSION_MAPPING_STORAGE = new HashMapBackedSessionMappingStorage();

    public static SessionMappingStorage getSessionMappingStorage() {
        return SESSION_MAPPING_STORAGE;
    }

    /**
     * The name of the artifact parameter. This is used to capture the session identifier.
     */
    private String artifactParameterName = "ticket";

    public void destroy() {
        // nothing to do
    }

    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        if ("POST".equals(request.getMethod())) {
            final String logoutRequest = CommonUtils.safeGetParameter(request, "logoutRequest");

            if (CommonUtils.isNotBlank(logoutRequest)) {

                if (log.isTraceEnabled()) {
                    log.trace("Logout request=[" + logoutRequest + "]");
                }

                final String sessionIdentifier = XmlUtils.getTextForElement(logoutRequest, "SessionIndex");

                if (CommonUtils.isNotBlank(sessionIdentifier)) {
                    final HttpSession session = SESSION_MAPPING_STORAGE.removeSessionByMappingId(sessionIdentifier);

                    if (session != null) {
                        String sessionID = session.getId();

                        if (log.isDebugEnabled()) {
                            log.debug("Invalidating session [" + sessionID + "] for ST [" + sessionIdentifier + "]");

                        }
                        Set<Service> services = (Set<Service>) session.getAttribute(SessionMappingStorage.SERVICES);
                        if (services != null && !services.isEmpty()) {
                            for (Service service : services) {
                                log.debug("Service found : " + service.getRemoteUrl());
                                logOutOfService(service);
                            }
                        }
                        try {
                            session.invalidate();
                        } catch (final IllegalStateException e) {
                            log.debug(e, e);
                        }
                    }
                    return;
                }
            }
        } else {
            final String artifact = CommonUtils.safeGetParameter(request, this.artifactParameterName);
            final HttpSession session = request.getSession();

            if (log.isDebugEnabled() && session != null) {
                log.debug("Storing session identifier for " + session.getId());
            }
            if (CommonUtils.isNotBlank(artifact)) {
                try {
                    SESSION_MAPPING_STORAGE.removeBySessionById(session.getId());
                } catch (final Exception e) {
                    // ignore if the session is already marked as invalid.
                    // Nothing we can do!
                }
                SESSION_MAPPING_STORAGE.addSessionById(artifact, session);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void init() {
        CommonUtils.assertNotNull(this.artifactParameterName, "artifactParameterName cannot be null.");
        CommonUtils.assertNotNull(SESSION_MAPPING_STORAGE, "sessionMappingStorage cannote be null.");
    }

    public void init(final FilterConfig filterConfig) throws ServletException {
        if (!isIgnoreInitConfiguration()) {
            setArtifactParameterName(getPropertyFromInitParams(filterConfig, "artifactParameterName", "ticket"));
        }
        init();
    }

    public synchronized boolean logOutOfService(final Service service) {
        final String logoutRequest =
                "<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" ID=\"FAKE\" Version=\"2.0\" IssueInstant=\"somewhere in time\"><saml:NameID xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">@NOT_USED@</saml:NameID><samlp:SessionIndex>"
                        + service.getTicketId() + "</samlp:SessionIndex></samlp:LogoutRequest>";

        if (httpClient != null) {
            return httpClient.sendMessageToEndPoint(service.getRemoteUrl(), logoutRequest, true);
        }

        return false;
    }

    public void setArtifactParameterName(final String artifactParameterName) {
        this.artifactParameterName = artifactParameterName;
    }

    public void setSessionMappingStorage(final SessionMappingStorage storage) {
        SESSION_MAPPING_STORAGE = storage;
    }
}
