#include <jni.h>
#include <stdio.h>

JNIEXPORT jint JNICALL
Java_com_example_zzt_tagdaily_logic_crypt_FileEncryption_hello(JNIEnv *env, jobject instance) {
    printf("%s", "I am so sad");
    return 1;
}

