package com.charlew.reactive_exercise

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import java.time.Duration
import java.time.Instant
import javax.annotation.PostConstruct

@SpringBootApplication
class ReactiveExerciseApplication

fun main(args: Array<String>) {
	runApplication<ReactiveExerciseApplication>(*args)
}

@Service
class CoffeeService(private val repository: CoffeeRepository) {
	fun getAllCoffees(): Flux<Coffee> = repository.findAll()

	fun getCoffeeById(id: String): Mono<Coffee> = repository.findById(id)

	fun getOrdersForCoffeeById(coffeeId: String): Flux<CoffeeOrder> = Flux.interval(Duration.ofSeconds(1))
		.onBackpressureDrop()
		.map { CoffeeOrder(coffeeId, Instant.now()) }
}

@Component
class DataLoader(private val repository: CoffeeRepository) {
	@PostConstruct
	fun load(): Disposable =
		repository.deleteAll().thenMany(
			listOf("Cappuccino", "Double espresso", "Sypana", "Rozpuszczalna", "Americano")
				.toFlux()
				.map { Coffee(name = it) }
				.flatMap { repository.save(it) }
		)
			.thenMany(repository.findAll())
			.subscribe { println(it) }
}

interface CoffeeRepository:ReactiveCrudRepository<Coffee, String>

data class CoffeeOrder(val coffeeId: String, val whenOrdered: Instant)

@Document
data class Coffee(@Id val id: String? = null, val name: String = "John")