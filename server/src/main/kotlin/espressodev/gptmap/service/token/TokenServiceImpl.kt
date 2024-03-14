package espressodev.gptmap.service.token

import espressodev.gptmap.model.token.TokenTransaction
import espressodev.gptmap.model.token.TokenTransactions
import espressodev.gptmap.model.token.UserToken
import espressodev.gptmap.model.token.UserTokens
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class TokenServiceImpl : TokenService {
    override suspend fun getUserToken(userId: Int): UserToken? = dbQuery {
        UserTokens.select { UserTokens.userId eq userId }
            .map(::resultRowToUserToken)
            .singleOrNull()
    }

    override suspend fun createInitialUserToken(
        userId: Int,
        imageTokens: Int,
        searchTokens: Int,
        unsplashTokens: Int
    ): UserToken? = dbQuery {
        val insertStatement = UserTokens.insert {
            it[UserTokens.userId] = userId
            it[UserTokens.imageTokens] = imageTokens
            it[UserTokens.searchTokens] = searchTokens
            it[UserTokens.unsplashTokens] = unsplashTokens
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUserToken)
    }

    override suspend fun addUserToken(
        userId: Int,
        imageTokens: Int,
        searchTokens: Int,
        unsplashTokens: Int
    ): UserToken? = dbQuery {
        UserTokens.update({ UserTokens.userId eq userId }) {
            with(SqlExpressionBuilder) {
                it.update(UserTokens.imageTokens, UserTokens.imageTokens + imageTokens)
                it.update(UserTokens.searchTokens, UserTokens.searchTokens + searchTokens)
                it.update(UserTokens.unsplashTokens, UserTokens.unsplashTokens + unsplashTokens)
            }
        } > 0
        UserTokens.select { UserTokens.userId eq userId }.singleOrNull()?.let(::resultRowToUserToken)
    }

    override suspend fun updateUserToken(
        userId: Int,
        imageTokens: Int,
        searchTokens: Int,
        unsplashTokens: Int
    ): Boolean = dbQuery {
        UserTokens.update({ UserTokens.userId eq userId }) {
            it[UserTokens.imageTokens] = imageTokens
            it[UserTokens.searchTokens] = searchTokens
            it[UserTokens.unsplashTokens] = unsplashTokens
        } > 0
    }

    override suspend fun removeTokenFromUser(
        userId: Int,
        imageTokens: Int,
        searchTokens: Int,
        unsplashTokens: Int
    ): Boolean = dbQuery {
        UserTokens.update({ UserTokens.userId eq userId }) {
            with(SqlExpressionBuilder) {
                it.update(UserTokens.imageTokens, UserTokens.imageTokens - imageTokens)
                it.update(UserTokens.searchTokens, UserTokens.searchTokens - searchTokens)
                it.update(UserTokens.unsplashTokens, UserTokens.unsplashTokens - unsplashTokens)
            }
        } > 0
    }

    override suspend fun getTokenTransactions(userId: Int): List<TokenTransaction> = dbQuery {
        TokenTransactions.select { TokenTransactions.userId eq userId }
            .map(::resultRowToTokenTransaction)
    }

    private fun resultRowToUserToken(row: ResultRow) = UserToken(
        userId = row[UserTokens.userId],
        imageTokens = row[UserTokens.imageTokens],
        searchTokens = row[UserTokens.searchTokens],
        unsplashTokens = row[UserTokens.unsplashTokens],
        lastUpdated = row[UserTokens.lastUpdated]
    )

    private fun resultRowToTokenTransaction(row: ResultRow) = TokenTransaction(
        transactionId = row[TokenTransactions.transactionId],
        userId = row[TokenTransactions.userId],
        tokenType = row[TokenTransactions.tokenType],
        tokenAmount = row[TokenTransactions.tokenAmount],
        associatedId = row[TokenTransactions.associatedId],
        transactionTime = row[TokenTransactions.transactionTime]
    )

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
