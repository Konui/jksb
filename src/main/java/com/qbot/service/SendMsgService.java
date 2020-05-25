package com.qbot.service;

import com.forte.qqrobot.SimpleRobotContext;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendMsgService {

    static SimpleRobotContext simpleRobotContext;

    @Autowired
    public void setSimpleRobotContext(SimpleRobotContext simpleRobotContext){
        this.simpleRobotContext=simpleRobotContext;
    }

    CQCodeUtil cqCodeUtil=CQCodeUtil.build();



    public void sendPrivateMsg(String uq,String content){
        simpleRobotContext.SENDER.sendPrivateMsg(uq,content);
    }
    public void sendGroupMsg(String gq,String uq,String content){
        simpleRobotContext.SENDER.sendGroupMsg(gq,content+cqCodeUtil.getCQCode_At(uq).toString());
    }
}
