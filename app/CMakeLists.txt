# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.
project("localizacion_inalambrica")
add_library( # Specifies the name of the library.
             cripto-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        src/main/cpp/Chacha20-256-csprng.c
        src/main/cpp/Chaskey16_29bytes.c
        src/main/cpp/hola.c
        src/main/cpp/hola_jni.c)
find_library(log-lib log)

include_directories(src/main/cpp/include)
target_link_libraries(cripto-lib
         ${log-lib})