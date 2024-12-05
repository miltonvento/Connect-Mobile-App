package hu.ait.connect

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import hu.ait.connect.ui.screen.category.CategoryViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class MainApplication : Application() {
}