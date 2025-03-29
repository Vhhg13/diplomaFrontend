package tk.vhhg.im.data

interface ImRepository {
    suspend fun putIm(im: String)
    suspend fun getIms(): List<String>
    suspend fun deleteIm(id: Int): Boolean
}