package org.esigate.cas.session;

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
