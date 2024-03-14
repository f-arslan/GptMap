package espressodev.gptmap.plugins

import espressodev.gptmap.model.token.TokenTransactions
import espressodev.gptmap.model.token.UserTokens
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase(config: ApplicationConfig) {
    val driverClassName = config.property("storage.driverClassName").getString()
    val jdbcURL = config.property("storage.jdbcURL").getString()
    val database = Database.connect(jdbcURL, driverClassName)
    transaction(database) {
        SchemaUtils.create(UserTokens)
        SchemaUtils.create(TokenTransactions)
    }
}
