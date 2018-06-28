package steam.servers;

import java.net.InetSocketAddress;

public class SourceServer {

    private InetSocketAddress host;

    public SourceServer(InetSocketAddress host) {
        this.host = host;
    }

    public InetSocketAddress getHost() {
        return host;
    }

    public void setHost(InetSocketAddress host) {
        this.host = host;
    }
}
