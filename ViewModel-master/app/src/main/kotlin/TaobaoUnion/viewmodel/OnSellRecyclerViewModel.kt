@file:Suppress("UNUSED_CHANGED_VALUE")

package TaobaoUnion.viewmodel

import TaobaoUnion.model.domain.MapData
import TaobaoUnion.model.domain.OnSellContent
import TaobaoUnion.utils.UrlUtils
import TaobaoUnion.utils.api
import android.annotation.SuppressLint
import android.graphics.Paint

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.adgvcxz.IModel
import com.adgvcxz.IMutation
import com.adgvcxz.add
import com.adgvcxz.recyclerviewmodel.*
import com.adgvcxz.toBind
import com.adgvcxz.viewmodel.sample.R
import com.bumptech.glide.Glide
import io.reactivex.Observable

/**
 * time:2020.5.5
 * author:LihC
 */
class OnSellRecyclerViewModel() : RecyclerViewModel() {
    override val initModel: RecyclerModel = RecyclerModel(null, hasLoadingItem = true, isAnim = true)
    //请求数据
    override fun request(refresh: Boolean): Observable<IMutation> {
        return getOnSellContent(refresh).map {
            if (refresh){
                SetData(it.data.tbk_dg_optimus_material_response.result_list.map_data.map {
                    OnSellRecyclerTextItemViewModel(it)
                })
            }else{
                AppendData(it.data.tbk_dg_optimus_material_response.result_list.map_data.map {
                    OnSellRecyclerTextItemViewModel(it)
                })
            }
        }
    }
}

class OnSellRecyclerTextItemView : IView<OnSellRecyclerTextItemView.TextItemViewHolder, OnSellRecyclerTextItemViewModel> {

    override val layoutId: Int = R.layout.item_on_sell_content
    override fun initView(view: View, parent: ViewGroup): TextItemViewHolder = TextItemViewHolder(view)
    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: TextItemViewHolder, viewModel: OnSellRecyclerTextItemViewModel, position: Int) {
        viewModel.toBind(viewHolder.disposables) {
            add({ cover }, { Glide.with(viewHolder.coverImageView).load(this).into(viewHolder.coverImageView) })
            add({ title }, { viewHolder.contentTitleTv.text = this })
            add({ originalPrice }, {
                viewHolder.originalPriceTv.text = "¥${this}元"
                viewHolder.originalPriceTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG //设置下滑线
            })
            add({ offPrice }, { viewHolder.offPriceTv.text =String.format("券后价%.2f", this) })
        }
    }

    class TextItemViewHolder(view: View) : ItemViewHolder(view) {
        val coverImageView: ImageView = view.findViewById(R.id.on_sell_cover)
        val contentTitleTv: TextView = view.findViewById(R.id.on_sell_content_title_tv)
        val originalPriceTv: TextView = view.findViewById(R.id.on_sell_original_price_tv)
        val offPriceTv: TextView = view.findViewById(R.id.on_sell_off_price_tv)
    }


}

class OnSellRecyclerTextItemViewModel(private val model_: MapData) : RecyclerItemViewModel<OnSellRecyclerTextItemViewModel.OnSellContentModel>() {

    private val originalPrice = model_.zk_final_price
    private val couponAmount = model_.coupon_amount
    private val offPrice = originalPrice.toFloat() - couponAmount
    private val coverUrl = UrlUtils.getCoverPath(model_.pict_url)

    override val initModel: OnSellContentModel = OnSellContentModel(coverUrl, model_.title,model_.coupon_click_url ,model_.zk_final_price, offPrice)

    class OnSellContentModel(var cover: String, var title: String, var couponClickUrl:String,
                             var originalPrice: String, var offPrice: Float) : IModel {

    }
}

//页码增加，内容增加
fun getOnSellContent(refresh: Boolean): Observable<OnSellContent> {
    var defaultPage = 1
    if (refresh) {
        defaultPage = 1
    } else {
        defaultPage++
    }
    val targetUrl = UrlUtils.getOnSellUrl(defaultPage)
    val task = api.getOnSellContent(targetUrl)
    return task.toRx()
}
