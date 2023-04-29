package horizonstudio.apps.pocktel.contracts.incoming

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class YARAMatch(val meta: YARARuleMeta, val rule: String, val strings: Array<String>) :
    Parcelable