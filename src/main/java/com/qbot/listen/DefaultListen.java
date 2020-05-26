package com.qbot.listen;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.ListenBody;
import com.forte.qqrobot.anno.Spare;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import com.qbot.command.Commands;
import org.springframework.stereotype.Component;

@Component
public class DefaultListen {


    CQCodeUtil cqCodeUtil=CQCodeUtil.build();

    @Listen(MsgGetTypes.privateMsg)
    @Spare
    public void defaultPMsg(PrivateMsg msg, MsgSender sender){
        sender.SENDER.sendPrivateMsg(msg,"暂时无法处理该命令");
    }

    @Listen(MsgGetTypes.groupMsg)
    @Spare
    public void defaultGMsg(GroupMsg msg, MsgSender sender){
        if(!cqCodeUtil.isAt(msg))return;
        sender.SENDER.sendGroupMsg(msg,"暂时无法处理该命令"+cqCodeUtil.getCQCode_At(msg.getQQ()).toString());
    }
    @Listen(MsgGetTypes.privateMsg)
    @Filter("命令")
    @ListenBody
    public void getCommandsP(PrivateMsg msg, MsgSender sender){
        sender.SENDER.sendPrivateMsg(msg,getCommands());
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(".*命令.*")
    @ListenBody
    public void getCommandsG(GroupMsg msg, MsgSender sender){
        if(!cqCodeUtil.isAt(msg))return;
        if(!"命令".equals(msg.getMsg().replaceAll("\\[CQ:at,.*\\]", "").replaceAll(" ","")))return;
        sender.SENDER.sendGroupMsg(msg,getCommands()+cqCodeUtil.getCQCode_At(msg.getQQ()).toString());
    }

    public String getCommands(){
        StringBuilder res=new StringBuilder("目前所有命令如下：\n");
        for(Commands cmd:Commands.values()){
            res.append(cmd.getKeyword());
            res.append("\n");
        }
        return res.toString();
    }

}
