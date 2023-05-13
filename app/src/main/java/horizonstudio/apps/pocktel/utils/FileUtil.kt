package horizonstudio.apps.pocktel.utils

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.net.URL
import java.nio.channels.Channels
import java.security.MessageDigest

class FileUtil {
    companion object {
        fun saveTempFile(uri: Uri, name: String, context: Context): File {
            context.contentResolver.openInputStream(uri).use {
                val file: File = File.createTempFile(name, null, context.filesDir)
                file.outputStream().use { outputStream ->
                    it?.copyTo(outputStream)
                }
                return file
            }
        }

        fun downloadFile(context: Context, url: URL): File {
            val fileName = File(url.file).name
            val outputFileName = context.filesDir.absolutePath + "/" + fileName

            url.openStream().use { inputStream ->
                Channels.newChannel(inputStream).use { rbc ->
                    FileOutputStream(outputFileName).use { outputStream ->
                        outputStream.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
                    }
                }
            }
            return File(outputFileName)
        }

        fun getFileName(uri: Uri, context: Context): String {
            val contentResolver = context.contentResolver
            val cursor = contentResolver?.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    return it.getString(columnIndex)
                }
            }
            throw NotFoundException("Could not find resource")
        }

        fun sha256(file: File): String = hash(file, MessageDigest.getInstance("SHA-256"))

        private fun hash(file: File, algorithm: MessageDigest): String {
            val hash = algorithm.digest(file.readBytes())
            return BigInteger(1, hash).toString(16)
        }

        fun createMultipartFile(file: File, name: String): MultipartBody.Part {
            return MultipartBody.Part.createFormData(
                name, file.name, RequestBody.create(
                    null, file
                )
            )
        }
    }
}