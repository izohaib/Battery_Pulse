package com.example.battery_pulse.core.utils

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TorchHelper {
    private val _isOn = MutableStateFlow(false)
    val isFlashOn: StateFlow<Boolean> = _isOn.asStateFlow()

    private var cameraId: String? = null

    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(id: String, enabled: Boolean) {
            if (id == cameraId) _isOn.value = enabled
        }
        override fun onTorchModeUnavailable(id: String) {
            if (id == cameraId) _isOn.value = false
        }
    }

    fun register(context: Context) {
        val cm = getSystemService(context, CameraManager::class.java) ?: return
        cameraId = cm.cameraIdList[0]
        cm.registerTorchCallback(torchCallback, null)
    }

    fun toggle(context: Context) {
        val cm = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val id = cameraId ?: cm.cameraIdList[0]
        val newState = !_isOn.value
        _isOn.value = newState          // update state immediately
        cm.setTorchMode(id, newState)   // apply to hardware
    }

    fun turnOff(context: Context) {
        if (!_isOn.value) return
        val cm = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val id = cameraId ?: cm.cameraIdList[0]
        cm.setTorchMode(id, false)
        _isOn.value = false
    }

    fun unregister(context: Context) {
        val cm = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cm.unregisterTorchCallback(torchCallback)
    }


}

//object TorchHelper {
//    private var isOn = false
//
//    fun toggle(context: Context) {
//        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//        val cameraId = cameraManager.cameraIdList[0]
//        isOn = !isOn
//        cameraManager.setTorchMode(cameraId, isOn)
//    }
//
//    fun turnOff(context: Context) {
//        if (isOn) {
//            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//            val cameraId = cameraManager.cameraIdList[0]
//            cameraManager.setTorchMode(cameraId, false)
//            isOn = false
//        }
//    }
//}