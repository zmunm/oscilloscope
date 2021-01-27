package com.zmunm.oscilloscope

import android.app.Activity
import com.zmunm.generated.validate

class SampleActivity: Activity() {
    @OscilloscopeEvent
    val event: Boolean = true.validate()
}