package com.hyr.redis.message;

/*******************************************************************************
 * @date 2018-03-01 下午 3:08
 * @author: <a href=mailto:huangyr@bonree.com>黄跃然</a>
 * @Description:
 ******************************************************************************/
public class ResultMessage {

    private boolean result;

    private String info;

    public ResultMessage(boolean result, String info) {
        this.result = result;
        this.info = info;
    }

    public ResultMessage() {
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "ResultMessage{" +
                "result=" + result +
                ", info='" + info + '\'' +
                '}';
    }

    public static ResultMessage build() {
        return new ResultMessage();
    }

    public static ResultMessage buildOK() {
        return new ResultMessage(true, "ok!");
    }

    public static ResultMessage build(Boolean result, String info) {
        return new ResultMessage(result, info);
    }
}
