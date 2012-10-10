LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)


ifndef GSTREAMER_SDK_ROOT
ifndef GSTREAMER_SDK_ROOT_ANDROID
$(error GSTREAMER_SDK_ROOT_ANDROID is not defined!)
endif
GSTREAMER_SDK_ROOT        := $(GSTREAMER_SDK_ROOT_ANDROID)
endif

LOCAL_MODULE    := android-aurena
LOCAL_SRC_FILES := android-aurena.c snra-json.c snra-client.c
LOCAL_SHARED_LIBRARIES := gstreamer_android
LOCAL_LDLIBS := -landroid -Wl,-Bstatic $(shell pkg-config --libs json-glib-1.0 libsoup-2.4 gstreamer-net-0.10) -Wl,-Bdynamic
LOCAL_CFLAGS := $(shell pkg-config --cflags json-glib-1.0 libsoup-2.4 gstreamer-net-0.10)
include $(BUILD_SHARED_LIBRARY)

GSTREAMER_NDK_BUILD_PATH  := $(GSTREAMER_SDK_ROOT)/share/gst-android/ndk-build/
include $(GSTREAMER_NDK_BUILD_PATH)/plugins.mk
GSTREAMER_PLUGINS         := $(GSTREAMER_PLUGINS_CORE) $(GSTREAMER_PLUGINS_PLAYBACK) $(GSTREAMER_PLUGINS_CODECS) $(GSTREAMER_PLUGINS_NET) $(GSTREAMER_PLUGINS_SYS)
G_IO_MODULES              := gnutls
include $(GSTREAMER_NDK_BUILD_PATH)/gstreamer.mk
