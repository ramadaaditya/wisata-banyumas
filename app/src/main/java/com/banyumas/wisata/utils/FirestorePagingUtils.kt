//package com.banyumas.wisata.utils
//
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.banyumas.wisata.data.model.Destination
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.QuerySnapshot
//import kotlinx.coroutines.tasks.await
//
//class FirestorePagingUtils(
//    private val db: FirebaseFirestore,
//    private val collection: String,
//    private val pageSize: Long = 20
//) : PagingSource<QuerySnapshot, Destination>() {
//
//    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Destination> {
//        return try {
//            val query = db.collection(collection)
//                .orderBy("id")
//                .limit(pageSize)
//
//            val currentPage = params.key ?: query.get().await()
//            val lastDocument = currentPage.documents.lastOrNull()
//
//            val nextQuery = lastDocument?.let {
//                query.startAfter(it).get().await()
//            }
//
////            // Convert Firestore documents to Destination domain model
////            val destinations = currentPage.toObjects(Destination::class.java)
////                .mapNotNull { it.toDestination() } // Use your mapper function to convert
//
//            LoadResult.Page(
//                data = currentPage.toObjects(Destination::class.java),
//                prevKey = null,
//                nextKey = nextQuery
//            )
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<QuerySnapshot, Destination>): QuerySnapshot? {
//        return null
//    }
//}