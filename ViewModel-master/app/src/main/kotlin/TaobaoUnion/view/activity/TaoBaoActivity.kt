package TaobaoUnion.view.activity

import TaobaoUnion.view.fragment.*
import android.annotation.SuppressLint
import androidx.fragment.app.FragmentManager
import com.adgvcxz.viewmodel.sample.BaseActivity
import com.adgvcxz.viewmodel.sample.R
import kotlinx.android.synthetic.main.activity_tao_bao.*

@SuppressLint("Registered")
class TaoBaoActivity : BaseActivity() {
    private var lastOneFragment: BaseFragment? = null
    private lateinit var  mHomeFragment: HomeFragment
    private lateinit var mSelectedFragment: SelectedFragment
    private lateinit var mOnSellFragment: OnSellFragment
    private lateinit var mSearchFragment: SearchFragment
    private lateinit var mFm:FragmentManager
    override val layoutId: Int = R.layout.activity_tao_bao
    override fun initBinding() {
        super.initBinding()
        initFragments()
        initListener()
    }

    private fun initFragments() {
        mHomeFragment = HomeFragment()
        mSelectedFragment = SelectedFragment()
        mOnSellFragment = OnSellFragment()
        mSearchFragment = SearchFragment()
        mFm = supportFragmentManager
        switchFragment(mHomeFragment)
    }

    private fun initListener() {
        main_navigation_bar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> switchFragment(mHomeFragment)
                R.id.selected -> switchFragment(mSelectedFragment)
                R.id.red_packet -> switchFragment(mOnSellFragment)
                R.id.search -> switchFragment(mSearchFragment)
            }
            return@setOnNavigationItemSelectedListener true
        }

    }

    private fun switchFragment(targetFragment: BaseFragment) {
        val fragmentTransaction = mFm.beginTransaction()
        if (!targetFragment.isAdded) {
            fragmentTransaction.add(R.id.main_page_container, targetFragment)
        } else {
            fragmentTransaction.show(targetFragment)
        }
        if (lastOneFragment != null) {
            fragmentTransaction.hide(lastOneFragment!!)
        }
        lastOneFragment = targetFragment
        fragmentTransaction.commit()
    }

}