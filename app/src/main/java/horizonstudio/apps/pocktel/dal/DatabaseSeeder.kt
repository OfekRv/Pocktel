package horizonstudio.apps.pocktel.dal

import horizonstudio.apps.pocktel.configurations.Constants.RULE_SETS_SEED

class DatabaseSeeder(private val db: PocktelDatabase) {
    fun seedDatabase() {
        if (db.ruleSetRepository().findAll().isEmpty()) {
            RULE_SETS_SEED.forEach { rs -> db.ruleSetRepository().insert(rs) }
        }
    }
}