package horizonstudio.apps.pocktel.contracts.incoming

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScanResultContract(val matches: Array<YARAMatch>) : Parcelable