/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.esigate.cas.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.1
 */
public final class HttpClient implements Serializable {

    private static final class MessageSender implements Callable<Boolean> {

        private final int connectionTimeout;

        private final boolean followRedirects;

        private final String message;

        private final int readTimeout;

        private final String url;

        public MessageSender(final String url, final String message, final int readTimeout,
                final int connectionTimeout, final boolean followRedirects) {
            this.url = url;
            this.message = message;
            this.readTimeout = readTimeout;
            this.connectionTimeout = connectionTimeout;
            this.followRedirects = followRedirects;
        }

        public Boolean call() throws Exception {
            HttpURLConnection connection = null;
            BufferedReader in = null;
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Attempting to access " + url);
                }
                final URL logoutUrl = new URL(url);
                final String output = "logoutRequest=" + URLEncoder.encode(message, "UTF-8");

                connection = (HttpURLConnection) logoutUrl.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setReadTimeout(this.readTimeout);
                connection.setConnectTimeout(this.connectionTimeout);
                connection.setInstanceFollowRedirects(this.followRedirects);
                connection.setRequestProperty("Content-Length", Integer.toString(output.getBytes().length));
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                final DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
                printout.writeBytes(output);
                printout.flush();
                printout.close();

                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while (in.readLine() != null) {
                    // nothing to do
                }

                if (log.isDebugEnabled()) {
                    log.debug("Finished sending message to" + url);
                }
                return true;
            } catch (final SocketTimeoutException e) {
                log.warn("Socket Timeout Detected while attempting to send message to [" + url + "].");
                return false;
            } catch (final Exception e) {
                log.warn("Error Sending message to url endpoint [" + url + "].  Error is [" + e.getMessage() + "]");
                return false;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (final IOException e) {
                        // can't do anything
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

    }

    private static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(100);

    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

    /** Unique Id for serialization. */
    private static final long serialVersionUID = -5306738686476129516L;

    /** List of HTTP status codes considered valid by this AuthenticationHandler. */

    private final int connectionTimeout = 5000;

    private final boolean followRedirects = true;

    private final int readTimeout = 5000;

    public void destroy() throws Exception {
        EXECUTOR_SERVICE.shutdown();
    }

    /**
     * Sends a message to a particular endpoint. Option of sending it without waiting to ensure a response was returned.
     * <p>
     * This is useful when it doesn't matter about the response as you'll perform no action based on the response.
     *
     * @param url
     *            the url to send the message to
     * @param message
     *            the message itself
     * @param async
     *            true if you don't want to wait for the response, false otherwise.
     * @return boolean if the message was sent, or async was used. false if the message failed.
     */
    public boolean sendMessageToEndPoint(final String url, final String message, final boolean async) {
        final Future<Boolean> result =
                EXECUTOR_SERVICE.submit(new MessageSender(url, message, this.readTimeout, this.connectionTimeout,
                        this.followRedirects));

        if (async) {
            return true;
        }

        try {
            return result.get();
        } catch (final Exception e) {
            return false;
        }
    }

}
