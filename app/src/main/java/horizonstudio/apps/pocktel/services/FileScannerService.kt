package horizonstudio.apps.pocktel.services

import horizonstudio.apps.pocktel.contracts.incoming.ScanResourceContract
import horizonstudio.apps.pocktel.contracts.incoming.ScanResourceStatusContract
import horizonstudio.apps.pocktel.contracts.incoming.ScanResultContract
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface FileScannerService {
    @Multipart
    @POST("/scan")
    suspend fun scanWithArchivedRulesSet(@Part sample: MultipartBody.Part, @Part rules: MultipartBody.Part): Response<ScanResourceContract>

    @Multipart
    @POST("/scan")
    suspend fun scanWithSingleRule(@Part sample: MultipartBody.Part, @Part rule: MultipartBody.Part): Response<ScanResourceContract>

    @GET("{scanRequestResource}")
    suspend fun scanStatus(@Path("scanRequestResource") scanRequestResource: String): Response<ScanResourceStatusContract>

    @GET("{scanResultResource}")
    suspend fun scanResult(@Path("scanResultResource") scanResultResource: String): Response<ScanResultContract>
}