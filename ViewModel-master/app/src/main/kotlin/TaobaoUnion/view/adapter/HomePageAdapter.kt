package TaobaoUnion.view.adapter

import TaobaoUnion.model.domain.Categories
import TaobaoUnion.view.fragment.HomePagerFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class HomePageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val categoryList = arrayListOf<Categories.Data>()
    private lateinit var homePagerFragment: HomePagerFragment

    override fun getItem(position: Int): Fragment {
        val dataBean = categoryList[position]
        homePagerFragment = HomePagerFragment.newInstance(dataBean)
        return homePagerFragment
    }

    override fun getCount(): Int = categoryList.size
    override fun getPageTitle(position: Int): CharSequence? = categoryList[position].title

    //设置数据
    fun setCategories(categories: List<Categories.Data>) {
        categoryList.clear()
        val data = categories
        this.categoryList.addAll(data)
        notifyDataSetChanged()
    }
}