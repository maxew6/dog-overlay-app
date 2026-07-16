package com.example.dogoverlay

/**
 * The three states the dog overlay can be in. BARK is a one-shot state:
 * the caller is responsible for switching back to SIT once playback ends
 * (see [com.example.dogoverlay.audio.BarkPlayer] and MainActivity).
 */
enum class DogState {
    SIT,
    WALK,
    BARK
}
