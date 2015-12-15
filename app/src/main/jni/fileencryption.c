#include <jni.h>
#include <stdio.h>
#include <android/log.h>


#define  LOG_TAG    "fileencryption"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

JNIEXPORT jint JNICALL
Java_com_example_zzt_tagdaily_logic_crypt_FileEncryption_hello(JNIEnv *env, jobject instance) {
    printf("%s", "I am so sad");
    return 1;
}

JNIEXPORT jboolean JNICALL
Java_com_example_zzt_tagdaily_logic_crypt_FileEncryption_opensslEncrypt(JNIEnv *env,
                                                                        jobject instance) {

    LOGI("%s", "I am so sad");
    return 1;
}