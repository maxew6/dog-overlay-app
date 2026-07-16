# Dog Overlay

A tiny single-Activity Jetpack Compose app: a dog sits on screen, and three
buttons — **Walk**, **Sit**, **Bark** — switch its animation. Bark plays a
sound once, then the dog returns to Sit automatically. No networking, no
login, no database, no ads/analytics, no "draw over other apps" permission —
everything renders inside the app's own full-screen scene.

## Stack

- Kotlin, single `MainActivity`, Jetpack Compose UI
- `minSdk 24`, `compileSdk`/`targetSdk 34`
- Animation: a small `Canvas`-based procedural renderer (`DogCanvas.kt`) —
  no Lottie/image libraries, smallest possible footprint
- Bark sound: `MediaPlayer` playing `app/src/main/res/raw/bark.mp3`

## Project layout

```
app/src/main/java/com/example/dogoverlay/
  MainActivity.kt          entry point, owns the bark MediaPlayer
  DogState.kt               enum SIT / WALK / BARK
  ui/DogOverlayScreen.kt    background scene + button row + state machine
  ui/DogCanvas.kt           the animated dog itself
  audio/BarkPlayer.kt       MediaPlayer wrapper for the one-shot bark
app/src/main/res/raw/bark.mp3
```

## Run locally

1. Open the project root in Android Studio (Koala/Ladybug or newer) and let
   it sync — Android Studio auto-repairs the Gradle wrapper jar on first
   sync, so you don't need to do anything else.
2. Press Run ▶ on a device/emulator running API 24+.

**Command line**, if you already have Gradle or Android Studio installed
somewhere on the machine (needed once, to generate the wrapper jar — see
"About the Gradle wrapper" below):

```bash
gradle wrapper --gradle-version 8.7   # one-time, only if gradlew fails
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### About the Gradle wrapper

This repo does **not** commit the binary `gradle-wrapper.jar` (only
`gradlew`, `gradlew.bat`, and `gradle-wrapper.properties`, which are plain
text). GitHub Actions regenerates the jar on every run (see workflow below).
Android Studio does the same automatically on project sync. If you want to
run `./gradlew` from a bare terminal with no Android Studio installed, run
`gradle wrapper --gradle-version 8.7` once first (requires *some* Gradle
install, or use the `gradle` binary bundled with Android Studio, e.g.
`~/Library/Application Support/Google/AndroidStudio*/gradle/gradle-*/bin/gradle`
on macOS).

## Replacing the placeholder dog art

The dog is currently drawn with plain shapes in `ui/DogCanvas.kt`
(`Canvas.drawOval/drawRoundRect/drawCircle/drawPath` calls) so the project
has zero missing assets and builds immediately. To use your real dog:

**Option A — quickest, keep the current architecture**
Open `DogCanvas.kt` and tweak the color constants at the top (`Coat`,
`CoatDark`, `Ear`, `Belly`, `Nose`) to match your reference photo, and adjust
the proportions (the `* 0.xx` multipliers) to match its shape. No new
dependencies needed.

**Option B — sprite sheet (recommended for real artwork)**
1. Export your dog reference as three horizontal sprite sheets:
   `dog_walk.png`, `dog_sit.png`, `dog_bark.png` (equal-size frames in a row)
   into `app/src/main/res/drawable/`.
2. Replace the body of `DogCanvas` with a frame-index animator: track an
   `Int` frame index driven by a `LaunchedEffect` + `delay(frameDurationMs)`
   loop (or `rememberInfiniteTransition` on an `Int`), and draw the current
   frame with `Image(bitmap = sheet, ...)` cropped to that frame's rect via
   `Modifier` + `BitmapPainter`/`ImageBitmap.asAndroidBitmap()` sub-region,
   or simply pre-slice the sheet into a `List<ImageBitmap>` once at load time
   and draw the current one with `drawImage(...)` in the `Canvas`.
3. Delete the shape-drawing code once the sprite version looks right.

**Option C — Lottie**
Add `com.airbnb.android:lottie-compose` (single new dependency), drop a
`dog.json` export from After Effects/Figma into `res/raw/`, and replace
`DogCanvas` with `LottieAnimation(composition, progress = ...)`, switching
the loaded composition/asset name per `DogState`. Left out by default to
keep the dependency list at zero beyond Compose itself.

## Replacing the bark sound

`app/src/main/res/raw/bark.mp3` is a synthesized placeholder tone (not a
real bark) generated with `ffmpeg`, just so the app has *something* audible
out of the box. Drop your own short (under ~1s) `bark.mp3` in the same path,
same filename — no code changes needed.

## GitHub Actions

`.github/workflows/android.yml` on every push/PR to `main` (and manually via
"Run workflow"):

1. Checks out the repo
2. Sets up JDK 17 (Temurin)
3. Installs Gradle 8.7 via `gradle/actions/setup-gradle` (this also gives
   dependency-cache reuse between runs)
4. Runs `gradle wrapper --gradle-version 8.7` to materialize
   `gradle-wrapper.jar` (see "About the Gradle wrapper" above)
5. Runs `./gradlew assembleDebug`
6. Uploads `app-debug.apk` as a build artifact ("dog-overlay-debug-apk"),
   downloadable from the workflow run's Summary page

No signing config is set up (debug build only, as requested) and no secrets
are required — the workflow runs on a clean checkout with no manual steps.

## Notes

- No image of a real dog was attached to the request that produced this
  repo, so `DogCanvas.kt` ships with a generic brown/tan cartoon dog as a
  clearly-marked placeholder. See "Replacing the placeholder dog art" above.
- `app/src/main/res/mipmap-*/ic_launcher*.png` are likewise simple generated
  placeholder launcher icons (see `tools_gen_icons.py`, a one-off script not
  part of the app — safe to delete once you have real icons).
