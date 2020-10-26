package com.programmergabut.solatkuy.ui.activityreadsurah

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseActivity
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Data
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import com.programmergabut.solatkuy.util.getOrAwaitValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_read_surah.*

@AndroidEntryPoint
class ReadSurahActivity : BaseActivity(R.layout.activity_read_surah) {

    companion object{
        const val surahID = "surahID"
        const val surahName = "surahName"
        const val surahTranslation = "surahTranslation"
    }

    private val readSurahViewModel: ReadSurahViewModel by viewModels()

    private lateinit var readSurahAdapter: ReadSurahAdapter
    private lateinit var mSelSurahId: String
    private lateinit var mSelSurahName: String
    private lateinit var mSelSurahTranslation: String

    private var mMenu: Menu? = null

    override fun setListener() {}
    override fun setIntentExtra() {
        mSelSurahId = intent.getStringExtra(surahID) ?: throw Exception("getExtras surahID")
        mSelSurahName = intent.getStringExtra(surahName) ?: throw Exception("getExtras surahName")
        mSelSurahTranslation = intent.getStringExtra(surahTranslation) ?: throw Exception("getExtras surahTranslation")
    }

    override fun setFirstView() {
        setSupportActionBar(tb_readSurah)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initRVReadSurah()
    }

    override fun setObserver() {
        var data: Data? = null

        readSurahViewModel.selectedSurahAr.observe(this, {
            when(it.status){
                EnumStatus.SUCCESS -> {
                    if(it.data == null)
                        throw Exception(Thread.currentThread().stackTrace[1].methodName)
                    data = it.data.data
                    setVisibility(it.status)
                    tb_readSurah.title = data!!.englishName
                    tb_readSurah.subtitle = data!!.revelationType + " - " + data!!.numberOfAyahs + " Ayahs"
                    readSurahViewModel.getListFavAyahBySurahID(mSelSurahId.toInt())
                }
                EnumStatus.LOADING -> {
                    setVisibility(it.status)
                    tb_readSurah.title = ""
                }
                EnumStatus.ERROR -> {
                    setVisibility(it.status)
                    lottieAnimationView.cancelAnimation()
                    tv_readQuran_loading.text = getString(R.string.fetch_failed)
                }
            }
        })

        readSurahViewModel.msFavAyahBySurahID.observe(this, { local ->
            when(local.status){
                EnumStatus.SUCCESS -> {

                    local.data?.forEach { ayah ->
                        data?.ayahs?.forEach out@{remoteAyah ->
                            if(remoteAyah.numberInSurah == ayah.ayahID && mSelSurahId.toInt() == ayah.surahID){
                                remoteAyah.isFav = true
                                return@out
                            }
                        }
                    }
                    readSurahAdapter.listAyah = data?.ayahs!!
                    readSurahAdapter.notifyDataSetChanged()
                }
                EnumStatus.LOADING -> {}
                EnumStatus.ERROR -> {}
            }
        })

        readSurahViewModel.fetchReadSurahAr(mSelSurahId.toInt())
    }

    private fun setVisibility(status: EnumStatus){
        when(status){
            EnumStatus.SUCCESS -> {
                rv_read_surah.visibility = View.VISIBLE
                ab_readQuran.visibility = View.VISIBLE
                cc_readQuran_loading.visibility = View.GONE
            }
            EnumStatus.LOADING -> {
                ab_readQuran.visibility = View.INVISIBLE
                cc_readQuran_loading.visibility = View.VISIBLE
                rv_read_surah.visibility = View.INVISIBLE
            }
            EnumStatus.ERROR -> {
                ab_readQuran.visibility = View.INVISIBLE
                rv_read_surah.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.mMenu = menu
        menuInflater.inflate(R.menu.read_surah_menu, menu)

        readSurahViewModel.msFavSurah.observe(this, {
            when (it.status) {
                EnumStatus.SUCCESS -> {
                    if (it.data == null)
                        menu?.findItem(R.id.i_star_surah)?.icon =
                            ContextCompat.getDrawable(this, R.drawable.ic_star_24)
                    else
                        menu?.findItem(R.id.i_star_surah)?.icon =
                            ContextCompat.getDrawable(this, R.drawable.ic_star_yellow_24)
                }
                EnumStatus.LOADING -> {}
                EnumStatus.ERROR -> {}
            }
        })

        readSurahViewModel.getFavSurahBySurahID(mSelSurahId.toInt())

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.i_star_surah -> {
                if(mMenu?.findItem(R.id.i_star_surah)?.icon?.constantState == this.getDrawable(R.drawable.ic_star_24)?.constantState)
                    readSurahViewModel.insertFavSurah(MsFavSurah(mSelSurahId.toInt(), mSelSurahName, mSelSurahTranslation))
                else
                    readSurahViewModel.deleteFavSurah(MsFavSurah(mSelSurahId.toInt(), mSelSurahName, mSelSurahTranslation))

                //readSurahViewModel.getFavSurah(mSelSurahId.toInt())
                true
            }
            else -> super.onOptionsItemSelected(item!!)
        }
    }


    private fun initRVReadSurah() {
        readSurahAdapter = ReadSurahAdapter(readSurahViewModel, mSelSurahId, mSelSurahName, this)
        rv_read_surah.apply {
            adapter = readSurahAdapter
            layoutManager = LinearLayoutManager(this@ReadSurahActivity)
            setHasFixedSize(true)
        }
    }

}