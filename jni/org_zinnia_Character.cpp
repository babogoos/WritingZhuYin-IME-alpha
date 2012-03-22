#include "../zinnia.h"
#include "org_zinnia_Character.h"
#include <string.h>
/*
 * Class:     org_zinnia_Character
 * Method:    nativeNew
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_zinnia_Character_nativeNew
  (JNIEnv *, jobject){
	return (jlong)(zinnia::Character::create());
}
/*
 * Class:     org_zinnia_Character
 * Method:    nativeDelete
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_org_zinnia_Character_nativeDelete(JNIEnv* env, jobject obj, jlong address){
	zinnia::Character * character = (zinnia::Character*)address;
	delete character;
};

/*
 * Class:     org_zinnia_Character
 * Method:    set_value
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_zinnia_Character_set_1value
  (JNIEnv * env, jobject, jlong address, jstring str){
	zinnia::Character* character = (zinnia::Character*)address;
	const char *s = env->GetStringUTFChars(str, NULL);
	character->set_value(s);
	env->ReleaseStringUTFChars(str, s);
}

/*
 * Class:     org_zinnia_Character
 * Method:    value
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zinnia_Character_value
  (JNIEnv * env, jobject, jlong address){
	zinnia::Character* character = (zinnia::Character*)address;
	const char * s = character->value();
	return env->NewStringUTF(s);
}
/*
 * Class:     org_zinnia_Character
 * Method:    set_width
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_org_zinnia_Character_set_1width
  (JNIEnv *, jobject, jlong address, jlong width){
	zinnia::Character* character = (zinnia::Character*)address;
	character->set_width(width);
}
/*
 * Class:     org_zinnia_Character
 * Method:    set_height
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_org_zinnia_Character_set_1height
  (JNIEnv *, jobject, jlong address, jlong height){
	zinnia::Character* character = (zinnia::Character*)address;
	character->set_height(height);
}

/*
 * Class:     org_zinnia_Character
 * Method:    width
 * Signature: (J)V
 */
JNIEXPORT jlong JNICALL Java_org_zinnia_Character_width
  (JNIEnv *, jobject, jlong address){
	zinnia::Character* character = (zinnia::Character*)address;
	return character->width();
}

/*
 * Class:     org_zinnia_Character
 * Method:    height
 * Signature: (J)V
 */
JNIEXPORT jlong JNICALL Java_org_zinnia_Character_height
(JNIEnv *, jobject, jlong address){
	zinnia::Character* character = (zinnia::Character*)address;
	return character->height();
}

/*
 * Class:     org_zinnia_Character
 * Method:    clear
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_zinnia_Character_clear
(JNIEnv *, jobject, jlong address){
	zinnia::Character* character = (zinnia::Character*)address;
	character->clear();
}

/*
 * Class:     org_zinnia_Character
 * Method:    add
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_org_zinnia_Character_add
  (JNIEnv *, jobject, jlong address,jlong id, jint x, jint y){
	zinnia::Character* character = (zinnia::Character*)address;
	character->add(id,x,y);
}

/*
 * Class:     org_zinnia_Character
 * Method:    strokes_size
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_zinnia_Character_strokes_1size
  (JNIEnv *, jobject, jlong address){
	zinnia::Character* character = (zinnia::Character*)address;
	return character->strokes_size();
}

/*
 * Class:     org_zinnia_Character
 * Method:    stroke_size
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_org_zinnia_Character_stroke_1size
  (JNIEnv *, jobject, jlong address, jlong size){
	zinnia::Character* character = (zinnia::Character*)address;
	return character->stroke_size(size);
}
/*
 * Class:     org_zinnia_Character
 * Method:    x
 * Signature: (JJJ)I
 */
JNIEXPORT jint JNICALL Java_org_zinnia_Character_x
  (JNIEnv *, jobject, jlong address, jlong id, jlong i){
	zinnia::Character* character = (zinnia::Character*)address;
	return character->x(id,i);
}

/*
 * Class:     org_zinnia_Character
 * Method:    y
 * Signature: (JJJ)I
 */
JNIEXPORT jint JNICALL Java_org_zinnia_Character_y
(JNIEnv *, jobject, jlong address, jlong id, jlong i){
	zinnia::Character* character = (zinnia::Character*)address;
	return character->y(id,i);
}

/*
 * Class:     org_zinnia_Character
 * Method:    parse
 * Signature: (JLjava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_zinnia_Character_parse
  (JNIEnv * env, jobject, jlong address, jstring jstr){
	zinnia::Character* character = (zinnia::Character*)address;
	const char *s = env->GetStringUTFChars(jstr, NULL);
	jboolean ret = character->parse(s);
	env->ReleaseStringUTFChars(jstr, s);
	return ret;
}

/*
 * Class:     org_zinnia_Character
 * Method:    what
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_zinnia_Character_what
  (JNIEnv * env, jobject, jlong address){
	zinnia::Character* character = (zinnia::Character*)address;
	return env->NewStringUTF(character->what());
}
