package horizonstudio.apps.pocktel.dal.repositories

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import horizonstudio.apps.pocktel.dal.entities.RuleSet

@Dao
interface RuleSetRepository {
    @Query("SELECT * FROM ruleSet")
    fun findAll(): List<RuleSet>

    @Insert
    fun insert(ruleSet: RuleSet): Long

    @Delete
    fun delete(ruleSet: RuleSet): Int
}