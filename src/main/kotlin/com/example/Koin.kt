package com.example

import com.example.repositories.CustomerRepository
import org.koin.dsl.module

val repositoryKoinModule = module {
    single { CustomerRepository() }
}