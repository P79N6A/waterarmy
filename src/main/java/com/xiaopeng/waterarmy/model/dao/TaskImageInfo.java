package com.xiaopeng.waterarmy.model.dao;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 帖子任务对应文件
 *
 * Created by iason on 2018/10/28.
 */
@Table(name = "task_image_info")
public class TaskImageInfo implements Serializable {

    private static final long serialVersionUID = -4217840253534461213L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 任务id
     */
    @Column(name = "task_id")
    private String taskId;

    /**
     * 文件名
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * 文件路径
     */
    @Column(name = "file_path")
    private String filePath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}

