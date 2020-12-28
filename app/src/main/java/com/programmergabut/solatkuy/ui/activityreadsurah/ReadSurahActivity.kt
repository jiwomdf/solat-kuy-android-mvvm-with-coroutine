package com.programmergabut.solatkuy.ui.activityreadsurah

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseActivity
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Data
import com.programmergabut.solatkuy.databinding.ActivityReadSurahBinding
import com.programmergabut.solatkuy.databinding.ListReadSurahBinding
import com.programmergabut.solatkuy.util.EnumStatus
import com.programmergabut.solatkuy.util.LogConfig.Companion.ERROR
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty


@AndroidEntryPoint
class ReadSurahActivity : BaseActivity<ActivityReadSurahBinding, ReadSurahViewModel>(
    R.layout.activity_read_surah,
    ReadSurahViewModel::class.java
), View.OnClickListener {

    companion object{
        const val SURAH_ID = "SURAH_ID"
        const val SURAH_NAME = "SURAH_NAME"
        const val SURAH_TRANSLATION = "SURAH_TRANSLATION"
        const val IS_AUTO_SCROLL = "IS_AUTO_SCROLL"
    }

    private lateinit var readSurahAdapter: ReadSurahAdapter
    private lateinit var selectedSurahId: String
    private lateinit var selectedSurahName: String
    private lateinit var selectedTranslation: String
    private var isAutoScroll = false
    private var isFirstLoad = true
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setIntentExtra()
        setFirstView()
        viewModel.fetchReadSurahAr(selectedSurahId.toInt())
    }

    private fun setIntentExtra() {
        try{
            selectedSurahId = intent.getStringExtra(SURAH_ID) ?: throw Exception("ReadSurahActivity getExtras surahID")
            selectedSurahName = intent.getStringExtra(SURAH_NAME) ?: throw Exception("ReadSurahActivity getExtras surahName")
            selectedTranslation = intent.getStringExtra(SURAH_TRANSLATION) ?: throw Exception("ReadSurahActivity getExtras surahTranslation")
            isAutoScroll = intent.getBooleanExtra(IS_AUTO_SCROLL, false)
        }
        catch (ex: Exception){
            Log.d(ERROR, ex.message.toString())
            showBottomSheet(
                resources.getString(R.string.text_error_title),
                "",
                isCancelable = false,
                isFinish = true
            )
        }
    }

    private fun setFirstView() {
        setSupportActionBar(binding.tbReadSurah)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initRVReadSurah()
        setTheme(getIsBrightnessActive())
    }

    override fun setListener() {
        super.setListener()
        binding.fabBrightness.setOnClickListener(this)

        viewModel.selectedSurahAr.observe(this, {
            when (it.status) {
                EnumStatus.SUCCESS -> {
                    if (it.data?.data == null)
                        showBottomSheet(isCancelable = false, isFinish = true)

                    if(!checkLastSurahAndAyah())
                        showBottomSheet("Error Occurred", "Last surah and ayah not found", isCancelable = true, isFinish = true)

                    setVisibility(it.status)
                    setToolBarText(it.data?.data!!)
                    viewModel.getListFavAyahBySurahID(
                        selectedSurahId.toInt(), selectedSurahId.toInt(),
                        getLastReadSurah(), getLastReadAyah()
                    )

                    if(isFirstLoad){
                        Toast.makeText(this, "Swipe left to save your last read ayah", Toast.LENGTH_SHORT).show()
                        isFirstLoad = false
                    }
                }
                EnumStatus.LOADING -> {
                    setVisibility(it.status)
                    binding.tbReadSurah.title = ""
                }
                EnumStatus.ERROR -> {
                    setVisibility(it.status)
                    binding.lottieAnimationView.cancelAnimation()
                    binding.tvReadQuranLoading.text = getString(R.string.fetch_failed)
                    showBottomSheet(isCancelable = false, isFinish = true)
                }
            }
        })

        viewModel.msFavAyahBySurahID.observe(this, { local ->
            when (local.status) {
                EnumStatus.SUCCESS -> {
                    readSurahAdapter.apply {
                        listAyah = viewModel.fetchedArSurah.data.ayahs
                        notifyDataSetChanged()
                    }
                    if (isAutoScroll) {
                        (binding.rvReadSurah.layoutManager as LinearLayoutManager)
                            .scrollToPositionWithOffset(getLastReadAyah() - 1, 0)
                    }
                }
                EnumStatus.ERROR -> showBottomSheet(isCancelable = false, isFinish = true)
                else -> {/*NO-OP*/}
            }
        })

        viewModel.msFavSurah.observe(this, {
            if (it == null)
                menu?.findItem(R.id.i_star_surah)?.icon =
                    ContextCompat.getDrawable(this, R.drawable.ic_star_24)
            else
                menu?.findItem(R.id.i_star_surah)?.icon =
                    ContextCompat.getDrawable(this, R.drawable.ic_star_purple_24)
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fab_brightness -> {
                if(!getIsBrightnessActive()){
                    setIsBrightnessActive(true)
                    setTheme(true)
                }
                else{
                    setIsBrightnessActive(false)
                    setTheme(false)
                }
                readSurahAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun checkLastSurahAndAyah(): Boolean {
        val lastSurah = getLastReadSurah()
        val lastAyah = getLastReadAyah()

        if (lastSurah == -1 && lastAyah == -1)
            insertLastReadSharedPref(0, 0)
        else if(lastSurah == -1 || lastAyah == -1)
            return false

        return true
    }

    private fun setTheme(isBrightnessActive: Boolean){
        if(isBrightnessActive){
            binding.tbReadSurah.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
            binding.tbReadSurah.setSubtitleTextColor(ContextCompat.getColor(this, R.color.black))
            binding.tbReadSurah.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
        else{
            binding.tbReadSurah.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tbReadSurah.setSubtitleTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tbReadSurah.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_700))
        }
    }

    private fun setToolBarText(data: Data) {
        binding.tbReadSurah.title = data.englishName
        binding.tbReadSurah.subtitle = data.revelationType + " - " + data.numberOfAyahs + " Ayahs"
    }

    private fun setVisibility(status: EnumStatus){
        when(status){
            EnumStatus.SUCCESS -> {
                binding.rvReadSurah.visibility = View.VISIBLE
                binding.abReadQuran.visibility = View.VISIBLE
                binding.ccReadQuranLoading.visibility = View.GONE
                binding.fabBrightness.visibility = View.VISIBLE
            }
            EnumStatus.LOADING -> {
                binding.abReadQuran.visibility = View.INVISIBLE
                binding.ccReadQuranLoading.visibility = View.VISIBLE
                binding.rvReadSurah.visibility = View.INVISIBLE
                binding.fabBrightness.visibility = View.GONE
            }
            EnumStatus.ERROR -> {
                binding.abReadQuran.visibility = View.INVISIBLE
                binding.rvReadSurah.visibility = View.INVISIBLE
                binding.fabBrightness.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.read_surah_menu, menu)

        viewModel.getFavSurahBySurahID(selectedSurahId.toInt())
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.i_star_surah -> {
                if (menu?.findItem(R.id.i_star_surah)?.icon?.constantState
                    == ContextCompat.getDrawable(this, R.drawable.ic_star_24)?.constantState
                )
                    viewModel.insertFavSurah(
                        MsFavSurah(
                            selectedSurahId.toInt(),
                            selectedSurahName,
                            selectedTranslation
                        )
                    )
                else
                    viewModel.deleteFavSurah(
                        MsFavSurah(
                            selectedSurahId.toInt(),
                            selectedSurahName,
                            selectedTranslation
                        )
                    )

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initRVReadSurah() {
        readSurahAdapter = ReadSurahAdapter(
            onClickFavAyah,
            adapterTheme,
            ContextCompat.getDrawable(this, R.drawable.ic_favorite_red_24)!!,
            ContextCompat.getDrawable(this, R.drawable.ic_favorite_24)!!,
            ContextCompat.getColor(this, R.color.purple_500)
        )

        binding.rvReadSurah.apply {
            adapter = readSurahAdapter
            layoutManager = LinearLayoutManager(this@ReadSurahActivity)
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
        }
    }

    private val onClickFavAyah = fun(data: Ayah, binding: ListReadSurahBinding){
        val msFavAyah = MsFavAyah(
            selectedSurahId.toInt(),
            data.numberInSurah,
            selectedSurahName,
            data.text,
            data.textEn!!
        )

        if (binding.ivListFavFav.drawable.constantState == ContextCompat.getDrawable(
                this,
                R.drawable.ic_favorite_red_24
            )?.constantState
        ) {
            Toasty.info(this, "Unsaving the ayah", Toast.LENGTH_SHORT).show()
            viewModel.deleteFavAyah(msFavAyah)
            binding.ivListFavFav.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_favorite_24
                )
            )
        } else {
            Toasty.info(this, "Saving the ayah", Toast.LENGTH_SHORT).show()
            viewModel.insertFavAyah(msFavAyah)
            binding.ivListFavFav.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_favorite_red_24
                )
            )
        }
        viewModel.fetchReadSurahAr(selectedSurahId.toInt())
    }

    private val adapterTheme = fun(binding: ListReadSurahBinding){
        if(getIsBrightnessActive()){
            binding.tvListFavAr.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.tvListFavEn.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.tvListFavNum.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.clVhReadSurah.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
        else {
            binding.tvListFavAr.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tvListFavEn.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tvListFavNum.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.clVhReadSurah.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.dark_500
                )
            )
        }
    }

    private val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        0, LEFT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.layoutPosition
            val item = readSurahAdapter.listAyah[pos]

            insertLastReadSharedPref(selectedSurahId.toInt(), item.numberInSurah)
            Toasty.success(
                this@ReadSurahActivity,
                "Surah $selectedSurahId ayah ${item.numberInSurah} is now your last read",
                Toasty.LENGTH_LONG
            ).show()

            viewModel.fetchReadSurahAr(selectedSurahId.toInt())
            readSurahAdapter.notifyDataSetChanged()
        }

        override fun onChildDraw(
            canvas: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(
                canvas,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )

            val background =  ColorDrawable(
                ContextCompat.getColor(
                    this@ReadSurahActivity,
                    R.color.purple_500
                )
            )
            background.setBounds(
                viewHolder.itemView.right, viewHolder.itemView.top,
                viewHolder.itemView.left, viewHolder.itemView.bottom
            )
            background.draw(canvas)

            val icon = ContextCompat.getDrawable(
                this@ReadSurahActivity,
                R.drawable.ic_baseline_check_24
            ) ?: return

            val iconMargin = 50
            val iconTop = viewHolder.itemView.top + (viewHolder.itemView.height - icon.intrinsicHeight) / 2
            val iconBottom: Int = iconTop + icon.intrinsicHeight

            when {
                dX > 0 -> { // Swiping to the right
                    val iconLeft: Int = viewHolder.itemView.left + iconMargin + icon.intrinsicWidth
                    val iconRight: Int = viewHolder.itemView.left + iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                }
                dX < 0 -> { // Swiping to the left
                    val iconLeft: Int = viewHolder.itemView.right - iconMargin - icon.intrinsicWidth
                    val iconRight: Int = viewHolder.itemView.right - iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                }
            }

            icon.draw(canvas)
        }
    }


}