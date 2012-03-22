LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libZinniaJNI
LOCAL_SRC_FILES := character.cpp feature.cpp libzinnia.cpp param.cpp recognizer.cpp sexp.cpp svm.cpp trainer.cpp \
jni/org_zinnia_Character.cpp \
jni/org_zinnia_Recognizer.cpp \
jni/org_zinnia_Result.cpp \
jni/org_zinnia_Trainer.cpp

#zinnia.cpp
 
LOCAL_LDLIBS := -llog
LOCAL_LDLIBS := ./libsarmeabi/libZinniaJNI.so

include $(BUILD_SHARED_LIBRARY)
