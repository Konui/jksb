package com.qbot.listen;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.ListenBody;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import com.qbot.entiry.AccountEntiry;
import com.qbot.service.DkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class JksbListen {

    @Autowired
    DkService dkService;

    CQCodeUtil cqCodeUtil=CQCodeUtil.build();


    @Listen(MsgGetTypes.privateMsg)
    @Filter("更新打卡信息.*")
    @ListenBody
    public void UpdateDkP(PrivateMsg msg, MsgSender sender){
        String[] temp=msg.getMsg().split(" ");
        if(temp.length==3) {
            dkService.addAccountEntiry(msg.getQQ(), temp[1], temp[2]);
            AccountEntiry u=dkService.getAccountEntiry(msg.getQQ());
            if (u != null) {
                sender.SENDER.sendPrivateMsg(msg, "更新信息成功\n学号："+u.getUn()+"\n密码："+u.getPw());
            } else {
                sender.SENDER.sendPrivateMsg(msg, "更新信息失败。。。gg");
            }
        }else{
            sender.SENDER.sendPrivateMsg(msg,"请输入正确的命令格式：\n更新打卡信息+(一个空格)+学号+(一个空格)+密码");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(".*更新打卡信息.*")
    @ListenBody
    public void UpdateDkG(GroupMsg msg, MsgSender sender){
        //是否@自己
        if(!cqCodeUtil.isAt(msg))return;
        String message=msg.getMsg().replaceAll("\\[CQ:at,.*\\]", "");
        String[] temp=message.split(" ");
        if(temp.length==3) {
            dkService.addAccountEntiry(msg.getGroup(),msg.getQQ(), temp[1], temp[2]);
            AccountEntiry u=dkService.getAccountEntiry(msg.getQQ());
            if (u != null) {
                sender.SENDER.sendGroupMsg(msg, "更新信息成功\n学号："+u.getUn()+"\n密码："+u.getPw()+"\n"+cqCodeUtil.getCQCode_At(msg.getQQ()));
            } else {
                sender.SENDER.sendGroupMsg(msg, "更新信息失败。。。gg"+"\n"+cqCodeUtil.getCQCode_At(msg.getQQ()));
            }
        }else{
            sender.SENDER.sendGroupMsg(msg,"请输入正确的命令格式(并@我)：\n更新打卡信息+(一个空格)+学号+(一个空格)+密码"+"\n"+cqCodeUtil.getCQCode_At(msg.getQQ()));
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter("查询打卡信息")
    @ListenBody
    public void selectDkP(PrivateMsg msg,MsgSender sender){
        AccountEntiry u=dkService.getAccountEntiry(msg.getQQ());
        if (u !=null){
            sender.SENDER.sendPrivateMsg(msg, "学号："+u.getUn()+"\n密码："+u.getPw());
        }else{
            sender.SENDER.sendPrivateMsg(msg,"未查询到信息，请先更新信息");
        }
    }
    @Listen(MsgGetTypes.groupMsg)
    @Filter(".*查询打卡信息.*")
    @ListenBody
    public void selectDkG(GroupMsg msg,MsgSender sender){
        if(!cqCodeUtil.isAt(msg))return;
        AccountEntiry u=dkService.getAccountEntiry(msg.getQQ());
        if (u !=null){
            sender.SENDER.sendGroupMsg(msg, "学号："+u.getUn()+"\n密码："+u.getPw()+"\n"+cqCodeUtil.getCQCode_At(msg.getQQ()));
        }else{
            sender.SENDER.sendGroupMsg(msg,"未查询到信息，请先更新信息\n"+cqCodeUtil.getCQCode_At(msg.getQQ()));
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter("打卡")
    @ListenBody
    public void DkP(PrivateMsg msg, MsgSender sender){
        AccountEntiry u=dkService.getAccountEntiry(msg.getQQ());
        if(u!=null){
            dkService.Dk(u);
        }else{
            sender.SENDER.sendPrivateMsg(msg,"未查询到信息，请先更新信息");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(".*打卡.*")
    @ListenBody
    public void DkG(GroupMsg msg,MsgSender sender){
        if(!cqCodeUtil.isAt(msg))return;
        if(msg.getMsg().replaceAll("\\[CQ:at,.*\\]", "")!="打卡")return;
        AccountEntiry u=dkService.getAccountEntiry(msg.getQQ());
        if(u!=null){
            dkService.Dk(u);
        }else{
            sender.SENDER.sendGroupMsg(msg,"未查询到信息，请先更新信息\n"+cqCodeUtil.getCQCode_At(msg.getQQ()));
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter("test")
    @ListenBody
    public void test(PrivateMsg msg,MsgSender sender){
        for(int i=0;i<5;i++){

        }
    }
}
