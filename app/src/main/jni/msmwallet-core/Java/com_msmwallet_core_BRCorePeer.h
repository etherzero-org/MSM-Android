/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_msmwallet_core_BRCorePeer */

#ifndef _Included_com_msmwallet_core_BRCorePeer
#define _Included_com_msmwallet_core_BRCorePeer
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    getAddress
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_msmwallet_core_BRCorePeer_getAddress
  (JNIEnv *, jobject);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    getPort
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_msmwallet_core_BRCorePeer_getPort
  (JNIEnv *, jobject);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    getTimestamp
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_msmwallet_core_BRCorePeer_getTimestamp
  (JNIEnv *, jobject);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    setEarliestKeyTime
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_msmwallet_core_BRCorePeer_setEarliestKeyTime
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    setCurrentBlockHeight
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_msmwallet_core_BRCorePeer_setCurrentBlockHeight
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    getConnectStatusValue
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_msmwallet_core_BRCorePeer_getConnectStatusValue
  (JNIEnv *, jobject);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    connect
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_msmwallet_core_BRCorePeer_connect
  (JNIEnv *, jobject);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    disconnect
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_msmwallet_core_BRCorePeer_disconnect
  (JNIEnv *, jobject);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    scheduleDisconnect
 * Signature: (D)V
 */
JNIEXPORT void JNICALL Java_com_msmwallet_core_BRCorePeer_scheduleDisconnect
  (JNIEnv *, jobject, jdouble);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    setNeedsFilterUpdate
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_msmwallet_core_BRCorePeer_setNeedsFilterUpdate
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    getHost
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_msmwallet_core_BRCorePeer_getHost
  (JNIEnv *, jobject);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    getVersion
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_msmwallet_core_BRCorePeer_getVersion
  (JNIEnv *, jobject);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    getUserAgent
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_msmwallet_core_BRCorePeer_getUserAgent
  (JNIEnv *, jobject);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    getLastBlock
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_msmwallet_core_BRCorePeer_getLastBlock
  (JNIEnv *, jobject);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    getFeePerKb
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_msmwallet_core_BRCorePeer_getFeePerKb
  (JNIEnv *, jobject);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    getPingTime
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_com_msmwallet_core_BRCorePeer_getPingTime
  (JNIEnv *, jobject);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    createJniCorePeerNatural
 * Signature: ([BIJ)J
 */
JNIEXPORT jlong JNICALL Java_com_msmwallet_core_BRCorePeer_createJniCorePeerNatural
  (JNIEnv *, jclass, jbyteArray, jint, jlong);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    createJniCorePeer
 * Signature: ([B[B[B)J
 */
JNIEXPORT jlong JNICALL Java_com_msmwallet_core_BRCorePeer_createJniCorePeer
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jbyteArray);

/*
 * Class:     com_msmwallet_core_BRCorePeer
 * Method:    createJniCorePeerMagic
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_msmwallet_core_BRCorePeer_createJniCorePeerMagic
  (JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif
