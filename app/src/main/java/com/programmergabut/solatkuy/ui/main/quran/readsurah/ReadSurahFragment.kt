package com.programmergabut.solatkuy.ui.main.quran.readsurah

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseFragment
import com.programmergabut.solatkuy.data.local.localentity.MsAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.databinding.FragmentReadSurahBinding
import com.programmergabut.solatkuy.databinding.ListReadSurahBinding
import com.programmergabut.solatkuy.util.EnumStatus
import es.dmoral.toasty.Toasty


class ReadSurahFragment(
    viewModelTest: ReadSurahViewModel? = null
) : BaseFragment<FragmentReadSurahBinding, ReadSurahViewModel>(
    R.layout.fragment_read_surah,
    ReadSurahViewModel::class,
    viewModelTest
), View.OnClickListener {

    private val args: ReadSurahFragmentArgs by navArgs()
    private lateinit var readSurahAdapter: ReadSurahAdapter
    private var isFirstLoad = true
    private var menu: Menu? = null

    override fun getViewBinding() = FragmentReadSurahBinding.inflate(layoutInflater)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFirstView()
        viewModel.getSelectedSurah(args.selectedSurahId.toInt())
    }

    private fun setFirstView() {
        setupToolbar()
        initRVReadSurah()
        setTheme(viewModel.sharedPrefUtil.getIsBrightnessActive())
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.tbReadSurah)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbReadSurah.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun setListener() {
        super.setListener()
        binding.fabBrightness.setOnClickListener(this)
        observeApi()
        observeDB()
    }

    private fun observeApi() {
        viewModel.selectedSurah.observe(this, {
            when (it.status) {
                EnumStatus.SUCCESS, EnumStatus.ERROR -> {
                    if (it.data != null) {
                        checkLastSurahAndAyah(it.data)

                        setVisibility(it.status)
                        setToolBarText(it.data)
                        readSurahAdapter.listAyah = it.data
                        readSurahAdapter.notifyDataSetChanged()

                        if (args.isAutoScroll) {
                            (binding.rvReadSurah.layoutManager as LinearLayoutManager)
                                .scrollToPositionWithOffset(
                                    viewModel.sharedPrefUtil.getLastReadAyah() - 1,
                                    0
                                )
                        }
                    }
                }
                EnumStatus.LOADING -> {
                    setVisibility(it.status)
                    binding.tbReadSurah.title = ""
                }
            }
        })
    }

    private fun observeDB() {
        viewModel.favSurahBySurahID.observe(this, {
            if (it == null) {
                menu?.findItem(R.id.i_star_surah)?.icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_24)
            } else {
                menu?.findItem(R.id.i_star_surah)?.icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_purple_24)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_brightness -> {
                if (!viewModel.sharedPrefUtil.getIsBrightnessActive()) {
                    viewModel.sharedPrefUtil.setIsBrightnessActive(true)
                    setTheme(true)
                } else {
                    viewModel.sharedPrefUtil.setIsBrightnessActive(false)
                    setTheme(false)
                }
                readSurahAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun checkLastSurahAndAyah(data: List<MsAyah>) {
        val lastSurah = viewModel.sharedPrefUtil.getLastReadSurah()
        val lastAyah = viewModel.sharedPrefUtil.getLastReadAyah()
        if (lastSurah == args.selectedSurahId.toInt()) {
            data[lastAyah - 1].isLastRead = true
        }
    }

    private fun setTheme(isBrightnessActive: Boolean) {
        if (isBrightnessActive) {
            binding.tbReadSurah.setTitleTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            binding.tbReadSurah.setSubtitleTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            binding.tbReadSurah.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.clSurah.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        } else {
            binding.tbReadSurah.setTitleTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.tbReadSurah.setSubtitleTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.tbReadSurah.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dark_200
                )
            )
            binding.clSurah.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dark_700
                )
            )
        }
    }

    private fun setToolBarText(data: List<MsAyah>) {
        binding.tbReadSurah.title = data.first().englishName
        binding.tbReadSurah.subtitle =
            data.first().revelationType + " - " + data.first().numberOfAyahs + " Ayahs"
    }

    private fun setVisibility(status: EnumStatus) {
        when (status) {
            EnumStatus.SUCCESS, EnumStatus.ERROR -> {
                binding.rvReadSurah.visibility = View.VISIBLE
                binding.abReadQuran.visibility = View.VISIBLE
                binding.ccReadQuranLoading.visibility = View.GONE
                binding.fabBrightness.visibility = View.VISIBLE
            }
            EnumStatus.LOADING -> {
                if (isFirstLoad) binding.ccReadQuranLoading.visibility = View.VISIBLE
                binding.abReadQuran.visibility = View.INVISIBLE
                binding.rvReadSurah.visibility = View.INVISIBLE
                binding.fabBrightness.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.read_surah_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.finish()
                true
            }
            R.id.i_star_surah -> {
                val data = MsFavSurah(
                    args.selectedSurahId.toInt(),
                    args.selectedSurahName,
                    args.selectedTranslation
                )
                if (menu?.findItem(R.id.i_star_surah)?.icon?.constantState ==
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_star_24
                    )?.constantState
                )
                    viewModel.insertFavSurah(data)
                else
                    viewModel.deleteFavSurah(data)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initRVReadSurah() {
        readSurahAdapter = ReadSurahAdapter(
            adapterTheme,
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_red_24)!!,
            ContextCompat.getColor(requireContext(), R.color.purple_500)
        )
        binding.rvReadSurah.apply {
            adapter = readSurahAdapter
            layoutManager = LinearLayoutManager(requireContext())
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
        }
    }


    private val adapterTheme = fun(vhBinding: ListReadSurahBinding) {
        if (viewModel.sharedPrefUtil.getIsBrightnessActive()) {
            vhBinding.tvListFavAr.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            vhBinding.tvListFavEn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            vhBinding.tvListFavNum.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            vhBinding.clVhReadSurah.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        } else {
            vhBinding.tvListFavAr.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            vhBinding.tvListFavEn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            vhBinding.tvListFavNum.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            vhBinding.clVhReadSurah.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dark_500
                )
            )
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
            viewModel.sharedPrefUtil.insertLastReadSharedPref(
                args.selectedSurahId.toInt(),
                item.numberInSurah
            )
            Toasty.success(
                requireContext(),
                "Surah ${args.selectedSurahId} ayah ${item.numberInSurah} is now your last read",
                Toasty.LENGTH_LONG
            ).show()
            viewModel.getSelectedSurah(args.selectedSurahId.toInt())
        }

        override fun onChildDraw(
            canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
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
            val background =
                ColorDrawable(ContextCompat.getColor(requireContext(), R.color.purple_500))
            background.apply {
                setBounds(
                    viewHolder.itemView.right, viewHolder.itemView.top,
                    viewHolder.itemView.left, viewHolder.itemView.bottom
                )
                draw(canvas)
            }
            val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_check_24)
                ?: return
            val iconMargin = 50
            val iconTop =
                viewHolder.itemView.top + (viewHolder.itemView.height - icon.intrinsicHeight) / 2
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