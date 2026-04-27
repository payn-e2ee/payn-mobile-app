package com.example.payn.core.data.mappers

import com.example.payn.core.data.dto.DeviceDTO
import com.example.payn.core.domain.models.Device

fun DeviceDTO.toDevice(): Device {
    return Device(
        id = id,
        userId = userId,
        identityKey = identityKey,
        createdAt = createdAt,
    )
}