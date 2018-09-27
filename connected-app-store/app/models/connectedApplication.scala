package models

import java.util.UUID

case class connectedApplication(name:String, appCategory: UUID, description: String, rating: Int, size: Double, image: String, longDescription: String, cost: Double)
