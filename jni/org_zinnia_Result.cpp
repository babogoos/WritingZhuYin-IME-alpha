#include "../zinnia.h"
#include "org_zinnia_Result.h"
#include <string.h>
/*
 * Class:     org_zinnia_Result
 * Method:    nativeDelete
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_org_zinnia_Result_nativeDelete
  (JNIEnv *, jobject, jlong address){
	zinnia::Result * result = (zinnia::Result*)address;
	delete result;
}

/*
 * Class:     org_zinnia_Result
 * Method:    value
 * Signature: (JJ)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zinnia_Result_value
  (JNIEnv * env, jobject, jlong address, jlong i){
	zinnia::Result * result = (zinnia::Result*)address;
	const char * s = result->value(i);
	return env->NewStringUTF(s);
}

/*
 * Class:     org_zinnia_Result
 * Method:    score
 * Signature: (JJ)F
 */
JNIEXPORT jfloat JNICALL Java_org_zinnia_Result_score
(JNIEnv * env, jobject, jlong address, jlong i){
	zinnia::Result * result = (zinnia::Result*)address;
	return result->score(i);
}

/*
 * Class:     org_zinnia_Result
 * Method:    size
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_zinnia_Result_size
  (JNIEnv *, jobject, jlong address){
	zinnia::Result * result = (zinnia::Result*)address;
	return result->size();
}
