package com.qbot.command;

public enum Commands {
    //更新打卡信息
        UPDATE_DK("更新信息"),
    //查询打卡信息
        SELECT_DK("查询信息"),
    //打卡
        DK("打卡"),
    //开启/关闭自动打卡
        AUTO_DK("开启/关闭自动打卡"),
    //清空信息
        REMOVE_INFO("清空信息");
    private String keyword;
    Commands(String keyword){
        this.keyword=keyword;
    }
    public String getKeyword(){
        return this.keyword;
    }
}
