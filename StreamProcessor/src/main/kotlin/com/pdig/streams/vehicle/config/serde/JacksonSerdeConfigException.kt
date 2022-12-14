package com.pdig.streams.vehicle.config.serde

import kotlin.reflect.KClass

class JacksonSerdeConfigException(clazz: KClass<*>, message: String) :
    RuntimeException("Wrong configuration of class ${clazz.simpleName}: $message")
