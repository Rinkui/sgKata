package org.example.fr.sg.kata.service

import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class KataDateService : DateService {
    override fun now() = LocalDate.now()
}