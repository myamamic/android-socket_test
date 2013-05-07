LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
    native_socket.cpp

LOCAL_CPPFLAGS := -O2 -g

#LOCAL_LDLIBS :=  -ldl -llog
LOCAL_SHARED_LIBRARIES := \
    liblog

LOCAL_MODULE_TAGS := optional

LOCAL_MODULE := native_socket

include $(BUILD_EXECUTABLE)
