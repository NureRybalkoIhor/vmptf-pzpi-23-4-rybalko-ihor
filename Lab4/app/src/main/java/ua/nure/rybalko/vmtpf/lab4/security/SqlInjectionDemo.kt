package ua.nure.rybalko.vmtpf.lab4.security

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ua.nure.rybalko.vmtpf.lab4.database.tables.Clients
import java.sql.Connection

object SqlInjectionDemo {
    fun executeVulnerableQueryDemo(userInput: String): String {
        return "SELECT * FROM clients WHERE email = '$userInput'"
    }

    fun executeSafeExposedQuery(userInput: String): List<String> = transaction {
        val query = Clients.select { Clients.email eq userInput }
        query.map { "${it[Clients.firstName]} ${it[Clients.lastName]} (${it[Clients.email]})" }
    }

    fun executeSafeJdbcQuery(userInput: String): List<String> = transaction {
        val connection = this.connection.connection as Connection
        val statement = connection.prepareStatement("SELECT first_name, last_name, email FROM clients WHERE email = ?")
        statement.setString(1, userInput)
        val resultSet = statement.executeQuery()
        val results = mutableListOf<String>()
        while (resultSet.next()) {
            results.add("${resultSet.getString("first_name")} ${resultSet.getString("last_name")} (${resultSet.getString("email")})")
        }
        results
    }
}
