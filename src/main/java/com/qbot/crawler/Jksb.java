package com.qbot.crawler;

import com.qbot.entiry.AccountEntiry;
import com.qbot.entiry.TbData;
import com.qbot.service.SendMsgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class Jksb {
    @Autowired
    SendMsgService sendMsgService;

    private final String indexURL="https://jksb.v.zzu.edu.cn/vls6sss/zzujksb.dll/first0";
    private final String loginURL="https://jksb.v.zzu.edu.cn/vls6sss/zzujksb.dll/login";
    private final String tbURL="https://jksb.v.zzu.edu.cn/vls6sss/zzujksb.dll/jksb";
    public void dk(AccountEntiry accountEntiry) throws IOException{
        TbData data=new TbData();

        data.setUid(accountEntiry.getUn());
        data.setUpw(accountEntiry.getPw());

        CloseableHttpClient httpClient= HttpClients.createDefault();
        try {
            index(httpClient,data);
            login(httpClient,data);
            forward(httpClient,data);
            iframe(httpClient,data);
            enterTb(httpClient,data);
            lastTb(httpClient,data);
        }catch (Exception e){
            data.setMsg(data.getMsg()+e.getMessage());
        }
        finally {
            HttpClientUtils.closeQuietly(httpClient);
        }
        if(accountEntiry.isGroup()){
            sendMsgService.sendGroupMsg(accountEntiry.getQqGroup(),accountEntiry.getQqNum(),data.getMsg());
            log.info(accountEntiry.getQqNum()+"-已私回复");
        }else {
            sendMsgService.sendPrivateMsg(accountEntiry.getQqNum(),data.getMsg());
            log.info(accountEntiry.getQqNum()+"-已群回复");
        }
        log.info(data.getMsg());
    }
    //进入首页，获取hh28值
    private void index(CloseableHttpClient httpClient,TbData data) throws IOException{
        HttpGet login=new HttpGet(indexURL);
        CloseableHttpResponse resp=httpClient.execute(login);
        try{
            String html =EntityUtils.toString(resp.getEntity(),"UTF-8");
            Document document= Jsoup.parse(html);
            data.setHh28(document.select("input[type=hidden]").val());
        }finally {
            HttpClientUtils.closeQuietly(resp);
            login.releaseConnection();
        }
    }
    //登录
    private void login(CloseableHttpClient httpClient,TbData tbData) throws IOException{
        HttpPost login = new HttpPost(loginURL);

        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("uid", tbData.getUid()));
        data.add(new BasicNameValuePair("upw", tbData.getUpw()));
        data.add(new BasicNameValuePair("smbtn", tbData.getSmbtn()));
        data.add(new BasicNameValuePair("hh28", tbData.getHh28()));

        login.setEntity(new UrlEncodedFormEntity(data, StandardCharsets.UTF_8));
        CloseableHttpResponse resp = httpClient.execute(login);

        try {
            String html=EntityUtils.toString(resp.getEntity(),"UTF-8");
            String pattern = "location=\".*?\"";

            Pattern r=Pattern.compile(pattern);
            Matcher m=r.matcher(html);
            if(m.find()) {
                tbData.setTbURL(m.group(0).split("\"")[1]);
            }else{
                tbData.setOver(true);
                if(html.contains("对不起，登录失败，未检索到用户账号")){
                    tbData.setMsg("对不起，登录失败，未检索到用户账号\n请更新信息，或自行填报\n"+getIndexURL());
                }else if(html.contains("对不起，你的密码输入错误，登录失败。")){
                    tbData.setMsg("对不起，你的密码输入错误，登录失败。\n请更新信息，或自行填报\n"+getIndexURL());
                }else {
                    tbData.setMsg(html + "\n\n出错请自行填报\n" + getIndexURL());
                }
            }
        }finally{
            HttpClientUtils.closeQuietly(resp);
            login.releaseConnection();
        }

    }
    private void forward(CloseableHttpClient httpClient,TbData data) throws IOException{
        if(data.isOver())return;
        HttpGet forward1=new HttpGet(data.getTbURL());

        CloseableHttpResponse resp = httpClient.execute(forward1);
        try{
            String html =EntityUtils.toString(resp.getEntity(),"UTF-8");
            Document document= Jsoup.parse(html);
            data.setIfURL(document.select("#zzj_top_6s").attr("src"));
        }finally {
            HttpClientUtils.closeQuietly(resp);
            forward1.releaseConnection();
        }
    }
    private void iframe(CloseableHttpClient httpClient,TbData data) throws IOException{
        if(data.isOver())return;
        HttpGet forward1=new HttpGet(data.getIfURL());

        CloseableHttpResponse resp = httpClient.execute(forward1);
        try{
            String html =EntityUtils.toString(resp.getEntity(),"UTF-8");
            String pattern = "\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d";

            Pattern r=Pattern.compile(pattern);
            Matcher m=r.matcher(html);
            if(m.find()){
                String[] lastTime=m.group().split(" ");
                Date time = Calendar.getInstance(Locale.CHINA).getTime();
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String[] nowTime=sdf.format(time).split(" ");
                if(nowTime[0].equals(lastTime[0])){
                    data.setOver(true);
                    data.setMsg("今日您已经填报过了\n填报时间："+m.group());
                }else{
                    Document document = Jsoup.parse(html);
                    data.setDay6(document.select("input[name=day6]").val());
                    data.setDid(document.select("input[name=did]").val());
                    data.setDoor(document.select("input[name=door]").val());
                    data.setMen6(document.select("input[name=men6]").val());
                    data.setPtopid(document.select("input[name=ptopid]").val());
                    data.setSid(document.select("input[name=sid]").val());
                }
            }else{
                data.setOver(true);
                data.setMsg("未能确定今天是否填报过"+"\n请自行填报\n" + getIndexURL());
            }
        }finally {
            HttpClientUtils.closeQuietly(resp);
            forward1.releaseConnection();
        }
    }
    private void enterTb(CloseableHttpClient httpClient,TbData tbData) throws IOException{
        if(tbData.isOver())return;
        HttpPost tb=new HttpPost(tbURL);

        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("day6", tbData.getDay6()));
        data.add(new BasicNameValuePair("did", tbData.getDid()));
        data.add(new BasicNameValuePair("door", tbData.getDoor()));
        data.add(new BasicNameValuePair("men6", tbData.getMen6()));
        data.add(new BasicNameValuePair("ptopid", tbData.getPtopid()));
        data.add(new BasicNameValuePair("sid", tbData.getSid()));

        tb.setEntity(new UrlEncodedFormEntity(data,StandardCharsets.UTF_8));
        CloseableHttpResponse resp=httpClient.execute(tb);
        try{
            String html=EntityUtils.toString(resp.getEntity(),"UTF-8");
            Document document=Jsoup.parse(html);
            tbData.setMyvs1(getChecksVal(document.select("input[name=myvs_1]")));
            tbData.setMyvs2(getChecksVal(document.select("input[name=myvs_2]")));
            tbData.setMyvs3(getChecksVal(document.select("input[name=myvs_3]")));
            tbData.setMyvs4(getChecksVal(document.select("input[name=myvs_4]")));
            tbData.setMyvs5(getChecksVal(document.select("input[name=myvs_5]")));
            tbData.setMyvs6(getChecksVal(document.select("input[name=myvs_6]")));
            tbData.setMyvs7(getChecksVal(document.select("input[name=myvs_7]")));
            tbData.setMyvs8(getChecksVal(document.select("input[name=myvs_8]")));
            tbData.setMyvs9(getChecksVal(document.select("input[name=myvs_9]")));
            tbData.setMyvs10(getChecksVal(document.select("input[name=myvs_10]")));
            tbData.setMyvs11(getChecksVal(document.select("input[name=myvs_11]")));
            tbData.setMyvs12(getChecksVal(document.select("input[name=myvs_12]")));

            tbData.setMyvs13a(getSelectsVal(document.select("select[name=myvs_13a]").first()));
            tbData.setMyvs13b(getSelectsVal(document.select("select[name=myvs_13b]").first()));
            tbData.setMyvs13c(document.select("input[name=myvs_13c]").val());

            tbData.setMyvs14(getChecksVal(document.select("input[name=myvs_14]")));
            tbData.setMyvs14b(document.select("input[name=myvs_14b]").val());
            tbData.setMyvs15(getChecksVal(document.select("input[name=myvs_15]")));
            tbData.setMyvs16(getSelectsVal(document.select("select[name=myvs_16]").first()));
            tbData.setMyvs16b(document.select("input[name=myvs_16b]").val());
            tbData.setMyvs17(getSelectsVal(document.select("select[name=myvs_17]").first()));
            tbData.setMyvs18(getSelectsVal(document.select("select[name=myvs_18]").first()));

            tbData.setDid(document.select("input[name=did]").val());
            tbData.setDoor(document.select("input[name=door]").val());
            tbData.setDay6(document.select("input[name=day6]").val());
            tbData.setMen6(document.select("input[name=men6]").val());
            tbData.setSheng6(document.select("input[name=sheng6]").val());
            tbData.setShi6(document.select("input[name=shi6]").val());
            tbData.setFun3(document.select("input[name=fun3]").val());
            tbData.setPtopid(document.select("input[name=ptopid]").val());
            tbData.setSid(document.select("input[name=sid]").val());
            log.info(tbData.toString());
        }finally {
            HttpClientUtils.closeQuietly(resp);
            tb.releaseConnection();
        }
    }
    private void lastTb(CloseableHttpClient httpClient,TbData tbData) throws IOException{
        if(tbData.isOver())return;
        HttpPost dk=new HttpPost(tbURL);
        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("myvs_1",tbData.getMyvs1()));
        data.add(new BasicNameValuePair("myvs_2",tbData.getMyvs2()));
        data.add(new BasicNameValuePair("myvs_3",tbData.getMyvs3()));
        data.add(new BasicNameValuePair("myvs_4",tbData.getMyvs4()));
        data.add(new BasicNameValuePair("myvs_5",tbData.getMyvs5()));
        data.add(new BasicNameValuePair("myvs_6",tbData.getMyvs6()));
        data.add(new BasicNameValuePair("myvs_7",tbData.getMyvs7()));
        data.add(new BasicNameValuePair("myvs_8",tbData.getMyvs8()));
        data.add(new BasicNameValuePair("myvs_9",tbData.getMyvs9()));
        data.add(new BasicNameValuePair("myvs_10",tbData.getMyvs10()));
        data.add(new BasicNameValuePair("myvs_11",tbData.getMyvs11()));
        data.add(new BasicNameValuePair("myvs_12",tbData.getMyvs12()));
        data.add(new BasicNameValuePair("myvs_13a",tbData.getMyvs13a()));
        data.add(new BasicNameValuePair("myvs_13b",tbData.getMyvs13b()));
        data.add(new BasicNameValuePair("myvs_13c",tbData.getMyvs13c()));
        data.add(new BasicNameValuePair("myvs_14",tbData.getMyvs14()));
        data.add(new BasicNameValuePair("myvs_14b",tbData.getMyvs14b()));
        data.add(new BasicNameValuePair("myvs_15",tbData.getMyvs15()));
        data.add(new BasicNameValuePair("myvs_16",tbData.getMyvs16()));
        data.add(new BasicNameValuePair("myvs_16b",tbData.getMyvs16b()));
        data.add(new BasicNameValuePair("myvs_17",tbData.getMyvs17()));
        data.add(new BasicNameValuePair("myvs_18",tbData.getMyvs18()));
        data.add(new BasicNameValuePair("did",tbData.getDid()));
        data.add(new BasicNameValuePair("door",tbData.getDoor()));
        data.add(new BasicNameValuePair("day6",tbData.getDay6()));
        data.add(new BasicNameValuePair("men6",tbData.getMen6()));
        data.add(new BasicNameValuePair("sheng6",tbData.getSheng6()));
        data.add(new BasicNameValuePair("shi6",tbData.getShi6()));
        data.add(new BasicNameValuePair("fun3",tbData.getFun3()));
        data.add(new BasicNameValuePair("ptopid",tbData.getPtopid()));
        data.add(new BasicNameValuePair("sid",tbData.getSid()));


        dk.setEntity(new UrlEncodedFormEntity(data,StandardCharsets.UTF_8));
        CloseableHttpResponse resp=httpClient.execute(dk);
        try{
            String html=EntityUtils.toString(resp.getEntity(),"UTF-8");
            if(html.contains("感谢您今日上报健康状况！")){
                tbData.setMsg("感谢您今日上报健康状况！");
            }else{
                tbData.setMsg(html);
            }
        }finally {
            HttpClientUtils.closeQuietly(resp);
            dk.releaseConnection();
        }
    }

    private String getIndexURL(){
        return this.indexURL;
    }
    private String getChecksVal(Elements els){
        for(Element e:els){
            if(e.hasAttr("checked")){
                return e.val();
            }
        }
        return "";
    }
    private String getSelectsVal(Element els){
        for (Element e:els.children()){
            if(e.hasAttr("selected")){
                return e.val();
            }
        }
        return "";
    }
}
