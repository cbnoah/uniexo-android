package com.unicofrance.uniexo.data.remote

import com.unicofrance.uniexo.data.local.database.entities.Container
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    // Need to be implemented later on to the API to get the containers
    // (and then update the .csv file / room db)
    @GET("containers")
    suspend fun getContainers(): Result<List<Container>>

    @GET("containers/{id}")
    suspend fun getContainerById(@Path("id") id: String): Result<Container>
}
