package yosshi.plantphenotyping

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import yosshi.plantphenotyping.adapter.ImagePagerAdapter
import yosshi.plantphenotyping.api.APIClient
import yosshi.plantphenotyping.databinding.ActivityMainBinding
import yosshi.plantphenotyping.model.ActivityResultData
import yosshi.plantphenotyping.model.ImageRequestBody
import yosshi.plantphenotyping.model.ImageResponseBody
import yosshi.plantphenotyping.service.Base64Service

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_GALLERY = 0
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ImagePagerAdapter
    private val onReceiveActivityResult = PublishRelay.create<ActivityResultData>()
    private val imageResponse: BehaviorRelay<ImageResponseBody> = BehaviorRelay.create()
    private val state: BehaviorRelay<Int> = BehaviorRelay.createDefault(1)
    private val bag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        adapter = ImagePagerAdapter(fm = supportFragmentManager)
        setupView()
        setupListener()
    }

    override fun onDestroy() {
        bag.clear()
        adapter.dispose()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onReceiveActivityResult.accept(ActivityResultData(requestCode = requestCode, resultCode = resultCode, data = data))
    }

    private fun setupView() {
        binding.viewPager.adapter = adapter
        binding.circlePageIndicator.setViewPager(binding.viewPager, 0)
        binding.circlePageIndicator.visibility = View.GONE
        binding.pieChartPlantRatio.setNoDataText("")
        binding.pieChartPlantRatio.setUsePercentValues(true)
        binding.pieChartPlantRatio.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        binding.pieChartPlantRatio.visibility = View.GONE
    }

    private fun setupListener() {
        binding.buttonAddPicture.clicks()
            .observeOn(Schedulers.io())
            .doAfterNext {
                Intent().apply {
                    this.type = "image/*"
                    this.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(this, REQUEST_GALLERY)
                }
            }
            .flatMap { onReceiveActivityResult }
            .filter { it.isValid }
            .map { MediaStore.Images.Media.getBitmap(contentResolver, it.data?.data!!) }
            .map { Base64Service().encode(it) }
            .subscribe { sendRequest(it) }
            .addTo(bag)


        imageResponse
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ reloadView(image = it) }, {})
            .addTo(bag)

        state
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ binding.viewAnimator.displayedChild = it }, {})
            .addTo(bag)
    }

    private fun reloadView(image: ImageResponseBody) {
        binding.textViewFlowerCount.text = getString(R.string.count, image?.flowerCount ?: 0)
        binding.textViewerFruitCount.text = getString(R.string.count, image?.fruitCount ?: 0)
        when (image.images.isEmpty()) {
            false -> {
                binding.circlePageIndicator.visibility = View.VISIBLE
                binding.pieChartPlantRatio.visibility = View.VISIBLE
            }
            else -> {
                binding.circlePageIndicator.visibility = View.GONE
                binding.pieChartPlantRatio.visibility = View.GONE
            }
        }
        binding.pieChartPlantRatio.data = image.let { body ->
            return@let body.ratio
                .asSequence()
                .filter { r -> r.count != 0f }
                .map { r -> PieEntry(r.count, r.segmentType.labelName) }
                .toList()
                .run {
                    val dataSet  = PieDataSet(this, "")
                    dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
                    dataSet.setDrawValues(true)
                    return@run dataSet
                }
                .run {
                    val pieData = PieData(this)
                    pieData.setValueFormatter(PercentFormatter())
                    pieData.setValueTextSize(20f)
                    pieData.setValueTextColor(Color.WHITE)
                    return@run pieData
                }
        }
        binding.pieChartPlantRatio.notifyDataSetChanged()
        binding.pieChartPlantRatio.invalidate()
        adapter.items.accept(image.images)
    }

    private fun sendRequest(base64String: String) {
        APIClient.instance.postImage(body = ImageRequestBody(images = base64String))
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { state.accept(0) }
            .doOnSuccess { state.accept(1) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                imageResponse.accept(it.body())
            }, {
                Toast.makeText(this, "通信エラー", Toast.LENGTH_SHORT).show()
            })
            .addTo(bag)
    }
}
