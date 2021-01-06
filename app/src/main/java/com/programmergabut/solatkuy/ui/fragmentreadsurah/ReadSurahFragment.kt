package com.programmergabut.solatkuy.ui.fragmentreadsurah

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseFragment
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Data
import com.programmergabut.solatkuy.databinding.FragmentReadSurahBinding
import com.programmergabut.solatkuy.databinding.ListReadSurahBinding
import com.programmergabut.solatkuy.ui.MainActivity
import com.programmergabut.solatkuy.util.EnumStatus
import com.programmergabut.solatkuy.util.LogConfig.Companion.ERROR
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty


@AndroidEntryPoint
class ReadSurahFragment(viewModelTest: ReadSurahViewModel? = null) : BaseFragment<FragmentReadSurahBinding, ReadSurahViewModel>(
    R.layout.fragment_read_surah,
    ReadSurahViewModel::class.java,
    viewModelTest
), View.OnClickListener {

    private val args: ReadSurahFragmentArgs by navArgs()
    private lateinit var readSurahAdapter: ReadSurahAdapter
    private lateinit var selectedSurahId: String
    private lateinit var selectedSurahName: String
    private lateinit var selectedTranslation: String
    private var isAutoScroll = false
    private var isFirstLoad = true
    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setIntentExtra()
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setIntentExtra() {
        try{
            selectedSurahId = args.selectedSurahId
            selectedSurahName = args.selectedSurahName
            selectedTranslation = args.selectedTranslation
            isAutoScroll = args.isAutoScroll
        }
        catch (ex: Exception){
            Log.d(ERROR, ex.message.toString())
            showBottomSheet(resources.getString(R.string.text_error_title), "", isCancelable = false, isFinish = true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFirstView()
        viewModel.fetchReadSurahAr(selectedSurahId.toInt())
    }

    private fun setFirstView() {
        (activity as MainActivity).setSupportActionBar(binding.tbReadSurah)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRVReadSurah()
        setTheme(getIsBrightnessActive())
    }

    override fun setListener() {
        super.setListener()
        binding.fabBrightness.setOnClickListener(this)

        observeApi()
        observeDB()
    }

    private fun observeApi(){
        viewModel.selectedSurahAr.observe(this, {
            when (it.status) {
                EnumStatus.SUCCESS -> {
                    if (it.data?.data == null)
                        showBottomSheet(isCancelable = false, isFinish = true)

                    if (!checkLastSurahAndAyah()){
                        showBottomSheet(resources.getString(R.string.text_error_title),
                            "Last surah and ayah not found", isCancelable = false, isFinish = true)
                    }

                    setVisibility(it.status)
                    setToolBarText(it.data?.data!!)
                    viewModel.getListFavAyahBySurahID(selectedSurahId.toInt(), selectedSurahId.toInt(), getLastReadSurah(), getLastReadAyah())

                    if (isFirstLoad) {
                        Toast.makeText(requireContext(), "Swipe left to save your last read ayah", Toast.LENGTH_SHORT).show()
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
    }

    private fun observeDB(){
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
                else -> {/*NO-OP*/
                }
            }
        })

        viewModel.msFavSurah.observe(this, {
            if (it == null)
                menu?.findItem(R.id.i_star_surah)?.icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_24)
            else
                menu?.findItem(R.id.i_star_surah)?.icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_purple_24)
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fab_brightness -> {
                if (!getIsBrightnessActive()) {
                    setIsBrightnessActive(true)
                    setTheme(true)
                } else {
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
        }
        else{
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
                    R.color.dark_700
                )
            )
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.read_surah_menu, menu)

        viewModel.getFavSurahBySurahID(selectedSurahId.toInt())
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
                    selectedSurahId.toInt(),
                    selectedSurahName,
                    selectedTranslation
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

                viewModel.getFavSurahBySurahID(selectedSurahId.toInt())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initRVReadSurah() {
        readSurahAdapter = ReadSurahAdapter(
            onClickFavAyah,
            adapterTheme,
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_red_24)!!,
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_24)!!,
            ContextCompat.getColor(requireContext(), R.color.purple_500)
        )

        binding.rvReadSurah.apply {
            adapter = readSurahAdapter
            layoutManager = LinearLayoutManager(requireContext())
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

        if (binding.ivListFavFav.drawable.constantState ==
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_red_24)?.constantState) {
            Toasty.info(requireContext(), "Unsaving the ayah", Toast.LENGTH_SHORT).show()
            viewModel.deleteFavAyah(msFavAyah)
            binding.ivListFavFav.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favorite_24
                )
            )
        }
        else {
            Toasty.info(requireContext(), "Saving the ayah", Toast.LENGTH_SHORT).show()
            viewModel.insertFavAyah(msFavAyah)
            binding.ivListFavFav.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_favorite_red_24
                )
            )
        }
        viewModel.fetchReadSurahAr(selectedSurahId.toInt())
    }

    private val adapterTheme = fun(binding: ListReadSurahBinding){
        if(getIsBrightnessActive()){
            binding.tvListFavAr.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            binding.tvListFavEn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            binding.tvListFavNum.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            binding.clVhReadSurah.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        }
        else {
            binding.tvListFavAr.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.tvListFavEn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.tvListFavNum.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.clVhReadSurah.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
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
                requireContext(),
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
                    requireContext(),
                    R.color.purple_500
                )
            )
            background.setBounds(
                viewHolder.itemView.right, viewHolder.itemView.top,
                viewHolder.itemView.left, viewHolder.itemView.bottom
            )
            background.draw(canvas)

            val icon = ContextCompat.getDrawable(
                requireContext(),
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