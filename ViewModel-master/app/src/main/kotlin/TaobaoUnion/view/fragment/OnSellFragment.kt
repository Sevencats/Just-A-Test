package TaobaoUnion.view.fragment

import TaobaoUnion.view.activity.TicketActivity
import TaobaoUnion.viewmodel.OnSellRecyclerTextItemView
import TaobaoUnion.viewmodel.OnSellRecyclerTextItemViewModel
import TaobaoUnion.viewmodel.OnSellRecyclerViewModel
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adgvcxz.add
import com.adgvcxz.addTo
import com.adgvcxz.recyclerviewmodel.RecyclerAdapter
import com.adgvcxz.recyclerviewmodel.Refresh
import com.adgvcxz.recyclerviewmodel.itemClicks
import com.adgvcxz.toBind
import com.adgvcxz.viewmodel.sample.LoadingItemView
import com.adgvcxz.viewmodel.sample.R
import kotlinx.android.synthetic.main.fragment_on_sell.*

class OnSellFragment : BaseFragment() {
    private lateinit var mGridLayoutManager: LinearLayoutManager
    private lateinit var intent: Intent

    private val mOnSellRecyclerViewModel = OnSellRecyclerViewModel()

    override fun loadRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_with_bar_layout, container, false)
    }

    override fun getRootViewId(): Int = R.layout.fragment_on_sell

    override fun initView(rootView: View) {
        mGridLayoutManager = GridLayoutManager(context, 2)
        on_sell_content_list.layoutManager = mGridLayoutManager
        intent = Intent(context, TicketActivity::class.java)
    }

    override fun initBinding() {
        val adapter = RecyclerAdapter(mOnSellRecyclerViewModel) {
            when (it) {
                is OnSellRecyclerTextItemViewModel -> OnSellRecyclerTextItemView()
                else -> LoadingItemView()
            }
        }
        adapter.itemClicks()
                .filter {
                    mOnSellRecyclerViewModel.currentModel().items[it].currentModel() is OnSellRecyclerTextItemViewModel.OnSellContentModel
                }.map {
                    mOnSellRecyclerViewModel.currentModel().items[it].currentModel() as OnSellRecyclerTextItemViewModel.OnSellContentModel
                }.subscribe {
                    intent.apply {
                        putExtra("ticket_url", it.couponClickUrl)
                        putExtra("ticket_title", it.title)
                        putExtra("ticket_cover", it.cover)
                    }
                    startActivity(intent)
                }.addTo(disposables)

        on_sell_content_list.adapter = adapter
        mOnSellRecyclerViewModel.toBind(disposables) {
            add({ isRefresh }, { refreshLayout2.isRefreshing = this })
        }
        mOnSellRecyclerViewModel.action.onNext(Refresh)
    }
}