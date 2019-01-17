package com.hyr.redis.message;

import java.util.Arrays;
import java.util.List;

/*******************************************************************************
 * @date 2018-03-01 下午 3:08
 * @author: <a href=mailto:>黄跃然</a>
 * @Description:
 ******************************************************************************/
public class ResultMessage {

    private boolean result;

    private List<String> infos;

    public ResultMessage(boolean result, List<String> infos) {
        this.result = result;
        this.infos = infos;
    }

    public ResultMessage(boolean result, String... infos) {
        this.result = result;
        this.infos = Arrays.asList(infos);
    }

    public ResultMessage() {
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<String> getInfos() {
        return infos;
    }

    public void setInfos(List<String> infos) {
        this.infos = infos;
    }

    public void setInfos(String... infos) {
        this.infos = Arrays.asList(infos);
    }


    public static ResultMessage build() {
        return new ResultMessage();
    }

    public static ResultMessage buildOK() {
        return new ResultMessage(true, Arrays.asList("ok!"));
    }

    public static ResultMessage build(Boolean result, List<String> infos) {
        return new ResultMessage(result, infos);
    }

    public static ResultMessage build(Boolean result, String... infos) {
        return new ResultMessage(result, infos);
    }

    @Override
    public String toString() {
        return "ResultMessage{" +
                "result=" + result +
                ", infos=" + infos +
                '}';
    }
}
