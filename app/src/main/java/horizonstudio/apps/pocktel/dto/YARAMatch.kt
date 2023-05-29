package horizonstudio.apps.pocktel.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class YARAMatch(val meta: YARARuleMeta, val rule: String, val strings: Array<String>) :
    Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as YARAMatch

        if (meta != other.meta) return false
        if (rule != other.rule) return false
        if (!strings.contentEquals(other.strings)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = meta.hashCode()
        result = 31 * result + rule.hashCode()
        result = 31 * result + strings.contentHashCode()
        return result
    }
}