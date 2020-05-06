package TaobaoUnion.view.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adgvcxz.viewmodel.sample.R

class SearchFragment : BaseFragment() {

    override fun loadRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_search_layout, container, false)
    }

    override fun getRootViewId(): Int = R.layout.fragment_search

    override fun initView(rootView: View) {
        setUpState(State.SUCCESS)
    }
}