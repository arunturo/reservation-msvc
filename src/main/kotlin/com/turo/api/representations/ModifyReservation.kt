package com.turo.api.representations

import java.time.OffsetDateTime

data class ModifyReservation(
    val startDate: OffsetDateTime?,
    val endDate: OffsetDateTime?
)
