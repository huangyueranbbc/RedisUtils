package com.hyr.redis;

/*******************************************************************************
 * @date 2018-03-02 下午 4:55
 * @author: <a href=mailto:huangyr@bonree.com>黄跃然</a>
 * @Description: cluster node slots info
 ******************************************************************************/
public class NodeSlots {

    private Long start;
    private Long end;
    private String ip;
    private Long port;

    public NodeSlots(Long start, Long end, String ip, Long port) {
        this.start = start;
        this.end = end;
        this.ip = ip;
        this.port = port;
    }

    public Long getStart() {
        return start;
    }

    public Long getEnd() {
        return end;
    }

    public String getIp() {
        return ip;
    }

    public Long getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "NodeSlots{" +
                "start=" + start +
                ", end=" + end +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
