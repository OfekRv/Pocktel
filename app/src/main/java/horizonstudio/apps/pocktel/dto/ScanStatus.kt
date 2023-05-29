package horizonstudio.apps.pocktel.dto

import com.squareup.moshi.Json

enum class ScanStatus {
    @Json(name="Pending")
    PENDING,
    @Json(name="Completed")
    COMPLETED,
    @Json(name="Error")
    ERROR
}