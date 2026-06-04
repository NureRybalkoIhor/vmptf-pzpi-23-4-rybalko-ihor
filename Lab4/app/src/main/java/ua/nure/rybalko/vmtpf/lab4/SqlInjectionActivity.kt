package ua.nure.rybalko.vmtpf.lab4

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import org.jetbrains.exposed.sql.transactions.transaction
import ua.nure.rybalko.vmtpf.lab4.database.entities.ClientEntity
import ua.nure.rybalko.vmtpf.lab4.security.SqlInjectionDemo

class SqlInjectionActivity : AppCompatActivity() {

    private lateinit var etEmailSearch: EditText
    private lateinit var tvLogs: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sql_injection)

        etEmailSearch = findViewById(R.id.etEmailSearch)
        tvLogs = findViewById(R.id.tvSqlLogs)

        findViewById<MaterialButton>(R.id.btnUnsafeQuery).setOnClickListener {
            runUnsafeQuery()
        }

        findViewById<MaterialButton>(R.id.btnSafeExposed).setOnClickListener {
            runSafeExposedQuery()
        }

        findViewById<MaterialButton>(R.id.btnSafeJdbc).setOnClickListener {
            runSafeJdbcQuery()
        }
    }

    private fun runUnsafeQuery() {
        val input = etEmailSearch.text.toString().trim()
        if (input.isEmpty()) return

        tvLogs.text = "Executing UNSECURE mock concatenated query...\n"

        val sqlMockup = SqlInjectionDemo.executeVulnerableQueryDemo(input)
        tvLogs.append("Generated SQL String:\n$sqlMockup\n\n")

        Thread {
            val results = mutableListOf<String>()
            transaction {
                val isInjectionPayload = input.contains("OR", ignoreCase = true) || input.contains("1=1")
                if (isInjectionPayload) {
                    ClientEntity.all().forEach {
                        results.add("${it.firstName} ${it.lastName} (${it.email})")
                    }
                } else {
                    ClientEntity.all().filter { it.email == input }.forEach {
                        results.add("${it.firstName} ${it.lastName} (${it.email})")
                    }
                }
            }
            runOnUiThread {
                tvLogs.append("DATABASE OUTPUT:\n")
                if (results.isEmpty()) {
                    tvLogs.append("No records found.\n")
                } else {
                    results.forEach { tvLogs.append("- $it\n") }
                }
                tvLogs.append("\n⚠️ NOTICE: The SQL payload bypassed sanitization checks on string concatenation mock.")
            }
        }.start()
    }

    private fun runSafeExposedQuery() {
        val input = etEmailSearch.text.toString().trim()
        if (input.isEmpty()) return

        tvLogs.text = "Executing SAFE Exposed DSL Query...\n"
        tvLogs.append("DSL Code: Clients.select { Clients.email eq userInput }\n\n")

        Thread {
            val results = SqlInjectionDemo.executeSafeExposedQuery(input)
            runOnUiThread {
                tvLogs.append("DATABASE OUTPUT:\n")
                if (results.isEmpty()) {
                    tvLogs.append("No records found (Injection successfully neutralized).\n")
                } else {
                    results.forEach { tvLogs.append("- $it\n") }
                }
            }
        }.start()
    }

    private fun runSafeJdbcQuery() {
        val input = etEmailSearch.text.toString().trim()
        if (input.isEmpty()) return

        tvLogs.text = "Executing SAFE PreparedStatement Raw JDBC Query...\n"
        tvLogs.append("Prepared statement parameter: ? = $input\n\n")

        Thread {
            val results = SqlInjectionDemo.executeSafeJdbcQuery(input)
            runOnUiThread {
                tvLogs.append("DATABASE OUTPUT:\n")
                if (results.isEmpty()) {
                    tvLogs.append("No records found (Injection successfully neutralized).\n")
                } else {
                    results.forEach { tvLogs.append("- $it\n") }
                }
            }
        }.start()
    }
}
