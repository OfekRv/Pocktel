package horizonstudio.apps.pocktel.bl.scanners

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import horizonstudio.apps.pocktel.configurations.Constants.BASE_URL
import horizonstudio.apps.pocktel.configurations.Constants.MAX_TRIES
import horizonstudio.apps.pocktel.configurations.Constants.RULES_PARAMETER_NAME
import horizonstudio.apps.pocktel.configurations.Constants.SAMPLE_PARAMETER_NAME
import horizonstudio.apps.pocktel.configurations.Constants.TIME_INTERVAL
import horizonstudio.apps.pocktel.contracts.incoming.ScanResourceStatusContract
import horizonstudio.apps.pocktel.contracts.incoming.ScanResultContract
import horizonstudio.apps.pocktel.contracts.incoming.ScanStatus
import horizonstudio.apps.pocktel.exceptions.PocktelNetworkException
import horizonstudio.apps.pocktel.exceptions.PocktelYARARuleFileException
import horizonstudio.apps.pocktel.services.FileScannerService
import horizonstudio.apps.pocktel.utils.FileUtil.Companion.createMultipartFile
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
        val scanResource = scannerClient.scanWithArchivedRulesSet(
            createMultipartFile(sample, SAMPLE_PARAMETER_NAME), createMultipartFile(rules, RULES_PARAMETER_NAME)
        ).body() ?: throw PocktelNetworkException("Could not get server response")

        var scanRequestResource: ScanResourceStatusContract
        var tries = 0
        do {
            Thread.sleep(TIME_INTERVAL)
            scanRequestResource = scannerClient.scanStatus(scanResource.location).body()!!
        } while (scanRequestResource.status == ScanStatus.PENDING && ++tries <= MAX_TRIES)

        if (scanRequestResource.status == ScanStatus.ERROR) {
            throw PocktelYARARuleFileException("Error with submitted YARA rule")
        }
        if (scanRequestResource.status == ScanStatus.PENDING) {
            throw PocktelNetworkException("Unexpected error with the server, please try again")
        }
        return scannerClient.scanResult(scanRequestResource.result!!).body()!!
    }
}