package com.xiaopeng.waterarmy.common.Result;

import java.util.List;

public class AiKaCommentList {
    private AiKaCommentConfig config;
    private AiKaCommentNews news;
    private List<AiKaComment> list;

    public AiKaCommentConfig getConfig() {
        return config;
    }

    public void setConfig(AiKaCommentConfig config) {
        this.config = config;
    }

    public AiKaCommentNews getNews() {
        return news;
    }

    public void setNews(AiKaCommentNews news) {
        this.news = news;
    }

    public List<AiKaComment> getList() {
        return list;
    }

    public void setList(List<AiKaComment> list) {
        this.list = list;
    }
}
