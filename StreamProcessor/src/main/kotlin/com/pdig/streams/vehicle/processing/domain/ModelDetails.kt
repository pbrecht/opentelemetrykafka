package com.pdig.streams.vehicle.processing.domain

import com.pdig.streams.vehicle.processing.domain.primitives.Model
import com.pdig.streams.vehicle.processing.domain.primitives.Variant

data class VehicleDetails(
    val model: Model,
    val variant: Variant
)
