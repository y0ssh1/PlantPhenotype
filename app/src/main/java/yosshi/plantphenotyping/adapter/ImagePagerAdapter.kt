package yosshi.plantphenotyping.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import yosshi.plantphenotyping.fragment.ImageFragment
import kotlin.math.max

class ImagePagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {

    val items: BehaviorRelay<List<String>> = BehaviorRelay.createDefault(emptyList())
    private val bag = CompositeDisposable()

    init {
        items
            .subscribeBy(onNext = { notifyDataSetChanged() })
            .addTo(bag)
    }

    fun dispose() = bag.clear()

    override fun getCount() = max(items.value?.size ?: 0, 1)

    override fun getItem(p0: Int): Fragment =
        ImageFragment.newInstance(urlString = items.value?.getOrNull(p0))

    override fun getItemPosition(`object`: Any) = PagerAdapter.POSITION_NONE
}