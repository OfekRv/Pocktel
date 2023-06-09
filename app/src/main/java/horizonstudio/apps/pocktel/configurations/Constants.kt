package horizonstudio.apps.pocktel.configurations

import horizonstudio.apps.pocktel.dal.entities.RuleSet

object Constants {
    const val DATABASE_NAME:String = "pocktel"
    const val BASE_URL:String = "https://yarapi-a2zphhdpma-uc.a.run.app"
    const val MAX_TRIES: Int = 200
    const val TIME_INTERVAL: Long = 1600
    const val FILENAME_ARGUMENT_NAME: String = "filename"
    const val HASH_ARGUMENT_NAME: String = "hash"
    const val RESULT_ARGUMENT_NAME: String = "result"
    const val ARCHIVED_FILES_PATTERN: String = "application/zip"
    const val ALL_FILES_PATTERN: String = "*/*"
    const val SAMPLE_PARAMETER_NAME: String = "sample"
    const val RULES_PARAMETER_NAME: String = "rules"
    const val HASH_COPY_CLIPBOARD_LABEL: String = "hash"
    const val NOT_YET_ASSIGNED_ID: Long = 0
    const val TEXT_PLAIN_RESPONSE_MEDIA_TYPE = "text/plain"
    const val VT_HASH_URL = "https://www.virustotal.com/gui/file/"
    const val VT_HASH_SUMMARY_ROUTE = "summary"

    val RULE_SETS_SEED = listOf(
        RuleSet(NOT_YET_ASSIGNED_ID, "Yara-Rules Repo", null, "https://github.com/OfekRv/rules/archive/refs/heads/master.zip")
    )
}