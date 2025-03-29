package tk.vhhg.im.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tk.vhhg.im.data.ImRepository
import tk.vhhg.im.data.ImRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface ImModule {
    @Binds
    fun bindImRepo(imRepo: ImRepositoryImpl): ImRepository
}