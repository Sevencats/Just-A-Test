package TaobaoUnion.view.fragment

import TaobaoUnion.view.activity.TicketActivity
import TaobaoUnion.viewmodel.*
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.adgvcxz.add
import com.adgvcxz.addTo
import com.adgvcxz.recyclerviewmodel.RecyclerAdapter
import com.adgvcxz.recyclerviewmodel.Refresh
import com.adgvcxz.recyclerviewmodel.itemClicks
import com.adgvcxz.toBind
import com.adgvcxz.toEventBind
import com.adgvcxz.viewmodel.sample.R
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import kotlinx.android.synthetic.main.fragment_selected.*
import kotlinx.android.synthetic.main.fragment_with_bar_layout.*

class SelectedFragment : BaseFragment() {
    private val mSelectedPageViewModel = SelectedPageViewModel()

    private lateinit var intent: Intent

    override fun loadRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_with_bar_layout, container, false)
    }

    override fun getRootViewId(): Int = R.layout.fragment_selected

    override fun initView(rootView: View) {

        setUpState(State.SUCCESS)
        intent = Intent(context, TicketActivity::class.java)
        left_category_list.layoutManager = LinearLayoutManager(context)
        fragment_bar_title_tv.text = "精选宝贝"
        right_content_list.layoutManager = LinearLayoutManager(context)

    }

    override fun initBinding() {
        val leftAdapter = RecyclerAdapter(mSelectedPageViewModel) {
            when (it) {
                is SelectedPageTextItemViewModel -> SelectedPageTextItemView()
                else -> LoadingItemViewModel()
            }
        }

        left_category_list.adapter = leftAdapter

        leftAdapter.itemClicks()
                .filter {
                    mSelectedPageViewModel.currentModel().items[it].currentModel() is SelectedPageTextItemViewModel.SelectedPageCategoryModel
                }.map {
                    mSelectedPageViewModel.currentModel().items[it].currentModel() as SelectedPageTextItemViewModel.SelectedPageCategoryModel
                }.subscribe {
//                    val mCurrentPosition = 0
                    val mSelectedPageContentViewModel = SelectedPageContentViewModel(it.favoritesId)
                    val rightAdapter = RecyclerAdapter(mSelectedPageContentViewModel) {
                        when (it) {
                            is SelectedPageContentTextItemViewModel -> SelectedPageContentTextItemView()
                            else -> LoadingItemViewModel()
                        }
                    }

                    right_content_list.adapter = rightAdapter

                    //rightAdapter的点击事件
                    rightAdapter.itemClicks().filter {
                        mSelectedPageContentViewModel.currentModel().items[it].currentModel() is SelectedPageContentTextItemViewModel.SelectedPageContentModel
                    }.map {
                        mSelectedPageContentViewModel.currentModel().items[it].currentModel() as SelectedPageContentTextItemViewModel.SelectedPageContentModel
                    }.subscribe {
                        intent.apply {
                            putExtra("ticket_url", it.couponClickUrl)
                            putExtra("ticket_title", it.title)
                           putExtra("ticket_cover", it.cover)
                        }
                        startActivity(intent)
                    }.addTo(disposables)

                    mSelectedPageContentViewModel.toEventBind(disposables) {
                        add({ refreshes() }, refreshLayout1, { Refresh })
                    }
                    mSelectedPageContentViewModel.toBind(disposables) {
                        add({ isRefresh }, { refreshLayout1.isRefreshing = this })
                    }
                    mSelectedPageContentViewModel.action.onNext(Refresh)
                }.addTo(disposables)

        mSelectedPageViewModel.action.onNext(Refresh)
    }
}
