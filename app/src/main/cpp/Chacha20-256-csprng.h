//
// Created by DarkWayC0de on 24/06/2021.
//

#ifndef LOCALIZACION_INALAMBRICA_CHACHA20_256_CSPRNG_H
#define LOCALIZACION_INALAMBRICA_CHACHA20_256_CSPRNG_H

/**
* Autor:        Arget/MontseHack
* Fecha:        19 03 2017 20:16
* Nombre:       ChaCha20-256-CSPRNG
* Descripción:  Pequeña implementación del algoritmo
*               ChaCha20 de 256bits de Daniel J. Bernstein,
*               para su uso como CSPRNG o algoritmo
*               de cifrado.
*               Funciona empleando una semilla de 32 bytes
*               como clave para el algoritmo de cifrado.
*               El nonce también se podría considerar parte
*               de la semilla.
*               El keystream obtenido se empleará byte a byte
*               como la salida pseudo-aleatoria, apta para
*               uso criptográfico.
*               Además, este algoritmo, al igual que su
*               predecesor Salsa20, funciona únicamente
*               mediante operaciones de suma-rotación-xor,
*               lo que hace impide timing attacks.
* Referencias:
*               RFC 7539 - ChaCha20 and Poly1305 for IETF Protocols - IETF
*                   https://tools.ietf.org/html/rfc7539
*               Salsa20  - Daniel J. Bernstein et al.
*                   https://cr.yp.to/snuffle/spec.pdf
*               ChaCha20 - Daniel J. Bernstein et al.
*                   https://cr.yp.to/chacha.html
*               ChaCha20-128-CSPRNG - https://github.com/Emill
*                   https://gist.github.com/Emill/d8e8df7269f75b9485a2
*/
//https://gist.github.com/cieplak/560c8598687deb8d21fbc56cf0be2347#file-chacha20-256-csprng-h
#include <stdint.h> /* Para definiciones de uint*_t */
#include <string.h> /* memcpy() */

void chachaSeed(const uint8_t s[32], const uint8_t n[12]);

uint8_t chachaGet(void);


#endif //LOCALIZACION_INALAMBRICA_CHACHA20_256_CSPRNG_H
