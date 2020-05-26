package com.qbot.service;

import com.qbot.crawler.Jksb;
import com.qbot.entiry.AccountEntiry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.util.HashMap;
@Slf4j
@Service
public class DkService {
    private HashMap<String, AccountEntiry> dkList=new HashMap<>();
    @Autowired
    Jksb jksb;


    //获取信息
    public AccountEntiry getAccountEntiry(String qnum){
        return dkList.get(qnum);
    }
    //添加信息
    public void addAccountEntiry(String qnum, String un,String pw){
        dkList.put(qnum,new AccountEntiry(qnum,un,pw));
    }
    public void addAccountEntiry(String qqGroup,String qnum,String un,String pw){
        dkList.put(qnum,new AccountEntiry(qqGroup,qnum,un,pw));
    }
    //设置自动打卡
    public void setAutoDk(String qnum,boolean flg){
        dkList.get(qnum).setAuto(flg);
    }
    public void removeUser(AccountEntiry accountEntiry){
        dkList.remove(accountEntiry.getQqNum());
    }
    public void DkAll(){
        for(AccountEntiry ac:dkList.values()){
            if (ac.isAuto()){
                Dk(ac);
            }
        }
    }
    @Async
    public void Dk(AccountEntiry accountEntiry){
        try {
            jksb.dk(accountEntiry);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
