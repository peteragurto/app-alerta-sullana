package com.example.alertasullana.core

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    fun getRetrofit():Retrofit {
        //Funcion ejemplo = no sirve para nada
        return Retrofit.Builder()
            .baseUrl("googlemaps.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}