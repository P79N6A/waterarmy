package com.xiaopeng.waterarmy.model.dao;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户评论点赞记录表
 *
 * Created by iason on 2018/10/28
 */
@Table(name = "comment_like_log")
public class CommentLikeLog implements Serializable {

    private static final long serialVersionUID = -2544740253534460127L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 账号名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 平台，详见PlatformEnum
     */
    @Column(name = "platform")
    private String platform;

    /**
     * 评论点赞任务链接
     */
    @Column(name = "comment_like_link")
    private String commentLikeLink;

    /**
     * 需要点赞内容
     */
    @Column(name = "like_content")
    private String likeContent;

    public CommentLikeLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCommentLikeLink() {
        return commentLikeLink;
    }

    public void setCommentLikeLink(String commentLikeLink) {
        this.commentLikeLink = commentLikeLink;
    }

    public String getLikeContent() {
        return likeContent;
    }

    public void setLikeContent(String likeContent) {
        this.likeContent = likeContent;
    }
}

