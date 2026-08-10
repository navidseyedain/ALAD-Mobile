# Fix Gradle Build Errors

Revert the incorrect Android Gradle Plugin (AGP) version and add missing Jetpack Compose ViewModel dependencies to resolve compilation errors.

## Proposed Changes

### Build Configuration

#### [MODIFY] [root build.gradle.kts](file:///E:/AI/alad-mobile/build.gradle.kts)
- Revert AGP version from `8.13.2` to `8.2.2` to ensure compatibility with Gradle.

#### [MODIFY] [app build.gradle.kts](file:///E:/AI/alad-mobile/app/build.gradle.kts)
- Add `androidx.lifecycle:lifecycle-viewmodel-compose` dependency to resolve the `Unresolved reference: viewModel` error in `MainActivity.kt`.

## Verification Plan

### Automated Tests
- Run Gradle Sync to verify build configuration is valid.
- Run `app:assembleDebug` to ensure successful compilation.
