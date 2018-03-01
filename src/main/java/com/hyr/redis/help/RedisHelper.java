package com.hyr.redis.help;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 *
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
