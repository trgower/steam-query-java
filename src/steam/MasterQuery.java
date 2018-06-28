package steam;

import java.net.InetSocketAddress;

public class MasterQuery {

    private byte regionCode;
    private InetSocketAddress host;
    private String filter;

    public MasterQuery(byte regionCode, InetSocketAddress host, String filter) {
        this.regionCode = regionCode;
        this.host = host;
        this.filter = filter;
    }

    public byte getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(byte regionCode) {
        this.regionCode = regionCode;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public InetSocketAddress getHost() {
        return host;
    }

    public void setHost(InetSocketAddress host) {
        this.host = host;
    }
}
