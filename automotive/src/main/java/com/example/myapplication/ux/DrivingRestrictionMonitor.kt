package com.example.myapplication.ux

import android.car.Car
import android.car.drivingstate.CarUxRestrictions
import android.car.drivingstate.CarUxRestrictionsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DrivingRestrictionMonitor(context: Context) {
    private val appContext = context.applicationContext
    private var car: Car? = null
    private var manager: CarUxRestrictionsManager? = null

    private val _restricted = MutableStateFlow(false)
    val restricted: StateFlow<Boolean> = _restricted.asStateFlow()

    fun start() {
        if (!appContext.packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
            _restricted.value = false
            return
        }
        runCatching {
            val created = Car.createCar(appContext) ?: return
            car = created
            val ux = created.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE) as? CarUxRestrictionsManager
            manager = ux
            ux?.let {
                _restricted.value = it.currentCarUxRestrictions.isRequiresDistractionOptimization
                it.registerListener { restrictions: CarUxRestrictions ->
                    _restricted.value = restrictions.isRequiresDistractionOptimization
                }
            }
        }.onFailure {
            Log.w("SagohanjangUx", "UX restriction unavailable", it)
            _restricted.value = false
        }
    }

    fun stop() {
        runCatching { manager?.unregisterListener() }
        runCatching { car?.disconnect() }
        manager = null
        car = null
    }
}
