package com.unicofrance.uniexo.data.remote

import com.unicofrance.uniexo.data.local.database.entities.Container

class ApiService(
    private val api: Api
) {
    suspend fun getContainers(): Result<List<Container>> {
        return api.getContainers()
    }

    suspend fun getContainerById(id: String): Result<Container> {
        return api.getContainerById(id)
    }
}