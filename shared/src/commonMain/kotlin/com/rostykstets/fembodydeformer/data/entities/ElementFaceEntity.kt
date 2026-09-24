package com.rostykstets.fembodydeformer.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class ElementFaceEntity(val elementId: Int, val faceIndex: Int)