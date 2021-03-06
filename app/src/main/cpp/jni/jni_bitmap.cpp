#include <jni.h>
#include <android/bitmap.h>
#include <cstring>
jobject generateBitmap(JNIEnv *env, uint32_t width, uint32_t height);
extern "C"
JNIEXPORT jobject JNICALL
Java_com_liyaan_jni_JNIBitmap_callNativeMirrorBitmap(JNIEnv *env, jobject thiz,
        jobject bitmap) {
    AndroidBitmapInfo bitmapInfo;
    int ret;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &bitmapInfo)) < 0) {
        return NULL;
    }
    void *bitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels)) < 0) {
        return NULL;
    }
    uint32_t newWidth = bitmapInfo.width;
    uint32_t newHeight = bitmapInfo.height;
    uint32_t *newBitmapPixels = new uint32_t[newWidth * newHeight];
    int whereToget = 0;
    for (int y = 0; y < newHeight; ++y) {
        for (int x = newWidth - 1; x >= 0; x--) {
            uint32_t pixel = ((uint32_t *) bitmapPixels)[whereToget++];
            newBitmapPixels[newWidth * y + x] = pixel;
        }
    }
    AndroidBitmap_unlockPixels(env, bitmap);
    jobject newBitmap = generateBitmap(env,newWidth,newHeight);
    void *resultBitmapPixels;
    AndroidBitmap_lockPixels(env,newBitmap,&resultBitmapPixels);
    int pixelsCount = newWidth * newHeight;
    memcpy((uint32_t *)resultBitmapPixels,newBitmapPixels,  sizeof(uint32_t) * pixelsCount);
    AndroidBitmap_unlockPixels(env,newBitmap);

    delete [] newBitmapPixels;

    return newBitmap;
}
jobject generateBitmap(JNIEnv *env,uint32_t width,uint32_t height){
    jclass bitmapCls = env->FindClass("android/graphics/Bitmap");
    jmethodID creatBitmapFunction = env->GetStaticMethodID(bitmapCls,
            "createBitmap","(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jstring configName = env->NewStringUTF("ARGB_8888");
    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID valueOfBitmapConfigFunction =
            env->GetStaticMethodID(bitmapConfigClass,
                    "valueOf", "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    jobject bitmapConfig = env->CallStaticObjectMethod(bitmapConfigClass,
                                                       valueOfBitmapConfigFunction, configName);
    jobject newBitmap =env->CallStaticObjectMethod(bitmapCls,
                                                   creatBitmapFunction,
                                                   width,
                                                   height, bitmapConfig);
    return newBitmap;

}
