#include "../zinnia.h"
#include "org_zinnia_Recognizer.h"
#include <string.h>
/*
 * Class:     org_zinnia_Recognizer
 * Method:    nativeNew
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_zinnia_Recognizer_nativeNew
  (JNIEnv *, jobject)
{
	return (jlong)zinnia::Recognizer::create();
};

/*
 * Class:     org_zinnia_Recognizer
 * Method:    nativeDelete
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_org_zinnia_Recognizer_nativeDelete
  (JNIEnv *, jobject, jlong address)
{
	delete (zinnia::Recognizer*)address;
}
/*
 * Class:     org_zinnia_Recognizer
 * Method:    open
 * Signature: (JLjava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_zinnia_Recognizer_open__JLjava_lang_String_2
  (JNIEnv * env, jobject, jlong address, jstring str){
	zinnia::Recognizer* recognizer = (zinnia::Recognizer*)address;
	const char *s = env->GetStringUTFChars(str, NULL);
	jboolean ret = recognizer->open(s);
	env->ReleaseStringUTFChars(str, s);
	return ret;
}

/*
 * Class:     org_zinnia_Recognizer
 * Method:    open
 * Signature: (JLjava/lang/String;J)Z
 */
JNIEXPORT jboolean JNICALL Java_org_zinnia_Recognizer_open__JLjava_lang_String_2J
  (JNIEnv * env, jobject, jlong address, jstring str, jlong mode){
	zinnia::Recognizer* recognizer = (zinnia::Recognizer*)address;
	const char *s = env->GetStringUTFChars(str, NULL);
	jboolean ret = recognizer->open(s,mode);
	env->ReleaseStringUTFChars(str, s);
	return ret;
}

/*
 * Class:     org_zinnia_Recognizer
 * Method:    close
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_org_zinnia_Recognizer_close
  (JNIEnv *, jobject, jlong address){
	zinnia::Recognizer* recognizer = (zinnia::Recognizer*)address;
	return recognizer->close();
}

/*
 * Class:     org_zinnia_Recognizer
 * Method:    size
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_zinnia_Recognizer_size
  (JNIEnv *, jobject, jlong address){
	zinnia::Recognizer* recognizer = (zinnia::Recognizer*)address;
	return recognizer->size();
}

/*
 * Class:     org_zinnia_Recognizer
 * Method:    value
 * Signature: (JJ)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zinnia_Recognizer_value
  (JNIEnv * env, jobject, jlong address, jlong i)
{
	zinnia::Recognizer* recognizer = (zinnia::Recognizer*)address;
	jstring jstr = env->NewStringUTF(recognizer->value(i));
	return jstr;
}

/*
 * Class:     org_zinnia_Recognizer
 * Method:    classify
 * Signature: (JJJ)Lorg/zinnia/Result;
 */
JNIEXPORT jlong JNICALL Java_org_zinnia_Recognizer_classify
  (JNIEnv *, jobject, jlong address, jlong character_address, jlong nbest)
{
	zinnia::Recognizer* recognizer = (zinnia::Recognizer*)address;
	zinnia::Character* character = (zinnia::Character*)character_address;
	return (jlong)recognizer->classify(*character,(size_t)nbest);
}

/*
 * Class:     org_zinnia_Recognizer
 * Method:    what
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zinnia_Recognizer_what
  (JNIEnv *env, jobject, jlong address)
{
	zinnia::Recognizer* recognizer = (zinnia::Recognizer*)address;
	jstring jstr = env->NewStringUTF(recognizer->what());
}
