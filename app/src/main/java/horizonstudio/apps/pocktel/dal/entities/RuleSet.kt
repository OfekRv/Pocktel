package horizonstudio.apps.pocktel.dal.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RuleSet(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val path: String?,
    val url: String?
)