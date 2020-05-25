package com.qbot.entiry;

import lombok.Data;

@Data
public class AccountEntiry {
    private String qqNum;
    private String un;
    private String pw;
    private String qqGroup;
    private boolean isGroup;
    private boolean isAuto;

    public AccountEntiry(){}
    public AccountEntiry(String qqNum,String un,String pw){
        this.qqNum=qqNum;
        this.un=un;
        this.pw=pw;
    }
    public AccountEntiry(String qqGroup,String qqNum,String un,String pw){
        this.qqGroup=qqGroup;
        this.qqNum=qqNum;
        this.un=un;
        this.pw=pw;
        this.isGroup=true;
    }
}
