package TaobaoUnion.model.domain

import com.adgvcxz.IModel

data class HomePagerContent(
        val code: Int,
        val `data`: List<Data>,
        val message: String,
        val success: Boolean
)

data class Data(
        val category_id: Int,
        val category_name: Any,
        val click_url: String,
        val commission_rate: String,
        val coupon_amount: Int,
        val coupon_click_url: String,
        val coupon_end_time: String,
        val coupon_info: Any,
        val coupon_remain_count: Int,
        val coupon_share_url: String,
        val coupon_start_fee: String,
        val coupon_start_time: String,
        val coupon_total_count: Int,
        val item_description: String,
        val item_id: Long,
        val level_one_category_id: Int,
        val level_one_category_name: String,
        val nick: String,
        val pict_url: String,
        val seller_id: Long,
        val shop_title: String,
        val small_images: SmallImages,
        val title: String,
        val user_type: Int,
        val volume: Int,
        val zk_final_price: String
):IModel

data class SmallImages(val string: List<String>):IModel

