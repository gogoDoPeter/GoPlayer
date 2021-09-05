#include <jni.h>
#include <string>
#include "WlFFmpeg.h"
#include "WlPlaystatus.h"

extern "C"
{
#include <libavformat/avformat.h>
}


_JavaVM *javaVM = NULL;
WlCallJava *callJava = NULL;
WlFFmpeg *fFmpeg = NULL;
WlPlaystatus *playstatus = NULL;

bool nexit = true;
pthread_t thread_start;


extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
    jint result = -1;
    javaVM = vm;
    JNIEnv *env;
    if(vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
    {

        return result;
    }
    return JNI_VERSION_1_4;

}

extern "C"
JNIEXPORT void JNICALL
Java_com_peter_myplayer_player_WePlayer_nativePrepared(JNIEnv *env, jobject instance, jstring source_) {
    const char *source = env->GetStringUTFChars(source_, 0);

    if(fFmpeg == NULL)
    {
        if(callJava == NULL)
        {
            callJava = new WlCallJava(javaVM, env, &instance);
        }
        callJava->onCallLoad(MAIN_THREAD, true);
        playstatus = new WlPlaystatus();
        fFmpeg = new WlFFmpeg(playstatus, callJava, source);
        fFmpeg->parpared();
    }
}

void *startCallBack(void *data)
{
    WlFFmpeg *fFmpeg = (WlFFmpeg *) data;
    fFmpeg->start();
    pthread_exit(&thread_start);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_peter_myplayer_player_WePlayer_nativeStart(JNIEnv *env, jobject thiz) {
    if(fFmpeg != NULL)
    {
        pthread_create(&thread_start, NULL, startCallBack, fFmpeg);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_peter_myplayer_player_WePlayer_nativePause(JNIEnv *env, jobject thiz) {
    if(fFmpeg != NULL)
    {
        fFmpeg->pause();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_peter_myplayer_player_WePlayer_nativeResume(JNIEnv *env, jobject thiz) {
    if(fFmpeg != NULL)
    {
        fFmpeg->resume();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_peter_myplayer_player_WePlayer_nativeStop(JNIEnv *env, jobject instance) {
    if(!nexit)
    {
        return;
    }

    jclass clz = env->GetObjectClass(instance);
    jmethodID jmid_next = env->GetMethodID(clz, "onCallNext", "()V");

    nexit = false;
    if(fFmpeg != NULL)
    {
        fFmpeg->release();
        delete(fFmpeg);
        fFmpeg = NULL;
        if(callJava != NULL)
        {
            delete(callJava);
            callJava = NULL;
        }
        if(playstatus != NULL)
        {
            delete(playstatus);
            playstatus = NULL;
        }
    }
    nexit = true;
    env->CallVoidMethod(instance, jmid_next);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_peter_myplayer_player_WePlayer_nativeSeek(JNIEnv *env, jobject thiz, jint secds) {
    if(fFmpeg != NULL)
    {
        fFmpeg->seek(secds);
    }
}