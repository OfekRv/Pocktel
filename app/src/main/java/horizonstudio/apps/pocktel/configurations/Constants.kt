package horizonstudio.apps.pocktel.configurations

import horizonstudio.apps.pocktel.dal.entities.RuleSet

object Constants {
    const val DATABASE_NAME:String = "pocktel"
    const val BASE_URL:String = "https://yarapi-a2zphhdpma-uc.a.run.app"
    const val MAX_TRIES: Int = 20
    const val TIME_INTERVAL: Long = 500
    const val HASH_ARGUMENT_NAME: String = "hash"
    const val RESULT_ARGUMENT_NAME: String = "result"
    const val ARCHIVED_FILES_PATTERN: String = "application/zip"
    const val ALL_FILES_PATTERN: String = "*/*"
    const val SAMPLE_PARAMETER_NAME: String = "sample"
    const val RULES_PARAMETER_NAME: String = "rules"
    const val HASH_COPY_CLIPBOARD_LABEL: String = "hash"
    const val NOT_YET_ASSIGNED_ID: Long = 0
    const val TEXT_PLAIN_RESPONSE_MEDIA_TYPE = "text/plain"
    const val NAME_PARAM = "name"
    const val URL_PARAM = "url"
    const val ERROR_DIALOG_TITLE = "Error"

    // TODO: find or create good ones
    val RULE_SETS_SEED = listOf(
        RuleSet(NOT_YET_ASSIGNED_ID, "TEST", null, "URL")
    )
}