package tk.vhhg.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tk.vhhg.data.room.RoomRepository
import tk.vhhg.data.room.RoomRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindRoomRepo(roomRepo: RoomRepositoryImpl): RoomRepository
}