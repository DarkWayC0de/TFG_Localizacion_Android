//
// Created by DarkWayC0de on 24/06/2021.
//
#include "include/hola_jni.h"
extern  JNIEXPORT jint  JNICALL
Java_com_example_localizacionInalambrica_App_adios(JNIEnv *env, jobject thiz) {
    return adios(2);
}

extern JNIEXPORT jint JNICALL
Java_com_example_localizacionInalambrica_App_adios2(JNIEnv *env, jobject thiz, jint a) {
    return adios(a);
}

JNIEXPORT jint JNICALL
Java_com_example_localizacionInalambrica_App_adios3(JNIEnv *env, jobject thiz) {
    // TODO: implement adios3()
}