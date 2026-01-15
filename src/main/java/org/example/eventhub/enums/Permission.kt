package org.example.eventhub.enums

enum class Permission {
    USER_READ,
    USER_UPDATE,
    USER_DELETE,

    EVENT_READ,
    EVENT_CREATE,
    EVENT_UPDATE,
    EVENT_DELETE,

    ORDER_CREATE,
    ORDER_READ,
    ORDER_CANCEL,
    ORDER_PAY
}
