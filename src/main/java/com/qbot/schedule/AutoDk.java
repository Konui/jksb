package com.qbot.schedule;

import com.forte.qqrobot.anno.timetask.CronTask;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.timetask.TimeJob;
import com.forte.qqrobot.utils.CQCodeUtil;
import com.qbot.service.DkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@CronTask("0 15 0 * * ? *")
public class AutoDk implements TimeJob {
    @Autowired
    DkService dkService;

    @Override
    public void execute(MsgSender msgSender, CQCodeUtil cqCodeUtil) {
        dkService.DkAll();
    }
}
