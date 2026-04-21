SHELL      := /bin/bash
DEVICE_IP  := 192.168.42.78
APK        := app/build/outputs/apk/debug/app-debug.apk

.PHONY: all clean build install push

all: install

clean:
	source .envrc && ./gradlew clean

build:
	source .envrc && ./gradlew :app:assembleDebug

install: build
	@source .envrc; \
	SERIAL=$$(adb devices 2>/dev/null | grep -oE '$(DEVICE_IP):[0-9]+' | head -1); \
	[ -z "$$SERIAL" ] && SERIAL=$$(adb mdns services 2>/dev/null | grep RFCX | awk '{print $$1"._adb-tls-connect._tcp"}' | head -1); \
	[ -z "$$SERIAL" ] && SERIAL=$$(adb devices 2>/dev/null | grep RFCX | awk '{print $$1}' | head -1); \
	[ -z "$$SERIAL" ] && { echo "ERROR: device not found — ensure phone is on the same WiFi and wireless debugging is on"; exit 1; }; \
	echo "Installing on $$SERIAL"; \
	adb -s $$SERIAL install -r $(APK)

push: install
