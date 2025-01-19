package io.kskim.awskmsexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties
@SpringBootApplication
class AwsKmsExampleApplication

fun main(args: Array<String>) {
    runApplication<AwsKmsExampleApplication>(*args)
}
