package horizonstudio.apps.pocktel.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScanResultContract(val matches: Array<YARAMatch>) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScanResultContract

        if (!matches.contentEquals(other.matches)) return false

        return true
    }

    override fun hashCode(): Int {
        return matches.contentHashCode()
    }
}