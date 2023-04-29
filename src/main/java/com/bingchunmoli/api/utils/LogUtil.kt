package com.bingchunmoli.api.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

object LogUtil {
    private val logger: ConcurrentMap<Class<*>, Logger> = ConcurrentHashMap()

    val Any.log: Logger
        get() {
            return logger.getOrPut(this::class.java) {
                LoggerFactory.getLogger(this::class.java)
            }
        }

    fun trace(str: String, vararg arg: Any?){
        if (log.isTraceEnabled) {
            log.trace(str, arg)
        }
    }

    fun debug(str: String, vararg arg: Any?){
        if (log.isDebugEnabled) {
            log.debug(str, arg)
        }
    }

    fun info(str: String, vararg arg: Any?){
        if (log.isInfoEnabled) {
            log.info(str, arg)
        }
    }

    fun warn(str: String, vararg arg: Any?){
        if (log.isErrorEnabled) {
            log.warn(str, arg)
        }
    }

    fun error(str: String, vararg arg: Any?){
        if (log.isErrorEnabled) {
            log.error(str, arg)
        }
    }

}