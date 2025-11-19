package com.droidnotes.feature.news.navigation

object NewsRoutes {
    const val FEED = "feed"
    const val SEARCH = "search"
    const val ARTICLE_DETAIL = "article/{articleId}"
    const val BOOKMARKS = "bookmarks"

    fun articleDetail(articleId: String) = "article/$articleId"
}
