package com.programmergabut.solatkuy.viewmodel

/* @Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: Repository,
                       private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    companion object{
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(application: Application, context: Context): ViewModelFactory{
            return instance ?: synchronized(this){
                instance ?: ViewModelFactory(Injection.provideRepository(application), context)
            }
        }
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val networkHelper = NetworkHelper.getInstance(context)

        return when{
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> MainActivityViewModel(repository) as T
            modelClass.isAssignableFrom(FragmentSettingViewModel::class.java) -> FragmentSettingViewModel(repository) as T
            modelClass.isAssignableFrom(FragmentMainViewModel::class.java) -> FragmentMainViewModel(repository, networkHelper) as T
            modelClass.isAssignableFrom(FragmentCompassViewModel::class.java) -> FragmentCompassViewModel(repository, networkHelper) as T
            modelClass.isAssignableFrom(QuranFragmentViewModel::class.java) -> QuranFragmentViewModel(repository, networkHelper) as T
            modelClass.isAssignableFrom(FragmentInfoViewModel::class.java) -> FragmentInfoViewModel(repository, networkHelper) as T
            modelClass.isAssignableFrom(ReadSurahViewModel::class.java) -> ReadSurahViewModel(repository, networkHelper) as T
            modelClass.isAssignableFrom(FavAyahViewModel::class.java) -> FavAyahViewModel(repository) as T
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    }
} */