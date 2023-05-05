package horizonstudio.apps.pocktel.bl

import horizonstudio.apps.pocktel.dal.entities.RuleSet
import horizonstudio.apps.pocktel.dal.repositories.RuleSetRepository

class RuleSetBl(private val repository: RuleSetRepository) {
    fun findAll(): Collection<RuleSet> {
        return repository.findAll()
    }

    fun save(ruleSet: RuleSet): Long {
        return repository.insert(ruleSet)
    }

    fun delete(ruleSet: RuleSet): Boolean {
        return repository.delete(ruleSet) > 0
    }
}