package org.example.eventhub.enums

import org.example.eventhub.enums.Permission.*

enum class Role(
    val permissions: Set<Permission>,
) {

    USER(
        setOf(
            USER_READ,
            USER_UPDATE,
            USER_DELETE,

            EVENT_READ,

            ORDER_CREATE,
            ORDER_READ,
            ORDER_CANCEL,
            ORDER_PAY
        )
    ),

    ORGANIZER(
        setOf(
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
        )
    ),

    ADMIN(
        Permission.entries.toSet(),
    )
}