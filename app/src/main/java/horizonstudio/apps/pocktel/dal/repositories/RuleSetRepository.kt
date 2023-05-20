package horizonstudio.apps.pocktel.dal.repositories

import androidx.room.*
import horizonstudio.apps.pocktel.dal.entities.RuleSet

@Dao
interface RuleSetRepository {
    @Query("SELECT * FROM ruleSet")
    fun findAll(): List<RuleSet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ruleSet: RuleSet): Long

    @Delete
    fun delete(ruleSet: RuleSet): Int
}