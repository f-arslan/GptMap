package espressodev.gptmap.model.token

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant


data class UserToken(
    val userId: Int,
    val imageTokens: Int,
    val searchTokens: Int,
    val unsplashTokens: Int,
    val lastUpdated: Instant
)

object UserTokens : Table() {
    val userId = integer("userId").autoIncrement()
    val imageTokens = integer("imageTokens")
    val searchTokens = integer("searchTokens")
    val unsplashTokens = integer("unsplashTokens")
    val lastUpdated = timestamp("lastUpdated").clientDefault { Instant.now() }

    override val primaryKey = PrimaryKey(userId)
}
