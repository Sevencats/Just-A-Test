package TaobaoUnion.view.fragment

import TaobaoUnion.model.domain.Categories
import TaobaoUnion.utils.Constants
import TaobaoUnion.view.activity.TicketActivity
import TaobaoUnion.viewmodel.HomePagerRecyclerViewModel
import TaobaoUnion.viewmodel.TextItemView
import TaobaoUnion.viewmodel.TextItemViewModel
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adgvcxz.add
import com.adgvcxz.addTo
import com.adgvcxz.recyclerviewmodel.LoadingItemViewModel
import com.adgvcxz.recyclerviewmodel.RecyclerAdapter
import com.adgvcxz.recyclerviewmodel.Refresh
import com.adgvcxz.recyclerviewmodel.itemClicks
import com.adgvcxz.toBind
import com.adgvcxz.toEventBind
import com.adgvcxz.viewmodel.sample.R
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import kotlinx.android.synthetic.main.fragment_home_pager.*
import kotlinx.android.synthetic.main.fragment_home_pager.refreshLayout

class HomePagerFragment : BaseFragment() {

    private val id by lazy { arguments?.getInt(Constants.KEY_HOME_PAGER_MATERIAL_ID) }
    private val mHomePagerRecyclerViewModel by lazy {
        id?.let { HomePagerRecyclerViewModel(it) }
    }

    private lateinit var intent: Intent

    override fun getRootViewId(): Int = R.layout.fragment_home_pager

    override fun initView(rootView: View) {

        intent = Intent(context, TicketActivity::class.java)

        home_pager_content_list.layoutManager = LinearLayoutManager(context)//设置布局管理器
        home_pager_content_list.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

                //设置recyclerView里的itemView之间的间隔
                outRect.top = 16
                outRect.bottom = 16
            }
        })

    }

    @SuppressLint("CheckResult")
    override fun initBinding() {
        val adapter = RecyclerAdapter(mHomePagerRecyclerViewModel!!) {
            when (it) {
                is TextItemViewModel -> TextItemView()
                else -> TaobaoUnion.viewmodel.LoadingItemViewModel()
            }
        }
        home_pager_content_list.adapter = adapter

        mHomePagerRecyclerViewModel?.toEventBind(disposables) {
            add({ refreshes() }, refreshLayout, { Refresh })
        }

        adapter.itemClicks()
                .filter {
                    mHomePagerRecyclerViewModel!!.currentModel().items[it].currentModel() is TextItemViewModel.HomePagerContentModel
                }.map { mHomePagerRecyclerViewModel!!.currentModel().items[it].currentModel() as TextItemViewModel.HomePagerContentModel }
                .subscribe {
                    //传递数据
                    intent.apply {
                        putExtra("ticket_url", it.goodsUrl)
                        putExtra("ticket_title", it.goodsTitle)
                        putExtra("ticket_cover", it.goodsCover)
                    }
                    startActivity(intent)

                }.addTo(disposables)

        mHomePagerRecyclerViewModel?.toBind(disposables) {
            add({ isRefresh }, { refreshLayout.isRefreshing = this })
        }
        mHomePagerRecyclerViewModel?.action?.onNext(Refresh)

    }

    companion object {
        fun newInstance(category: Categories.Data): HomePagerFragment {
            val homePagerFragment = HomePagerFragment()
            //使用Bundle传递数据
            val bundle = Bundle()
            bundle.apply {
                putString(Constants.KEY_HOME_PAGER_TITLE, category.title)
                putInt(Constants.KEY_HOME_PAGER_MATERIAL_ID, category.id)
            }
            homePagerFragment.arguments = bundle
            return homePagerFragment
        }
    }
}