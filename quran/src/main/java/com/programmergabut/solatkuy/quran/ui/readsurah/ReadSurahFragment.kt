package com.programmergabut.solatkuy.quran.ui.readsurah

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.data.local.localentity.MsAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.di.SubModuleDependencies
import com.programmergabut.solatkuy.quran.R
import com.programmergabut.solatkuy.quran.base.BaseFragmentQuran
import com.programmergabut.solatkuy.quran.databinding.FragmentReadSurahBinding
import com.programmergabut.solatkuy.quran.databinding.ListReadSurahBinding
import com.programmergabut.solatkuy.quran.di.DaggerQuranViewModelComponent
import com.programmergabut.solatkuy.quran.di.QuranViewModelComponent
import com.programmergabut.solatkuy.util.Status
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import es.dmoral.toasty.Toasty
import com.programmergabut.solatkuy.R as superappR

@AndroidEntryPoint
class ReadSurahFragment(
    viewModelTest: ReadSurahViewModel? = null
) : BaseFragmentQuran<FragmentReadSurahBinding, ReadSurahViewModel>(
    R.layout.fragment_read_surah,
    ReadSurahViewModel::class.java,
    viewModelTest
), View.OnClickListener {

    private val args: ReadSurahFragmentArgs by navArgs()
    private var component: QuranViewModelComponent? = null
    private lateinit var readSurahAdapter: ReadSurahAdapter
    private var menu: Menu? = null
    private var isMoreFabClick = false

    private val rotateOpenAnimation by lazy { AnimationUtils.loadAnimation(requireContext(), superappR.anim.fab_rotate_open_animation)}
    private val rotateCloseAnimation by lazy { AnimationUtils.loadAnimation(requireContext(), superappR.anim.fab_rotate_close_animation)}
    private val fromBottomAnimation by lazy { AnimationUtils.loadAnimation(requireContext(), superappR.anim.fab_from_bottom_animation)}
    private val toBottomAnimation by lazy { AnimationUtils.loadAnimation(requireContext(), superappR.anim.fab_to_bottom_animation)}

    override fun getViewBinding() = FragmentReadSurahBinding.inflate(layoutInflater)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getActivityComponent()?.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        initRVReadSurah()
        setListener()
        viewModel.getSelectedSurah(args.selectedSurahId.toInt())
    }

    private fun getActivityComponent(): QuranViewModelComponent? {
        if (component == null) {
            component = DaggerQuranViewModelComponent.builder()
                .context(requireContext().applicationContext)
                .dependencies(
                    EntryPointAccessors.fromApplication(
                        requireContext().applicationContext,
                        SubModuleDependencies::class.java
                    )
                )
                .build()
        }
        return component
    }

    private fun setupView() {
        (activity as AppCompatActivity).setSupportActionBar(binding.tbReadSurah)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTheme(sharedPrefUtil.getIsBrightnessActive())
    }


    private fun setListener() {
        binding.apply {
            fabBrightness.setOnClickListener(this@ReadSurahFragment)
            fabMore.setOnClickListener(this@ReadSurahFragment)
            fabShowContent.setOnClickListener(this@ReadSurahFragment)
            fabArSize.setOnClickListener(this@ReadSurahFragment)

            tbReadSurah.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }

            viewModel.selectedSurah.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.Success, Status.Error -> {
                        if (it.data != null) {
                            checkLastSurahAndAyah(it.data ?: emptyList())
                            setVisibility(it.status)
                            setToolBarText(it.data ?: emptyList())
                            readSurahAdapter.listAyah = it.data ?: emptyList()
                            readSurahAdapter.notifyDataSetChanged()

                            if (args.isAutoScroll) {
                                (rvReadSurah.layoutManager as LinearLayoutManager)
                                    .scrollToPositionWithOffset(sharedPrefUtil.getLastReadAyah() - 1, 0)
                            }
                        }
                    }
                    Status.Loading -> {
                        setVisibility(it.status)
                        tbReadSurah.title = ""
                    }
                }
            }

            viewModel.favSurahBySurahID.observe(viewLifecycleOwner) {
                if (it == null) {
                    menu?.findItem(superappR.id.i_star_surah)?.icon =
                        ContextCompat.getDrawable(requireContext(), superappR.drawable.ic_star_24)
                } else {
                    menu?.findItem(superappR.id.i_star_surah)?.icon =
                        ContextCompat.getDrawable(requireContext(), superappR.drawable.ic_star_purple_24)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fab_more -> {
                setFabVisibility(isMoreFabClick)
                setFabAnimation(isMoreFabClick)
                isMoreFabClick = !isMoreFabClick
            }
            R.id.fab_brightness -> {
                if (!sharedPrefUtil.getIsBrightnessActive()) {
                    sharedPrefUtil.setIsBrightnessActive(true)
                    setTheme(true)
                } else {
                    sharedPrefUtil.setIsBrightnessActive(false)
                    setTheme(false)
                }
                readSurahAdapter.notifyDataSetChanged()
            }
            R.id.fab_show_content -> {
                binding.apply {
                    when(sharedPrefUtil.getReadSurahContentType()){
                        1 -> {
                            tvFontType.text = "ا"
                            fabArSize.isVisible = true
                            sharedPrefUtil.setReadSurahContentType(2)
                        }
                        2 -> {
                            tvFontType.text = "A"
                            fabArSize.isVisible = false
                            sharedPrefUtil.setReadSurahContentType(3)
                        }
                        3 -> {
                            tvFontType.text = "Aا"
                            fabArSize.isVisible = true
                            sharedPrefUtil.setReadSurahContentType(1)
                        }
                    }
                    readSurahAdapter.notifyDataSetChanged()
                }
            }
            R.id.fab_ar_size -> {
                binding.apply {
                    when(sharedPrefUtil.getReadSurahArTextSize()){
                        1 -> {
                            tvArSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24F)
                            sharedPrefUtil.setReadSurahArTextSize(2)
                        }
                        2 -> {
                            tvArSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32F)
                            sharedPrefUtil.setReadSurahArTextSize(3)
                        }
                        3 -> {
                            tvArSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
                            sharedPrefUtil.setReadSurahArTextSize(1)
                        }
                    }
                    readSurahAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun checkLastSurahAndAyah(data: List<MsAyah>) {
        val lastSurah = sharedPrefUtil.getLastReadSurah()
        val lastAyah = sharedPrefUtil.getLastReadAyah()
        if (lastSurah == args.selectedSurahId.toInt()){
            data[lastAyah - 1].isLastRead = true
        }
    }

    private fun setTheme(isBrightnessActive: Boolean){
        binding.apply {
            if(isBrightnessActive){
                tbReadSurah.setTitleTextColor(ContextCompat.getColor(requireContext(), superappR.color.black))
                tbReadSurah.setSubtitleTextColor(ContextCompat.getColor(requireContext(), superappR.color.black))
                tbReadSurah.setBackgroundColor(ContextCompat.getColor(requireContext(), superappR.color.white))
                clSurah.setBackgroundColor(ContextCompat.getColor(requireContext(), superappR.color.white))
            } else {
                tbReadSurah.setTitleTextColor(ContextCompat.getColor(requireContext(), superappR.color.white))
                tbReadSurah.setSubtitleTextColor(ContextCompat.getColor(requireContext(), superappR.color.white))
                tbReadSurah.setBackgroundColor(ContextCompat.getColor(requireContext(), superappR.color.dark_700))
                clSurah.setBackgroundColor(ContextCompat.getColor(requireContext(), superappR.color.black))
            }
        }
    }

    private fun setToolBarText(data: List<MsAyah>) {
        val selectedData = data.first()
        binding.tbReadSurah.apply {
            title = selectedData.englishName
            subtitle = "${selectedData.revelationType} - ${selectedData.numberOfAyahs} Ayahs"
        }
    }

    private fun setVisibility(status: Status){
        binding.apply {
            when(status){
                Status.Success, Status.Error -> {
                    rvReadSurah.visibility = View.VISIBLE
                    fabMore.visibility = View.VISIBLE
                    iLoadingReadSurah.ccReadQuranLoading.visibility = View.GONE
                    setReadSurahAnimation(isLoading = false)
                }
                Status.Loading -> {
                    rvReadSurah.visibility = View.INVISIBLE
                    fabMore.visibility = View.GONE
                    iLoadingReadSurah.ccReadQuranLoading.visibility = View.VISIBLE
                    setReadSurahAnimation(isLoading = true)
                }
            }
        }
    }

    private fun setReadSurahAnimation(isLoading: Boolean) {
        binding.iLoadingReadSurah.apply {
            clLoading1.isVisible = isLoading
            clLoading2.isVisible = isLoading
            clLoading3.isVisible = isLoading
            if(isLoading){
                shimmerViewContainer.startShimmer()
            } else {
                shimmerViewContainer.stopShimmer()
            }
        }
    }

    private fun setFabVisibility(buttonClicked: Boolean) {
        binding.apply {
            if (!buttonClicked){
                fabBrightness.visibility = View.VISIBLE
                rlShowContent.visibility = View.VISIBLE
                rlArSize.visibility = View.VISIBLE
                fabBrightness.isClickable = true
                fabShowContent.isClickable = true
                fabArSize.isClickable = true
            }else{
                fabBrightness.visibility = View.INVISIBLE
                rlShowContent.visibility = View.INVISIBLE
                rlArSize.visibility = View.INVISIBLE
                fabBrightness.isClickable = false
                fabShowContent.isClickable = false
                fabArSize.isClickable = false
            }
        }
    }

    private fun setFabAnimation(buttonClicked: Boolean) {
        binding.apply {
            if (!buttonClicked){
                fabBrightness.startAnimation(fromBottomAnimation)
                fabShowContent.startAnimation(fromBottomAnimation)
                fabArSize.startAnimation(fromBottomAnimation)
                fabMore.startAnimation(rotateOpenAnimation)
            }else{
                fabBrightness.startAnimation(toBottomAnimation)
                fabShowContent.startAnimation(toBottomAnimation)
                fabArSize.startAnimation(toBottomAnimation)
                fabMore.startAnimation(rotateCloseAnimation)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(superappR.menu.read_surah_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.finish()
                true
            }
            superappR.id.i_star_surah -> {
                val data = MsFavSurah(args.selectedSurahId.toInt(), args.selectedSurahName, args.selectedTranslation)
                if (viewModel.favSurahBySurahID.value == null){
                    viewModel.insertFavSurah(data)
                } else {
                    viewModel.deleteFavSurah(data)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initRVReadSurah() {
        readSurahAdapter = ReadSurahAdapter(
            adapterTheme,
            adapterContent,
            adapterArTextSize,
            ContextCompat.getDrawable(requireContext(), superappR.drawable.ic_favorite_red_24)!!,
            ContextCompat.getColor(requireContext(), superappR.color.purple_500)
        )
        binding.rvReadSurah.apply {
            adapter = readSurahAdapter
            layoutManager = LinearLayoutManager(requireContext())
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
        }
    }


    private val adapterTheme = fun(vhBinding: ListReadSurahBinding){
        vhBinding.apply {
            if(sharedPrefUtil.getIsBrightnessActive()){
                tvListFavAr.setTextColor(ContextCompat.getColor(requireContext(), superappR.color.black))
                tvListFavEn.setTextColor(ContextCompat.getColor(requireContext(), superappR.color.black))
                tvListFavNum.setTextColor(ContextCompat.getColor(requireContext(), superappR.color.black))
                clVhReadSurah.setBackgroundColor(ContextCompat.getColor(requireContext(), superappR.color.white))
            }
            else {
                tvListFavAr.setTextColor(ContextCompat.getColor(requireContext(), superappR.color.white))
                tvListFavEn.setTextColor(ContextCompat.getColor(requireContext(), superappR.color.white))
                tvListFavNum.setTextColor(ContextCompat.getColor(requireContext(), superappR.color.white))
                clVhReadSurah.setBackgroundColor(ContextCompat.getColor(requireContext(), superappR.color.black))
            }
        }
    }

    private val adapterContent = fun(vhBinding: ListReadSurahBinding){
        vhBinding.apply {
            when(sharedPrefUtil.getReadSurahContentType()){
                1 -> {
                    tvListFavAr.isVisible = true
                    tvListFavEn.isVisible = true
                    tvListFavNum.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 1.0F }
                }
                2 -> {
                    tvListFavAr.isVisible = true
                    tvListFavEn.isVisible = false
                    tvListFavNum.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 0.0F }
                }
                3 -> {
                    tvListFavAr.isVisible = false
                    tvListFavEn.isVisible = true
                    tvListFavNum.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 1.0F }
                }
            }
        }
    }

    private val adapterArTextSize = fun(vhBinding: ListReadSurahBinding){
        vhBinding.apply {
            when(sharedPrefUtil.getReadSurahArTextSize()){
                1 -> {
                    tvListFavAr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
                }
                2 -> {
                    tvListFavAr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38F)
                }
                3 -> {
                    tvListFavAr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 46F)
                }
            }
        }
    }

    private val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val item = readSurahAdapter.listAyah[viewHolder.layoutPosition]
            sharedPrefUtil.insertLastReadSharedPref(args.selectedSurahId.toInt(), item.numberInSurah)
            Toasty.success(
                requireContext(),
                "Surah ${args.selectedSurahId} ayah ${item.numberInSurah} is now your last read",
                Toasty.LENGTH_LONG
            ).show()
            viewModel.getSelectedSurah(args.selectedSurahId.toInt())
        }

        override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                 dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            val background =  ColorDrawable(ContextCompat.getColor(requireContext(), superappR.color.purple_500))
            background.apply {
                setBounds(
                    viewHolder.itemView.right, viewHolder.itemView.top,
                    viewHolder.itemView.left, viewHolder.itemView.bottom
                )
                draw(canvas)
            }
            val icon = ContextCompat.getDrawable(requireContext(), superappR.drawable.ic_baseline_check_24) ?: return
            val iconMargin = 50
            val iconTop = viewHolder.itemView.top + (viewHolder.itemView.height - icon.intrinsicHeight) / 2
            val iconBottom: Int = iconTop + icon.intrinsicHeight
            when {
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