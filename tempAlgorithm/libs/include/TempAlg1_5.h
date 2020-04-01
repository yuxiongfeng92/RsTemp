/*
version:BA_1.5_3.1.3_T1 ----2019/3/5  //蓝牙Blue和adv方式串一起进行处理
*/
#ifndef TEMPALG_H
#define TEMPALG_H

#include <stdio.h>
#include <stdint.h>

struct ProResult {
    float stabTemp;//算法处理后温度结果
    int status;//测量状态：0首次升温前的取下,1贴上未夹紧,2首次升温,3稳定阶段,4二次升温,10首次升温过程后的取下
    int samp;//算法需要数据的采样率
};

/**
*APP接口
*temp：实时温度 (float类型)
*time:每个温度点对应得时间戳，单位ms
*flag:测量中的设备标志，不同设备标志是唯一的
*state:测量状态,0测量开始（算法内部进行空间分配），1测量中，2测量结束（算法内部进行空间释放）
*isPill:吃药情况，暂不做处理，可传入0
*samp:蓝牙连接的情况下为采样率率，广播连接的情况下为包序
*connectType:连接方式，0--蓝牙，1--广播
*/
ProResult
appHandle(float temp, long long time, int16_t flag, int16_t state, int16_t isPill, int16_t samp,
          int16_t connectType);

#endif // !TEMPALG_H








