package horizonstudio.apps.pocktel.utils

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class DatabaseUtil {
    companion object {
        inline fun <reified E : RoomDatabase> buildDatabase(context: Context, dbName: String): E {
            return Room.databaseBuilder(context.applicationContext, E::class.java, dbName).build()
        }
    }
}