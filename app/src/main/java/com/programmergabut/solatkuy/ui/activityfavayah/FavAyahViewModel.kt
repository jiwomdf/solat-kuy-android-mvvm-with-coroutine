package com.programmergabut.solatkuy.ui.activityfavayah

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.programmergabut.solatkuy.data.Repository

class FavAyahViewModel(application: Application, repository: Repository): AndroidViewModel(application) {

    val favAyah = repository.getMsFavAyah()

}