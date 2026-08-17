package com.cedmulle.ft_hangouts

data class Message(
    var id: Int = -1,
    var contactId: Int,
    var content: String,
    var timestamp: Long,
    var isSent: Boolean
)
