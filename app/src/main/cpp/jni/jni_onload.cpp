#include <jni.h>
#include <string>


extern "C"
JNIEXPORT jint JNICALL
Java_com_liyaan_jni_JNIDynamicLoad_sum(JNIEnv *env, jobject thiz, jint x, jint y) {
    jint s = x+y;
    return s;
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_liyaan_jni_JNIDynamicLoad_geNativeString(JNIEnv *env, jobject thiz) {
    std::string hello = "hello word";
    return env->NewStringUTF(hello.c_str());
}
