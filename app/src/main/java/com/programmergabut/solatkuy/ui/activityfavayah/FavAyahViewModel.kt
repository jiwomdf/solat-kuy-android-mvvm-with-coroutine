package com.programmergabut.solatkuy.ui.activityfavayah

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah

class FavAyahViewModel(application: Application,private val repository: Repository): AndroidViewModel(application) {

    val favAyah = repository.getMsFavAyah()

    fun deleteFavAyah(msFavAyah: MsFavAyah) = repository.deleteFavAyah(msFavAyah)

}