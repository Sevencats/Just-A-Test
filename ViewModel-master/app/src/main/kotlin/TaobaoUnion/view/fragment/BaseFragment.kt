package TaobaoUnion.view.fragment

import TaobaoUnion.view.activity.TicketActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.adgvcxz.viewmodel.sample.R
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment : Fragment() {
    private lateinit var mBaseContainer: FrameLayout
    private lateinit var mRootView: View
    private lateinit var mSuccessView: View
    private lateinit var mLoadingView: View
    private lateinit var mErrorView: View

    internal val disposables: CompositeDisposable by lazy { CompositeDisposable() }

    enum class State {
        SUCCESS, LOADING, ERROR
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = loadRootView(inflater, container)
        mBaseContainer = mRootView.findViewById(R.id.base_container)
        loadStateView(inflater, container)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initBinding()
    }

    open fun initBinding() {}

    /**
     * 加载各种状态的View
     */
    private fun loadStateView(inflater: LayoutInflater, container: ViewGroup?) {
        //加载成功的View
        mSuccessView = loadSuccessView(inflater, container)
        //正在加载的View
        mLoadingView = loadLoadingView(inflater, container)
        //加载失败的View
        mErrorView = loadErrorView(inflater, container)
        mBaseContainer.apply {
            addView(mSuccessView)
            addView(mLoadingView)
            addView(mErrorView)
        }
        setUpState(State.SUCCESS)
    }

    internal fun setUpState(state: State) {
        mSuccessView.visibility = when (state) {
            State.SUCCESS -> View.VISIBLE
            else -> View.GONE
        }
        mLoadingView.visibility = when (state) {
            State.LOADING -> View.VISIBLE
            else -> View.GONE
        }
        mErrorView.visibility = when (state) {
            State.ERROR -> View.VISIBLE
            else -> View.GONE
        }
    }

    internal open fun loadRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.base_fragment_layout, container, false)
    }

    internal open fun loadSuccessView(inflater: LayoutInflater, container: ViewGroup?): View {
        val resId = getRootViewId()
        return inflater.inflate(resId, container, false)
    }

    private fun loadLoadingView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    private fun loadErrorView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_error, container, false)
    }

    abstract fun getRootViewId(): Int
    abstract fun initView(rootView: View)

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.dispose()
    }

//    abstract fun Intent(homePagerFragment: HomePagerFragment, java: Class<TicketActivity>): Any
}