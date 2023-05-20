package horizonstudio.apps.pocktel.dal

import horizonstudio.apps.pocktel.configurations.Constants.RULE_SETS_SEED
import horizonstudio.apps.pocktel.dal.entities.RuleSet
import horizonstudio.apps.pocktel.dal.repositories.RuleSetRepository

class DatabaseSeeder(private val db: PocktelDatabase) {
    fun seedDatabase() {
        RULE_SETS_SEED.forEach { rs -> db.ruleSetRepository().insert(rs) }
    }
}