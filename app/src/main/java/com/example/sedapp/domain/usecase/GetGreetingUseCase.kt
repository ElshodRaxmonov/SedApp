package com.example.sedapp.domain.usecase

import java.util.Calendar
import javax.inject.Inject

class GetGreetingUseCase @Inject constructor(){
    operator fun invoke(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): String {
        return when (hour) {
            in 0..11 -> "Good Morning!"
            in 12..16 -> "Good Afternoon!"
            else -> "Good Evening!"
        }
    }
}
