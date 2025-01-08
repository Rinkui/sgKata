package org.example.fr.sg.kata.service

import org.example.fr.sg.kata.model.AccountTransaction
import org.springframework.stereotype.Service

@Service
class BankAccountService(private val dateService: DateService) {
    private val transactions = mutableListOf<AccountTransaction>()

    fun getAccountHistory() = transactions
    fun deposeMoney(amount: Float) {
        if (amount <= 0) {
            throw IllegalArgumentException("Deposit could not be negative or zero")
        }
        transactions.add(
            AccountTransaction(
                amount = amount,
                date = dateService.now(),
                balance = lastBalance() + amount
            )
        )
    }

    fun retrieveMoney(amount: Float?): AccountTransaction {
        if (lastBalance() < 0) {
            throw IllegalArgumentException("Could not retrieve money if balance is negative")
        }
        val effectiveAmount = amount ?: lastBalance()

        val transaction = AccountTransaction(
            amount = -effectiveAmount,
            date = dateService.now(),
            balance = lastBalance() - effectiveAmount
        )
        transactions.add(transaction)
        return transaction
    }

    private fun lastBalance(): Float {
        return if (transactions.isEmpty()) 0f else transactions.last().balance
    }
}