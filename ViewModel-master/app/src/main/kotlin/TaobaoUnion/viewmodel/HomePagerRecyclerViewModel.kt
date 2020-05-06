package TaobaoUnion.viewmodel

import TaobaoUnion.model.domain.Data
import TaobaoUnion.model.domain.HomePagerContent
import TaobaoUnion.utils.UrlUtils
import TaobaoUnion.utils.api
import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.adgvcxz.*
import com.adgvcxz.recyclerviewmodel.*
import com.adgvcxz.viewmodel.sample.R
import com.adgvcxz.viewmodel.sample.RxBus
import com.bumptech.glide.Glide
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_loading.view.*
import retrofit2.Call

/**
 * time:2020.4.29
 * author:LihC
 */

var initId = 0

class HomePagerRecyclerViewModel(val id: Int) : RecyclerViewModel() {

    override val initModel: RecyclerModel = RecyclerModel(null, hasLoadingItem = true, isAnim = true)
    //请求数据
    override fun request(refresh: Boolean): Observable<IMutation> {

        return getCategoryByContentId(id, refresh).map {
            if (refresh)
                SetData(it.data.map {
                    TextItemViewModel(it)
                }) else AppendData(it.data.map {
                TextItemViewModel(it)
            })
        }
    }
}

class TextItemView : IView<TextItemView.TextItemViewHolder, TextItemViewModel> {

    override val layoutId: Int = R.layout.item_home_pager_content

    override fun initView(view: View, parent: ViewGroup): TextItemViewHolder = TextItemViewHolder(view)

    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: TextItemViewHolder, viewModel: TextItemViewModel, position: Int) {
        viewModel.toBind(viewHolder.disposables) {
            add({ goodsTitle }, { viewHolder.goodsTitle.text = this })
            add({ goodsOffPrice }, { viewHolder.goodsOffPriceTv.text = "省${this}元" })
            add({ goodsFinalPrice }, { viewHolder.goodsFinalPriceTv.text = String.format("¥%.2f", this) })
            add({ goodsOriginalPrice }, { viewHolder.goodsOriginalPriceTv.text = "¥ ${this}"
                viewHolder.goodsOriginalPriceTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            })
            add({ goodsSellCount }, { viewHolder.goodsSellCountTv.text = "${this}人已经购买" })
            add({ goodsCover }, { Glide.with(viewHolder.coverImageView).load(this).into(viewHolder.coverImageView) })
        }
    }

    class TextItemViewHolder(view: View) : ItemViewHolder(view) {
        val coverImageView: ImageView = view.findViewById(R.id.goods_cover)
        val goodsTitle: TextView = view.findViewById(R.id.goods_title)
        val goodsOffPriceTv: TextView = view.findViewById(R.id.goods_off_price)
        val goodsFinalPriceTv: TextView = view.findViewById(R.id.goods_after_off_prise)
        val goodsOriginalPriceTv: TextView = view.findViewById(R.id.goods_original_price)
        val goodsSellCountTv: TextView = view.findViewById(R.id.goods_sell_count)
    }
}

class TextItemViewModel(private val model_: Data) : RecyclerItemViewModel<TextItemViewModel.HomePagerContentModel>() {

    class ValueChangeMutation(val value: List<Data>) : IMutation

    class ValueChangeEvent(val id: Int, val value: List<Data>) : IEvent

    private val goodsUrl = model_.coupon_click_url ?: model_.click_url
    private val finalPrice = model_.zk_final_price.toFloat() //价格
    private val couponAmount = model_.coupon_amount.toFloat() // 优惠
    private val resultPrice = finalPrice - couponAmount //折后价格
    private val coverPath = UrlUtils.getCoverPath(model_.pict_url)//图片地址

    override val initModel: HomePagerContentModel =
            HomePagerContentModel(goodsUrl, model_.title, model_.coupon_amount, resultPrice, model_.zk_final_price, model_.volume, coverPath)

    override fun transformMutation(mutation: Observable<IMutation>): Observable<IMutation> {
        val value = RxBus.instance.toObservable(ValueChangeEvent::class.java)
                .filter {
                    it.id == currentModel().id
                }
                .map {
                    ValueChangeMutation(it.value)
                }

        return Observable.merge(value, mutation)
    }

    override fun scan(model: HomePagerContentModel, mutation: IMutation): HomePagerContentModel {
        when (mutation) {
            is ValueChangeMutation -> model.homePagerContent = mutation.value
        }
        return model
    }

    class HomePagerContentModel(val goodsUrl: String,
                                val goodsTitle: String, val goodsOffPrice: Int, val goodsFinalPrice: Float,
                                val goodsOriginalPrice: String, val goodsSellCount: Int, val goodsCover: String

    ) : IModel {
        var homePagerContent = emptyList<Data>()
        //TODO:这里需要修改
        var id = initId++
    }

}

class LoadingItemViewModel : IDefaultView<com.adgvcxz.recyclerviewmodel.LoadingItemViewModel> {
    override val layoutId: Int = R.layout.item_loading

    override fun bind(viewHolder: ItemViewHolder, viewModel: com.adgvcxz.recyclerviewmodel.LoadingItemViewModel, position: Int) {
        with(viewHolder.itemView) {
            viewModel.toBind(viewHolder.disposables) {
                add({ state != com.adgvcxz.recyclerviewmodel.LoadingItemViewModel.State.Failure }, {
                    loading.visibility = if (this) View.VISIBLE else View.GONE
                    failed.visibility = if (this) View.GONE else View.VISIBLE
                })
            }
        }
    }
}

//根据分类Id获取内容
fun getCategoryByContentId(categoryId: Int, refresh: Boolean): Observable<HomePagerContent> {
    val pageInfo: HashMap<Int, Int> = HashMap()
    var defaultPage = 1
    //设置当前页码
    if (refresh) {
        defaultPage = 1
    } else {
        defaultPage++
    }
    pageInfo[categoryId] = defaultPage
    val task = createTask(categoryId, defaultPage)
    return task.toRx()
}

private fun createTask(categoryId: Int, targetPage: Int): Call<HomePagerContent> {
    val url = UrlUtils.getDiscoveryContentUrl(categoryId, targetPage)
    return api.getContentListByMaterialId(url)
}


