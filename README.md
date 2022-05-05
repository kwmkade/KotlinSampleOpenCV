# KotlinSampleOpenCV

## OpenCVイントール手順
1. Moduleのインポート
```
Import module: Menu -> "File" -> "New" -> "Module" -> "Import Gradle project":
Source directory: select this "sdk" directory
Module name: ":opencv"
```
2. dependencyの設定(build.gradle)
```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    ...
    implementation project(':opencv')
}
```
3. opencv/java/AndroidManifest.xml にカメラの権限を追加
    - Camera2Rendererでエラーが発生するので
    - `<uses-permission android:name="android.permission.CAMERA" />` を追加
4. opencv/build.gradle の compileSdkVersion minSdkVersion targetSdkVersion をappのほうと合わせる
    - lintでエラーになるので
