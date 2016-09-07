package com.benx.droptodo;

//import cn.bmob.v3.BmobObject;

/**
 * Project: DropTODO
 * Author:  Ben.X
 * Date:    2016/8/6
 */

public class Feedback {

    /** 属性 */
    private String email;
    private String content;
    private String reply;


    /**
     * 构造方法
     */
    public Feedback() {}

    public Feedback(String _email, String _content) {
        this.email = _email;
        this.content = _content;
        this.reply = "";
    }


    /** Getter 和 Setter */

    public String getEmail() {
        return email;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
