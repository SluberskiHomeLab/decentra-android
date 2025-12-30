# Build Instructions for Decentra Android

This document provides detailed instructions for building the Decentra Android application.

## Prerequisites

### Required Tools

1. **Android Studio** (Hedgehog or later recommended)
   - Download from: https://developer.android.com/studio
   - Install with Android SDK and tools

2. **JDK 17** or higher
   - Included with Android Studio
   - Or download from: https://adoptium.net/

3. **Android SDK**
   - SDK Platform: Android 14.0 (API Level 34)
   - Minimum SDK: Android 7.0 (API Level 24)

### Optional Tools

- **ADB (Android Debug Bridge)**: For installing to devices
- **Gradle**: Can use wrapper (included) or system installation

## Building with Android Studio

### Step 1: Import Project

1. Open Android Studio
2. Select **File > Open** or **Open an Existing Project**
3. Navigate to the `decentra-android` directory
4. Click **OK**

### Step 2: Sync Project

Android Studio will automatically detect the Gradle configuration and prompt you to sync.

1. Wait for Gradle sync to complete
2. If prompted, install any missing SDK components
3. Resolve any dependency issues through Android Studio's interface

### Step 3: Build APK

#### Debug Build

1. In Android Studio, select **Build > Build Bundle(s) / APK(s) > Build APK(s)**
2. Wait for the build to complete
3. Click **locate** in the notification to open the APK location
4. APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

#### Release Build

1. First, create a signing keystore (if you don't have one):
   ```bash
   keytool -genkey -v -keystore decentra-release.keystore -alias decentra \
     -keyalg RSA -keysize 2048 -validity 10000
   ```

2. Configure signing in `app/build.gradle.kts`:
   ```kotlin
   android {
       signingConfigs {
           create("release") {
               storeFile = file("path/to/decentra-release.keystore")
               storePassword = "your-store-password"
               keyAlias = "decentra"
               keyPassword = "your-key-password"
           }
       }
       buildTypes {
           release {
               signingConfig = signingConfigs.getByName("release")
               // ... other settings
           }
       }
   }
   ```

3. Build: **Build > Build Bundle(s) / APK(s) > Build APK(s)**
4. Select **Build Variant**: Change to "release"
5. APK will be at: `app/build/outputs/apk/release/app-release.apk`

## Building from Command Line

### Using Gradle Wrapper (Recommended)

The project includes a Gradle wrapper, which downloads and uses the correct Gradle version automatically.

#### Debug Build

```bash
cd decentra-android
./gradlew assembleDebug
```

The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

#### Release Build

```bash
./gradlew assembleRelease
```

The APK will be generated at: `app/build/outputs/apk/release/app-release.apk`

### Using System Gradle

If you have Gradle installed system-wide:

```bash
gradle assembleDebug    # For debug build
gradle assembleRelease  # For release build
```

## Installing the APK

### On a Connected Device

With ADB installed and a device connected via USB:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

Or directly from Android Studio:
1. Select **Run > Run 'app'**
2. Choose your device from the list
3. Click **OK**

### Sideloading on Device

1. Transfer the APK to your device (via USB, email, cloud, etc.)
2. On the device, enable **Unknown Sources** in Settings
3. Open the APK file using a file manager
4. Follow prompts to install

## Common Build Issues

### Issue: Gradle Sync Failed

**Symptom**: Android Studio shows "Gradle sync failed"

**Solutions**:
- Check internet connection (required to download dependencies)
- Clear Gradle cache: `./gradlew clean cleanBuildCache`
- Invalidate caches in Android Studio: **File > Invalidate Caches / Restart**
- Update Gradle wrapper: `./gradlew wrapper --gradle-version 8.2`

### Issue: SDK Not Found

**Symptom**: Build fails with "SDK location not found"

**Solutions**:
- Create `local.properties` in the project root:
  ```properties
  sdk.dir=/path/to/Android/Sdk
  ```
- On Linux/Mac: Usually `~/Android/Sdk`
- On Windows: Usually `C:\Users\YourName\AppData\Local\Android\Sdk`

### Issue: Dependency Resolution Failed

**Symptom**: Cannot resolve dependencies

**Solutions**:
- Check that Google and Maven Central repositories are accessible
- Try building with `--refresh-dependencies`:
  ```bash
  ./gradlew assembleDebug --refresh-dependencies
  ```
- Check your `build.gradle.kts` files have correct repository configurations

### Issue: Out of Memory

**Symptom**: Build fails with `OutOfMemoryError`

**Solutions**:
- Increase Gradle memory in `gradle.properties`:
  ```properties
  org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
  ```
- Close other applications to free RAM
- Use the `--no-daemon` flag: `./gradlew assembleDebug --no-daemon`

## Verifying the Build

After building, you can verify the APK:

### Check APK Contents

```bash
# List APK contents
unzip -l app/build/outputs/apk/debug/app-debug.apk

# Or use Android's apkanalyzer (if available)
apkanalyzer manifest print app/build/outputs/apk/debug/app-debug.apk
```

### Test Installation

```bash
# Install on emulator or device
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch the app
adb shell am start -n com.decentra.app/.MainActivity
```

## Building for Different Architectures

The default build includes all architectures. To build for specific ABIs:

Modify `app/build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }
    
    // Or create separate APKs per ABI
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }
}
```

Then build:
```bash
./gradlew assembleDebug
```

This will create separate APKs for each architecture in the output folder.

## Clean Build

To clean all build artifacts:

```bash
./gradlew clean
```

Or in Android Studio: **Build > Clean Project**

## Continuous Integration

For CI/CD pipelines, use:

```bash
# Install dependencies and build (headless)
./gradlew assembleDebug --no-daemon --stacktrace
```

Example GitHub Actions workflow snippet:

```yaml
- name: Build APK
  run: |
    chmod +x ./gradlew
    ./gradlew assembleDebug --no-daemon --stacktrace
    
- name: Upload APK
  uses: actions/upload-artifact@v3
  with:
    name: app-debug
    path: app/build/outputs/apk/debug/app-debug.apk
```

## Additional Resources

- [Android Developer Documentation](https://developer.android.com/)
- [Gradle Build Tool](https://gradle.org/)
- [Android Studio User Guide](https://developer.android.com/studio/intro)

## Support

For build issues:
1. Check this document first
2. Review error messages carefully
3. Search Android Studio's issue tracker
4. Open an issue in this repository with:
   - Full error message
   - Build environment (OS, Android Studio version, Gradle version)
   - Steps to reproduce
