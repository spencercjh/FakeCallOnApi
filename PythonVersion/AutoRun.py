from hashlib import md5
import json
import requests


class autoRun:
    __salt = 'lpKK*TJE8WaIg%93O0pfn0#xS0i3xE$z'
    __url_1 = 'http://www.sportcampus.cn/api/reg/login'
    __url_2 = 'http://www.sportcampus.cn/api/run/runPage'
    __url_3 = 'http://www.sportcampus.cn/api/run/saveRunV2'

    @classmethod
    def __jiami(cls, data):
        sign_raw = cls.__salt + 'data' + data
        h = md5()
        h.update(sign_raw.encode('ascii'))
        return h.hexdigest()

    def __init__(self):
        self.tNode_info = []
        self.bNode_info = []
        self.userid = 0
        self.runPageId = 0
        with open('./data.txt') as f:
            list = f.readlines()
            for i in range(len(list)):
                list[i] = list[i].strip('\n')
            self.buPin = list[0]
            self.duration = list[1]
            self.starTime = list[2]
            self.endTime = list[3]
            self.real = list[4]
            self.totalNum = list[5]
            self.pubilcHeader = eval(list[6])
            self.loginData = list[7]
            self.requestForRunData = list[8]
            self.runningData = eval(list[9])

    def __Login(self):
        '''请求登录'''
        data = self.loginData
        sign = self.__jiami(data)

        res = requests.get(url=self.__url_1, params={'sign': sign, 'data': data}, headers=self.pubilcHeader)

        if(res.ok):
            res_body = eval(res.text)
            print(res_body)
            self.pubilcHeader['utoken'] = res_body['data']['utoken']  # 更新 utoken
            self.userid = res_body['data']['userid']
        else:
            print('登录失败！')
            print(res_body)

        return res.ok

    def __RequestForRun(self):
        '''请求跑步'''
        # 处理 sign
        data = self.requestForRunData
        sign = self.__jiami(data)

        res = requests.get(url=self.__url_2, params={'sign': sign, 'data': data}, headers=self.pubilcHeader)

        if(res.ok):
            res_body = eval(res.text)
            print(res_body)
            # 获取 4 个选经点的经纬坐标，列表形式
            self.tNode_info = res_body['data']['gpsinfo'][:]
            # 获取 2 个必经点的经纬坐标，列表形式
            self.bNode_info = res_body['data']['ibeacon'][:]
            # 获取 runPageId，整数形式
            self.runPageId = res_body['data']['runPageId']
        else:
            print('请求跑步失败！')
            print(res_body)

        return res.ok

    def __Run(self):
        '''上传跑步信息'''
        data = self.runningData

        for i in range(2):
            self.bNode_info[i]['position']["latitude"] = eval(self.bNode_info[i]['position']["latitude"])
            self.bNode_info[i]['position']["longitude"] = eval(self.bNode_info[i]['position']["longitude"])
            self.bNode_info[i]['position']['speed'] = 0.0
        data['bNode'] = [self.bNode_info[0]]

        for i in range(4):
            self.tNode_info[i]["latitude"] = eval(self.tNode_info[i]["latitude"])
            self.tNode_info[i]["longitude"] = eval(self.tNode_info[i]["longitude"])
            self.tNode_info[i]['speed'] = 0.0
        data['tNode'] = [self.tNode_info[0], self.tNode_info[2]]

        # 算配速，制造速度数据
        duration = eval(data['duration'])
        real = eval(data['real'])/1000
        data['speed'] = self.__computeSpeed(duration, real)
        data['buPin'] = self.buPin
        data['duration'] = self.duration
        data['startTime'] = self.starTime
        data['endTime'] = self.endTime
        data['real'] = self.real
        data['totalNum'] = self.totalNum
        data['type'] = '1'
        data['userid'] = str(self.userid)
        data['runPageId'] = str(self.runPageId)

        data_json = json.dumps(data)
        sign = self.__jiami(data_json)

        self.pubilcHeader['Content-Type'] = 'application/x-www-form-urlencoded'
        # public_header['Content-Length'] = str(len(data_json))

        # 制作请求报文
        res_3 = requests.post(url=self.__url_3, data={'sign': sign, 'data': data_json}, headers=self.pubilcHeader)
        # 发出请求报文，获得响应报文
        res3_body = eval(res_3.text)
        print(res3_body)

    def __computeSpeed(self, duration, real):
        peisu = duration / real
        peisu_min = peisu // 60
        peisu_sec = peisu % 60
        peisu_min = int(peisu_min)
        peisu_sec = int(peisu_sec)
        if peisu_min < 10:
            peisu_min_str = '0'+str(peisu_min)
        else:
            peisu_min_str = str(peisu_min)

        if peisu_sec < 10:
            peisu_sec_str = '0'+str(peisu_sec)
        else:
            peisu_sec_str = str(peisu_sec)

        speed = peisu_min_str + "'" + peisu_sec_str + "''"

        return speed

    def Exercise(self):
        if self.__Login():
            if self.__RequestForRun():
                self.__Run()


firstTry = autoRun()
firstTry.Exercise()
