package horizonstudio.apps.pocktel.bl.scanners

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import horizonstudio.apps.pocktel.configurations.Constants.BASE_URL
import horizonstudio.apps.pocktel.configurations.Constants.MAX_TRIES
import horizonstudio.apps.pocktel.configurations.Constants.RULES_PARAMETER_NAME
import horizonstudio.apps.pocktel.configurations.Constants.SAMPLE_PARAMETER_NAME
import horizonstudio.apps.pocktel.configurations.Constants.TEXT_PLAIN_RESPONSE_MEDIA_TYPE
import horizonstudio.apps.pocktel.configurations.Constants.TIME_INTERVAL
import horizonstudio.apps.pocktel.contracts.incoming.ScanResourceContract
import horizonstudio.apps.pocktel.contracts.incoming.ScanResourceStatusContract
import horizonstudio.apps.pocktel.contracts.incoming.ScanResultContract
import horizonstudio.apps.pocktel.contracts.incoming.ScanStatus
import horizonstudio.apps.pocktel.exceptions.PocktelNetworkException
import horizonstudio.apps.pocktel.exceptions.PocktelYARARuleFileException
import horizonstudio.apps.pocktel.services.FileScannerService
import horizonstudio.apps.pocktel.utils.FileUtil.Companion.createMultipartFile
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

class FileScanner {
    private val moshi by lazy {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    private val scannerClient by lazy {
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
            .create(FileScannerService::class.java)
    }

    suspend fun scan(sample: File, rules: File): ScanResultContract {
        val scanRequestResource = scannerClient.scanWithArchivedRulesSet(
            createMultipartFile(sample, SAMPLE_PARAMETER_NAME),
            createMultipartFile(rules, RULES_PARAMETER_NAME)
        )

        if (!scanRequestResource.isSuccessful) {
            handleUnsuccessfulRequest(scanRequestResource)
        }

        val scanResultResourceReference: String =
            getScanResourceReference(scanRequestResource.body()!!.location)
        return scannerClient.scanResult(scanResultResourceReference).body()!!
    }


    private fun handleUnsuccessfulRequest(scanResource: Response<ScanResourceContract>) {
        scanResource.errorBody()?.let { serverError ->
                {
                    if (serverError.contentType()!!.equals(TEXT_PLAIN_RESPONSE_MEDIA_TYPE)) {
                        throw PocktelYARARuleFileException(serverError.string())
                    }
                }
            }
        throw PocktelNetworkException("Could not get server response")
    }

    private suspend fun getScanResourceReference(scanResourceLocation: String): String {
        var scanResultResource: ScanResourceStatusContract
        var tries = 0
        do {
            Thread.sleep(TIME_INTERVAL)
            scanResultResource = scannerClient.scanStatus(scanResourceLocation).body()!!
        } while (scanResultResource.status == ScanStatus.PENDING && ++tries <= MAX_TRIES)

        if (scanResultResource.status != ScanStatus.COMPLETED) {
            throw PocktelNetworkException("Unexpected error with the server, please try again")
        }
        return scanResultResource.result!!
    }
}