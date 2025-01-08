package org.example.fr.sg.kata.controller.dto

import java.time.LocalDate

data class AccountTransactionApiDto(val amount: Float, val date: LocalDate, val balance: Float)