package com.banyumas.wisata.model.api

import com.google.gson.annotations.SerializedName

data class SearchResponse(

	@field:SerializedName("candidates")
	val candidates: List<CandidatesItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class CandidatesItem(

	@field:SerializedName("formatted_address")
	val formattedAddress: String? = null,

	@field:SerializedName("business_status")
	val businessStatus: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("place_id")
	val placeId: String? = null
)
