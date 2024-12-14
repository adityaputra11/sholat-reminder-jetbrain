package com.adityaputra.sholatreminder.helper

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val currentDateTime = LocalDateTime.now()
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
val formattedDateTime = currentDateTime.format(formatter)