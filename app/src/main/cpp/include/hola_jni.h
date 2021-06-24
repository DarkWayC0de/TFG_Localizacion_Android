//
// Created by DarkWayC0de on 24/06/2021.
//

#ifndef LOCALIZACION_INALAMBRICA_HOLA_JNI_H
#define LOCALIZACION_INALAMBRICA_HOLA_JNI_H
#include <jni.h>
#include "../hola.h"

extern  JNIEXPORT jint  JNICALL
Java_com_example_localizacionInalambrica_App_adios(JNIEnv *env, jobject thiz);

extern JNIEXPORT jint JNICALL
Java_com_example_localizacionInalambrica_App_adios2(JNIEnv *env, jobject thiz, jint a);

#endif //LOCALIZACION_INALAMBRICA_HOLA_JNI_H
