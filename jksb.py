#coding=utf-8
import requests,re

'''

带****************的注释一定要填
剩余的自己看是否需要修改
需要修改的自己先登录查看value

'''


loginJSON={
    "uid":"",    #****************学号
    "upw":"",    #****************密码
}
submitJSON={
    "day6":"b",             #每日填报
    "did":"1",
    "men6":"a"
}
finalJSON={
    "myvsp_5": "正常",  # 身体状况
    "myvs_1": "是",  # 体温
    "myvs_2": "否",  # 咳嗽
    "myvs_3": "否",  # 乏力
    "myvs_4": "否",  # 其他症状
    "myvsw_1": "否",  # 是否在郑州
    "myvsp_1": "41",  # 现居住地（省）
    "myvsp_3": "",  #****************市(代码)
    "myvsw_2": "",  #****************详细地址
    "myvsw_a1": "否",  # 小区是否有
    "myvsw_a2": "否",  # 同居
    "myvsp_6": "否",  # 回郑州
    "myvsw_3": "否",  # 14天内外出
    "myvsw_5": "在家学习",  # 在哪学
    "did": "2",
    "day6": "b",
    "men6": "a",
    "sheng6": "41",    #省
    "shi6": "",    #****************市(代码)
    "fun3": ""
}
URL="https://jksb.v.zzu.edu.cn/vls6sss/zzujksb.dll/"

pattern = re.compile(r'(ptopid).*(sid=)\d+')

session = requests.session()

#登录并获取ptopid,sid
response1=session.post(URL+"login",data=loginJSON)
key=pattern.search(response1.content.decode("utf-8")).group()

#进入填报页面
response2=session.get(URL+"jksb?"+str(key))

#分割出ptopid,sid
s=key.split("&")
s1=s[0].split("=")
s2=s[1].split("=")
submitJSON[s1[0]]=s1[1]
submitJSON[s2[0]]=s2[1]
finalJSON[s1[0]]=s1[1]
finalJSON[s2[0]]=s2[1]
#填报页面
response3=session.post(URL+"jksb",data=submitJSON)
response4=session.post(URL+"jksb",data=finalJSON)

response4.encoding='utf-8'
print(response4.text)
if response4.text.find("感谢")>-1:
    print("打卡成功")
else:print("打卡失败")





