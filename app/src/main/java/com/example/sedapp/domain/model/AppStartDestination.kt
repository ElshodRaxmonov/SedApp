package com.example.sedapp.domain.model

sealed class  AppStartDestination {

    object Onboarding : AppStartDestination()

    object Auth : AppStartDestination()

    object Dashboard : AppStartDestination()

}


