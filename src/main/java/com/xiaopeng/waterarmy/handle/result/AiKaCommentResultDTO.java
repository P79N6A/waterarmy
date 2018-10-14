package com.xiaopeng.waterarmy.handle.result;

import com.xiaopeng.waterarmy.handle.param.AiKaComment;

import java.util.List;

public class AiKaCommentResultDTO {
    private String msg;

    private List<AiKaComment> aiKaComments;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<AiKaComment> getAiKaComments() {
        return aiKaComments;
    }

    public void setAiKaComments(List<AiKaComment> aiKaComments) {
        this.aiKaComments = aiKaComments;
    }
}


