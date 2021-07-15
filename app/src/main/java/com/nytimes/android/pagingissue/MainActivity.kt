package com.nytimes.android.pagingissue

import android.os.Bundle
import android.text.format.DateUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomMasterTable
import com.nytimes.android.pagingissue.ui.theme.PagingIssueTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val storyFlow: Flow<PagingData<Story>> = Pager(PagingConfig(pageSize = 10)) {
            StoryPagingSource(StoryDatabase.getInstance(this).storyDao())
        }.flow

        setContent {
            PagingIssueTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        LazyList(storyFlow)

                    }
                }
            }
        }
    }
}

@Composable
fun LazyList(storyFlow: Flow<PagingData<Story>>) {
    val lazyItems = storyFlow.collectAsLazyPagingItems()
    LazyColumn() {
        itemsIndexed(lazyItems) { index, story ->
            if (index > 0) {
                Divider(
                    color = Color(0xFFE2E2E2),
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }
            story?.let {
                    Text(text = story.headline, fontSize = 16.sp)
                    Text(text = story.summary, fontSize = 10.sp)
            }
        }

    }
}
