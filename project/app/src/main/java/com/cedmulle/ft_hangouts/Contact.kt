package com.cedmulle.ft_hangouts

data class Contact(
    var id: Int = -1,
    var firstName: String,
    var lastName: String,
    var phone: String,
    var email: String,
    var address: String,
    var photoUri: String? = null
)
