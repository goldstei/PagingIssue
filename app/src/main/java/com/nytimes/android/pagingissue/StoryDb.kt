package com.nytimes.android.pagingissue

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [(Story::class)], version = 1, exportSchema = true)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao

    companion object {
        @Volatile
        private var instance: StoryDatabase? = null

        fun getInstance(context: Context): StoryDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): StoryDatabase {
            return Room.databaseBuilder(context, StoryDatabase::class.java, "stories")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            instance?.let { instance ->
                                for (i in 1..100) {
                                    instance.storyDao().insertStory(
                                        Story(
                                            url = "http://www.nytimes.com/$i",
                                            headline = "headline$i",
                                            summary = "summary$i"
                                        )
                                    )
                                }
                            }
                        }
                    }
                }).build()
        }
    }
}

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories LIMIT :pageSize OFFSET :offset")
    suspend fun selectAllStoriesByPage(offset: Int, pageSize: Int): List<Story>

    @Insert
    fun insertStory(story: Story)
}

@Entity(tableName = "stories")
data class Story(
    @PrimaryKey val url: String,
    val headline: String,
    val summary: String,
)
