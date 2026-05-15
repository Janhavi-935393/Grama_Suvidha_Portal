package com.gramasuvidha.portal.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admins")
data class AdminEntity(
    @PrimaryKey(autoGenerate = true)
    val adminId: Int = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val role: String = "Admin"
)
