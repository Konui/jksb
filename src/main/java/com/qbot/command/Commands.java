package com.qbot.command;

public enum Commands {
    //更新打卡信息
        UPDATE_DK("更新打卡信息"),
    //查询打卡信息
        SELECT_DK("查询打卡信息"),
    //打卡
        DK("打卡"),
    //开启自动打卡
        ON_AUTO_DK("开启自动打卡"),
    //关闭自动打卡
        OFF_AUTO_DK("关闭自动打卡");
    private String keyword;
    Commands(String keyword){
        this.keyword=keyword;
    }
    public String getKeyword(){
        return this.keyword;
    }
}
