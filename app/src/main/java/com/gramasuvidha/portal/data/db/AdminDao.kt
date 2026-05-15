package com.gramasuvidha.portal.data.db

import androidx.room.*
import com.gramasuvidha.portal.data.entity.AdminEntity

@Dao
@JvmSuppressWildcards
interface AdminDao {
    @Query("SELECT * FROM admins WHERE email = :email LIMIT 1")
    suspend fun getAdminByEmail(email: String): AdminEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: AdminEntity): Long
}
