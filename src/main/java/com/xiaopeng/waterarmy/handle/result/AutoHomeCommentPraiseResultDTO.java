package com.xiaopeng.waterarmy.handle.result;

/**
 *
 * 汽车之家评论点赞返回dto
 *
 */
public class AutoHomeCommentPraiseResultDTO {

    private boolean Status;
    private String Message;
    private Integer StateId;

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Integer getStateId() {
        return StateId;
    }

    public void setStateId(Integer stateId) {
        StateId = stateId;
    }

}
