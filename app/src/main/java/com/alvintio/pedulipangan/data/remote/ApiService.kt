package com.alvintio.pedulipangan.data.remote

import com.alvintio.pedulipangan.model.Food
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("/getproducts")
    fun getProducts(): Call<List<Food>>
}