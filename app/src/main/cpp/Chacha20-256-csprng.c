//
// Created by DarkWayC0de on 24/06/2021.
//

#include "Chacha20-256-csprng.h"

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

#include <stdint.h> /* Para definiciones de uint*_t */
#include <string.h> /* memcpy() */

/* Rota un entero 'x' de 32 bits 'n' bits a la izquierda */
#define RtoL(x, n) \
        ((x << n) | (x >> (32 - n)))

/* Definición de la función Quarter-Round del ChaCha20 */
#define chachaQR(a, b, c, d) \
    estado[a] += estado[b]; estado[d] ^= estado[a]; estado[d] = RtoL(estado[d], 16); \
    estado[c] += estado[d]; estado[b] ^= estado[c]; estado[b] = RtoL(estado[b], 12); \
    estado[a] += estado[b]; estado[d] ^= estado[a]; estado[d] = RtoL(estado[d],  8); \
    estado[c] += estado[d]; estado[b] ^= estado[c]; estado[b] = RtoL(estado[b],  7);

/* Cuatro constantes del ChaCha20: "expand 32-byte k" */
static const uint32_t chachaConst[4] = {0x61707865,
                                        0x3320646e,
                                        0x79622d32,
                                        0x6b206574};

static uint32_t chachaKey[8],
        chachaCount = 0,
        chachaNonce[3];

/* Almacena el número de bytes que quedan por leer de chachaRandomOutput */
static uint8_t chacha_quedanPorLeer = 0;

/* Es el keystream pseudo-aleatorio producido por chacha() al procesar
   un bloque de 64 bytes */
static uint8_t chachaRandomOutput[64];

/**
* Función:
*    void
*    chacha(void);
*
* Entradas:
*    chachaKey
*    chachaCount
*    chachaNonce
*
* Salidas:
*    chachaRandomOutput
*
* Descripción:
*      Produce por bloque de 64 bytes procesado un
*    keystream pseudo-aleatorio de 64 bytes.
*
*/
static void chacha()
{
    uint32_t estado[16];
    uint32_t i;

    /* Colocando el estado siguiendo el esquema:
       cccccccc  cccccccc  cccccccc  cccccccc
       kkkkkkkk  kkkkkkkk  kkkkkkkk  kkkkkkkk
       kkkkkkkk  kkkkkkkk  kkkkkkkk  kkkkkkkk
       bbbbbbbb  nnnnnnnn  nnnnnnnn  nnnnnnnn
    c=constant k=key b=blockcount n=nonce */
    memcpy(estado, chachaConst, 16);
    memcpy(&estado[4], chachaKey, 32);
    chachaCount++;
    estado[12] = chachaCount;
    memcpy(&estado[13], chachaNonce, 12);

    /* Se copia al búfer de salida el
       estado para uso posterior */
    memcpy(chachaRandomOutput, estado, 64);

    /* 20 rondas alternando diagonal-columna */
    for(i = 0; i < 10; i++)
    {
        chachaQR(0, 4,  8, 12) // Diagonal
        chachaQR(1, 5,  9, 13) // Columna
        chachaQR(2, 6, 10, 14) // Diagonal
        chachaQR(3, 7, 11, 15) // Columna
        chachaQR(0, 5, 10, 15) // Diagonal
        chachaQR(1, 6, 11, 12) // Columna
        chachaQR(2, 7,  8, 13) // Diagonal
        chachaQR(3, 4,  9, 14) // Columna
    }

    /* Se finaliza sumando al estado inicial
      (guardado en chachaRandomOutput) el estado actual */
    uint32_t *q = (uint32_t*)chachaRandomOutput;
    for(i = 0; i < 64; i++)
        q[i] += estado[i];
}

/* Pone a 0 los contadores de chachaCount y chacha_quedanPorLeer
   para iniciar un nuevo flujo pseudo-aleatorio con una nueva
   semilla apuntada por s, y un nuevo nonce apuntado por n,
   esta semilla y este nonce los copia a los inputs de chacha()
   para su posterior llamada mediante chachaGet(). */
void chachaSeed(const uint8_t s[32], const uint8_t n[12])
{
    memcpy(chachaKey, s, 32);
    memcpy(chachaNonce, n, 12);
    chachaCount = 0;
    chacha_quedanPorLeer = 0;
}

/* Devuelve el siguiente byte del flujo pseudo-aleatorio
   producido por chacha(). Debido a que chacha() produce
   el flujo en bloques de 64 bytes, esta función maneja el
   contador chacha_quedanPorLeer que almacena el número de
   bytes por leer del flujo, en caso de que se llame a
   esta función y queden 0 bytes por leer se llamará a chacha()
   para generar 64 bytes más. Dichos flujo lo almacena
   el array chachaRandomOutput. */
uint8_t chachaGet(void)
{
    if(!chacha_quedanPorLeer)
    {
        chacha();
        chacha_quedanPorLeer = 64;
    }
    return chachaRandomOutput[64 - (chacha_quedanPorLeer--)];
}
