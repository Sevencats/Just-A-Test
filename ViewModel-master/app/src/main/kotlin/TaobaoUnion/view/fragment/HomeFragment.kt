package TaobaoUnion.view.fragment

import TaobaoUnion.view.adapter.HomePageAdapter
import TaobaoUnion.viewmodel.HomeViewModel
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adgvcxz.add
import com.adgvcxz.toBind
import com.adgvcxz.viewmodel.sample.R
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * time:2020.4.24
 * author:LihC
 */
class HomeFragment : BaseFragment() {

    private val mHomeViewModel: HomeViewModel = HomeViewModel()

    private lateinit var mHomePageAdapter: HomePageAdapter

    override fun getRootViewId(): Int = R.layout.fragment_home

    override fun loadRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.base_home_fragment_layout, container, false)
    }

    override fun initView(rootView: View) {
        home_indicator.setupWithViewPager(home_pager)
        mHomePageAdapter = HomePageAdapter(childFragmentManager)
        home_pager.adapter = mHomePageAdapter
    }

    override fun initBinding() {

        mHomeViewModel.toBind(disposables) {
            add({ categories }, {
                mHomePageAdapter.setCategories(this)
            })
        }

        mHomeViewModel.action.onNext(HomeViewModel.Event.StartEvent)

    }
}