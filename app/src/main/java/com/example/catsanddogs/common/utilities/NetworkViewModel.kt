package com.example.catsanddogs.common.utilities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val connectivityObserver: ConnectivityObserver,
    application: Application
) : AndroidViewModel(application) {

    private val _networkStatus = MutableStateFlow<NetworkStatus>(NetworkStatus.Unavailable)
    val networkStatus = _networkStatus.asStateFlow()


    init {
        viewModelScope.launch {
            connectivityObserver.observeNetworkState().collect{ status ->
                _networkStatus.value = status
            }
        }
    }

}