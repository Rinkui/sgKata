package org.example.fr.sg.kata.controller

import org.example.fr.sg.kata.controller.dto.AccountTransactionApiDto
import org.example.fr.sg.kata.controller.dto.AmountApiDto
import org.example.fr.sg.kata.controller.dto.HistoryAccountStatementApiDto
import org.example.fr.sg.kata.model.AccountTransaction
import org.example.fr.sg.kata.service.BankAccountService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/account")
class BankAccountController(
    private val bankAccountService: BankAccountService
) {
    @PostMapping("/money")
    fun deposeMoney(@RequestBody amount: AmountApiDto): ResponseEntity<String> {
        bankAccountService.deposeMoney(amount.amount)
        return ResponseEntity.accepted().build()
    }

    @GetMapping("/money")
    fun retrieveMoney(@RequestParam amount: Float?): ResponseEntity<AccountTransactionApiDto> {
        return ResponseEntity.ok(bankAccountService.retrieveMoney(amount).toAccountTransactionDto())
    }

    @GetMapping
    fun getAccountStatement(): ResponseEntity<HistoryAccountStatementApiDto> {
        return ResponseEntity.ok(bankAccountService.getAccountHistory().toHistoryDto())
    }

    private fun List<AccountTransaction>.toHistoryDto() =
        HistoryAccountStatementApiDto(map { it.toAccountTransactionDto() })

    private fun AccountTransaction.toAccountTransactionDto() = AccountTransactionApiDto(
        amount = amount,
        date = date,
        balance = balance
    )
}


