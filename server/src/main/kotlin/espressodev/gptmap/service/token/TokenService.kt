package espressodev.gptmap.service.token

import espressodev.gptmap.model.token.TokenTransaction
import espressodev.gptmap.model.token.UserToken

interface TokenService {
    suspend fun getUserToken(userId: Int): UserToken?
    suspend fun createInitialUserToken(
        userId: Int,
        imageTokens: Int = 10,
        searchTokens: Int = 5,
        unsplashTokens: Int = 10
    ): UserToken?

    suspend fun addUserToken(
        userId: Int,
        imageTokens: Int = 0,
        searchTokens: Int = 0,
        unsplashTokens: Int = 0
    ): UserToken?

    suspend fun updateUserToken(
        userId: Int,
        imageTokens: Int = 0,
        searchTokens: Int = 0,
        unsplashTokens: Int = 0
    ): Boolean

    suspend fun removeTokenFromUser(
        userId: Int,
        imageTokens: Int = 0,
        searchTokens: Int = 0,
        unsplashTokens: Int = 0
    ): Boolean

    suspend fun getTokenTransactions(userId: Int): List<TokenTransaction>
}
