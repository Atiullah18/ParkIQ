package data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parking_spaces")
data class ParkingSpaceEntity(
    @PrimaryKey val id: String,
    val parkingName: String,
    val location: String,
    val rating: String,
    val availableSlots: String,
    val availableHours: String,
    val hourlyRent: String,
    val parkingType: String,
    val description: String,
    val imageUri: String,
    val drawableResName: String,
    val hasPhoto: Boolean,
    val updatedAt: Long
)
