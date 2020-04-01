#include <jni.h>
#include <sys/time.h>
#include <android/log.h>
#include <RSWatchAlg.h>

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,"JNIHelper",__VA_ARGS__)

static bool IS_DEBUG;

extern "C" {

long getCurrentTime() {
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

JNIEXPORT void JNICALL
Java_com_proton_temp_algorithm_utils_AlgorithmHelper_init(JNIEnv *env, jclass type,
                                                          jboolean isDebug) {
    IS_DEBUG = isDebug;
}

JNIEXPORT jstring JNICALL
Java_com_proton_temp_algorithm_utils_AlgorithmHelper_getVersion(JNIEnv *env, jclass type) {
    return env->NewStringUTF(getVersion());
}

JNIEXPORT jobject JNICALL
Java_com_proton_temp_algorithm_utils_AlgorithmHelper_getTemp(JNIEnv *env,
                                                             jclass type,
                                                             jfloat temp,
                                                             jlong time,
                                                             jint flag,
                                                             jint state,
                                                             jint isPill,
                                                             jint sample,
                                                             jint connectType,
                                                             jint algorithmType) {

    long startTime = getCurrentTime();
    if (IS_DEBUG) {
        LOGW("算法开始1.5:temp = %lf,state = %d,connectStyle = %d,algorithmType=%d,time=%ld", temp,
             state,
             connectType,
             algorithmType, time);
    }
    if (sample <= 0) {
        sample = 1;
    }
    TempResult result = getTemp(temp, time, flag, state, isPill, sample, connectType,
                                algorithmType);
    jclass tempResultClass = env->FindClass("com/proton/temp/algorithm/bean/TempResult");
    jmethodID tempResultClint = env->GetMethodID(tempResultClass, "<init>", "()V");
    jobject tempResultObj = env->NewObject(tempResultClass, tempResultClint);

    if (IS_DEBUG) {
        LOGW("算法结束1.5:temp = %lf,status = %d,耗时:%ld", result.stabTemp, result.status,
             (getCurrentTime() - startTime));
    }
    env->SetFloatField(tempResultObj, env->GetFieldID(tempResultClass, "currentTemp", "F"),
                       result.stabTemp);
    env->SetIntField(tempResultObj, env->GetFieldID(tempResultClass, "status", "I"),
                     result.status);
    env->SetIntField(tempResultObj, env->GetFieldID(tempResultClass, "gesture", "I"),
                     result.gesture);
    return tempResultObj;
}
}