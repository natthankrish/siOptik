@echo off
"C:\\Users\\Juan Christopher\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HD:\\Folder Kuliah Cadangan\\Sems 6\\PPL\\ocr-android\\opencv\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=29" ^
  "-DANDROID_PLATFORM=android-29" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\Juan Christopher\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\Juan Christopher\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\Juan Christopher\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\Juan Christopher\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=D:\\Folder Kuliah Cadangan\\Sems 6\\PPL\\ocr-android\\opencv\\build\\intermediates\\cxx\\RelWithDebInfo\\1749sx41\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=D:\\Folder Kuliah Cadangan\\Sems 6\\PPL\\ocr-android\\opencv\\build\\intermediates\\cxx\\RelWithDebInfo\\1749sx41\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=RelWithDebInfo" ^
  "-BD:\\Folder Kuliah Cadangan\\Sems 6\\PPL\\ocr-android\\opencv\\.cxx\\RelWithDebInfo\\1749sx41\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
