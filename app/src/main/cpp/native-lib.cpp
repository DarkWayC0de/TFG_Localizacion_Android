//
// Created by DarkWayC0de on 15/06/2021.
//
#include <jni.h>
#include <string>



int hola(){
    return 1;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_example_localizacionInalambrica_App_hola1(JNIEnv *env, jobject thiz) {
    return hola();
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_localizacionInalambrica_App_mio(JNIEnv *env, jobject thiz) {
    std::string mio="mio";
    return env->NewStringUTF(mio.c_str());
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_localizacionInalambrica_StartActivity_mio1(JNIEnv *env, jobject thiz) {
    // TODO: implement mio()
}