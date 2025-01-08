package fr.sg.kata.utils

import org.example.fr.sg.kata.service.DateService
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.time.LocalDate

@Primary
@Service
class FakeDateService : DateService {
    override fun now() = LocalDate.parse("2025-01-07")
}