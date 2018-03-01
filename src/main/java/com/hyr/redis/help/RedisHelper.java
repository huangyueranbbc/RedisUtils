package com.hyr.redis.help;

/*******************************************************************************
 * @date 2018-03-01 下午 2:11
 * @author: <a href=mailto:huangyr@bonree.com>黄跃然</a>
 * @Description:
 ******************************************************************************/
public class RedisHelper {

    /**
     * get node id string
     *
     * @param nodeInfos
     * @return
     */
    public static String getNodeId(String nodeInfos) {
        for (String infoLine : nodeInfos.split("\n")) {
            if (infoLine.contains("myself")) {
                return infoLine.split(" ")[0];
            }
        }
        return "";
    }

}
