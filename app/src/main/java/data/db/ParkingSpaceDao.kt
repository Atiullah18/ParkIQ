package data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingSpaceDao {
    @Query("SELECT * FROM parking_spaces ORDER BY updatedAt DESC")
    fun getAllParkingSpaces(): Flow<List<ParkingSpaceEntity>>

    @Query("SELECT * FROM parking_spaces WHERE id = :id")
    suspend fun getParkingSpaceById(id: String): ParkingSpaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(parkingSpace: ParkingSpaceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(parkingSpaces: List<ParkingSpaceEntity>)
}
