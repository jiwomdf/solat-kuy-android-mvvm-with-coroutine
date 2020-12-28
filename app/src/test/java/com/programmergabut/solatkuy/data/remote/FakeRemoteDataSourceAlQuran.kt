package com.programmergabut.solatkuy.data.remote

import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquran
import com.programmergabut.solatkuy.data.remote.api.AllSurahService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahArService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahEnService
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse

class FakeRemoteDataSourceAlQuran constructor(
    private val readSurahEnService: ReadSurahEnService,
    private val allSurahService: AllSurahService,
    private val readSurahArService: ReadSurahArService
): RemoteDataSourceApiAlquran {

    override suspend fun fetchReadSurahEn(surahID: Int): ReadSurahEnResponse {
        return readSurahEnService.fetchReadSurahEn(surahID)
    }

    override suspend fun fetchAllSurah(): AllSurahResponse {
        return allSurahService.fetchAllSurah()
    }

    override suspend fun fetchReadSurahAr(surahID: Int): ReadSurahArResponse {
        return readSurahArService.fetchReadSurahAr(surahID)
    }
}