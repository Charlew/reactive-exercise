package com.charlew.reactive_exercise

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration

@RunWith(SpringRunner::class)
@WebFluxTest(CoffeeService::class)
class InternalAPITest {

    @Autowired
    lateinit var service: CoffeeService

    @MockBean
    lateinit var repository: CoffeeRepository

    private fun sampleCoffees(): Map<Int, Coffee> {
        val map = HashMap<Int, Coffee>()

        map[1] = Coffee("1-TEST", "Americano")
        map[2] = Coffee("2-TEST", "Cappuccino")

        return map
    }

    @Before
    fun setUp() {
        Mockito.`when`(repository.findAll()).thenReturn(Flux.just(sampleCoffees()[1], sampleCoffees()[2]))
        Mockito.`when`(repository.findById(sampleCoffees()[1]!!.id!!)).thenReturn(Mono.just(sampleCoffees()[1]!!))
        Mockito.`when`(repository.findById(sampleCoffees()[2]!!.id!!)).thenReturn(Mono.just(sampleCoffees()[2]!!))
    }

    @Test
    fun getAllCoffees() {
    }

    @Test
    fun getCoffeeById() {
    }

    @Test
    fun `get orders for coffee by id`() {
        StepVerifier.withVirtualTime { service.getOrdersForCoffeeById(sampleCoffees()[1]!!.id!!).take(10) }
                .thenAwait(Duration.ofHours(10))
                .expectNextCount(10)
                .verifyComplete()
    }
}
