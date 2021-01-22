package com.programmergabut.solatkuy.data.remote

import com.programmergabut.solatkuy.util.DebugUtil.Companion.execute
import com.programmergabut.solatkuy.data.remote.api.AllSurahService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahArService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahEnService
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import javax.inject.Inject

class RemoteDataSourceApiAlquranImpl @Inject constructor(
    private val readSurahEnService: ReadSurahEnService,
    private val allSurahService: AllSurahService,
    private val readSurahArService: ReadSurahArService
): RemoteDataSourceApiAlquran{

    override fun fetchReadSurahEn(surahID: Int): ReadSurahEnResponse {
        return execute(readSurahEnService.fetchReadSurahEn(surahID))
    }

    override fun fetchAllSurah(): AllSurahResponse {
        return execute(allSurahService.fetchAllSurah())
    }

    override fun fetchReadSurahAr(surahID: Int): ReadSurahArResponse {
        return execute(readSurahArService.fetchReadSurahAr(surahID))
    }

}