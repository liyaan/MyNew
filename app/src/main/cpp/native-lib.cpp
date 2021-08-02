#include <jni.h>
#include <string>
extern "C" jstring
Java_com_liyaan_mynew_JniDemoActivity_stringFromJni(
        JNIEnv* env,
        jobject /* this */) {
    const char *hello = "Hello from C++";
    std::string text = "hello word";
    return env->NewStringUTF(text.c_str());
}
