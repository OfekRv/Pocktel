package horizonstudio.apps.pocktel.utils

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class DatabaseUtil {
    companion object {
        inline fun <reified D : RoomDatabase> buildDatabase(context: Context, dbName: String): D {
            return Room.databaseBuilder(context, D::class.java, dbName)
                .allowMainThreadQueries().build()
        }
    }
}