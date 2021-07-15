package com.nytimes.android.pagingissue

import androidx.paging.PagingSource
import androidx.paging.PagingState

class StoryPagingSource(val dao: StoryDao) : PagingSource<Int, Story>() {
    override fun getRefreshKey(state: PagingState<Int, Story>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        val key = params.key ?: 0
        val nextKey = key + params.loadSize
        return LoadResult.Page(
            data = dao.selectAllStoriesByPage(key, 10),
            prevKey = null,
            nextKey = nextKey
        )
    }
}