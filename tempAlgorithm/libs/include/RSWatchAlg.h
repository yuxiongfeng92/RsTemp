/*
* 润生手表算法:包含诊所算法以及家用算法
    version: RS_2.1.0_6 -- 20191012 //对外算法状态统一10改为6，不再区分公司和润生算法;新增predCheck字段作为输出
	version: RS_2.1.0_4 -- 20190717 //降温策略修正，增加状态5->3的跳转，区分润生算法需要检测重新开始；
	version: RS_2.1.0_3 -- 20190716 //降温策略修改：5min每分钟降0.05改为0.03，如果前一算法温度高于40度仍采用0.05；
	version: RS_2.1.0_2 -- 20190710 //降温策略统一阈值为3度，升温要求3min出预测温度，二次升温预测不得比前一算法温度低0.2度以上，修正stabInit重复调用bug，贴上到预测过渡段新增处理
*/
#ifndef RSWATCHALG_H
#define RSWATCHALG_H
//#include <string>
struct  TempResult
{
	float stabTemp; //算法处理后的返回温度
	int status; //算法处理后的返回的状态信息，有取下（0，10），贴上（1，5），预测（2，4），稳定（3）
	int gesture;//手臂姿势 --- 新增手势的字段0--张开到夹紧，1--夹紧，2--夹紧到张开，3--张开， 4--脱落
	bool predCheck;//是否成功完成3min预测
};
/**
* temp 贴的实时温度
* time 接收到温度的时间，润生仅为蓝牙连接，这个时间可以不用
* flag 设备标识，主要是多设备的情况下进行分配的数据识别，手表没有多设备的设计，可以默认传1
* state 测量开始--0，结束--2，测量中--1 三种状体，用于清除数据和分配数据
* isPill 吃药情况，暂时没有对吃药情况进行处理，可以默认传0
* sample 当前温度的采样率，当断开连接后，需要外部计算断开时间作为采样率
* connectStyle 连接类型，手表现只有一种蓝牙连接，可传默认值1
* algorithmType,用户类型，1--销售用户，2--租用用户，3--诊所用户
**/
TempResult getTemp(float temp, long long time, int flag, int state, int isPill, int sample, \
	int connectStyle, int algorithmType);

char* getVersion();

//string algVersion() {
//	string ver = "RS_2.0.1";
//	return ver;
//}

#endif

