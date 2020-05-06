package TaobaoUnion.utils

import TaobaoUnion.model.Api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//class RetrofitManager private constructor() {
//     var retrofit: Retrofit? = null
//
//
//    companion object {
//        const val BASE_URL = "https://www.sunofbeach.net/shop/api/"
//        var instance: Retrofit? = null
//            get() {
//                if (instance == null) {
//                    instance =
//                    field = instance
//                }
//                return field
//            }
//
//        fun get(): RetrofitManager = instance!!
//    }
//
//    fun getApi(): Api? = retrofit?.create(Api::class.java)
//}

val api = Retrofit.Builder()
        .baseUrl("https://www.sunofbeach.net/shop/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Api::class.java)