SHELL          := /bin/bash
DEVICE_IP      := 192.168.42.78
APK            := app/build/outputs/apk/debug/app-debug.apk
AAB            := app/build/outputs/bundle/release/app-release.aab
KEYSTORE_PROPS := publish/keystore.properties

.PHONY: all clean build install push bundle test

all: install

test:
	source .envrc && ./gradlew :app:testDebugUnitTest

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

bundle: $(KEYSTORE_PROPS)
	@grep -q "FILL_IN" $(KEYSTORE_PROPS) && { echo "ERROR: fill in passwords in $(KEYSTORE_PROPS) first"; exit 1; } || true
	source .envrc && ./gradlew :app:bundleRelease
	@echo "AAB ready: $(AAB)"

$(KEYSTORE_PROPS):
	@echo "ERROR: $(KEYSTORE_PROPS) not found — run publish/keystore/create-keystore.sh first"; exit 1
