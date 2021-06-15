//
// Created by DarkWayC0de on 15/06/2021.
//
#include <jni.h>
//#include <string>



int hola(){
    return 1;
}

extern JNIEXPORT jint JNICALL
Java_com_example_localizacionInalambrica_App_hola(JNIEnv *env, jobject thiz) {
    return hola();
}

/** STRING NO ESTA EN C
 *
extern JNIEXPORT jstring JNICALL
Java_com_example_localizacionInalambrica_App_mio(JNIEnv *env, jobject thiz) {
    std::string mio="mio";
    return env->NewStringUTF(mio.c_str());
}
 */
extern JNIEXPORT jstring JNICALL
Java_com_example_localizacionInalambrica_StartActivity_mio(JNIEnv *env, jobject thiz) {
    // TODO: implement mio()
}