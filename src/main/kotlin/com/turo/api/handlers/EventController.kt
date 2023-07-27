package com.turo.api.handlers

import com.turo.ApplicationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/events")
class EventController(private val eventService: ApplicationService) {
    @PostMapping("/publish")
    fun publishPendingEvents(): ResponseEntity<String> {
        eventService.publishDomainEvents()
        return ResponseEntity.ok("Published pending events")
    }
}