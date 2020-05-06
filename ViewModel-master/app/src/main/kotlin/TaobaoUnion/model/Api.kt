package TaobaoUnion.model

import TaobaoUnion.model.domain.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface Api {

    @GET("discovery/categories")
    fun getCategories(): Call<Categories>

    @GET
    fun getContentListByMaterialId(@Url url: String): Call<HomePagerContent>

    @POST("tpwd")
    fun getTicketByUrl(@Body body: TicketRequestItem): Call<TicketResult>

    @GET("recommend/categories")
    fun getSelectedPageCategories(): Call<SelectedPageCategory>

    @GET
    fun getSelectedPageContent(@Url url: String): Call<SelectedContent>

    @GET
    fun getOnSellContent(@Url url: String): Call<OnSellContent>
}