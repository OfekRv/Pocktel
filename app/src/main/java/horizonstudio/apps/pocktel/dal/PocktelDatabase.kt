package horizonstudio.apps.pocktel.dal

import androidx.room.Database
import androidx.room.RoomDatabase
import horizonstudio.apps.pocktel.dal.entities.RuleSet
import horizonstudio.apps.pocktel.dal.repositories.RuleSetRepository

@Database(entities = [RuleSet::class], version = 1)
abstract class PocktelDatabase : RoomDatabase() {
    abstract fun ruleSetRepository(): RuleSetRepository
}