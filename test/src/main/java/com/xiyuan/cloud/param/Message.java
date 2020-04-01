package com.xiyuan.cloud.param;

import com.sun.istack.internal.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Created by xiyuan_fengyu on 2020/4/1 17:52.
 */
@ApiModel(value = "实体类参数", description = "演示实体类参数")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(name = "id", value = "消息编号", example="123456")
    private long id;

    @NotNull
    @ApiModelProperty(name = "content", value = "消息内容", example="你好啊")
    private String content;

    public long getId() {
        return id;
    }

    public Message setId(long id) {
        this.id = id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content=" + content +
                '}';
    }

}
