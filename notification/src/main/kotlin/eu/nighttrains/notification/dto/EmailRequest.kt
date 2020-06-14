package eu.nighttrains.notification.dto

data class EmailRequest(val recipientAddress: String, val subject: String, val text: String)
