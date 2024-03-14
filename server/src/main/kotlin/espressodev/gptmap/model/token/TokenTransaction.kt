package espressodev.gptmap.model.token

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

data class TokenTransaction(
    val transactionId: Int,
    val userId: Int,
    val tokenType: String,
    val tokenAmount: Int,
    val associatedId: String,
    val transactionTime: Instant
)

object TokenTransactions : Table() {
    val transactionId = integer("transactionId").autoIncrement()
    val userId = integer("userId").references(UserTokens.userId)
    val tokenType = varchar("tokenType", 128)
    val tokenAmount = integer("tokenAmount")
    val associatedId = varchar("associatedId", 128)
    val transactionTime = timestamp("transactionTime")

    override val primaryKey = PrimaryKey(transactionId)
}

