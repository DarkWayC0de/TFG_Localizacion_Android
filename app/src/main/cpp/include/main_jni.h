//
// Created by DarkWayC0de on 24/06/2021.
//

#ifndef LOCALIZACION_INALAMBRICA_MAIN_JNI_H
#define LOCALIZACION_INALAMBRICA_MAIN_JNI_H

#include <jni.h>
#include "../main.h"

JNIEXPORT jstring JNICALL
Java_com_example_localizacionInalambrica_servicios_ServicioBluetooth_location_1to_1encode_1and_1encrypter(
        JNIEnv *env, jobject thiz, jint longitud, jint latitud, jint altitud, jint bearing,
        jint speed);

#endif //LOCALIZACION_INALAMBRICA_MAIN_JNI_H
