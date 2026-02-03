package ru.vasilev.calorie_calculator.presentation.main_screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import ru.vasilev.domain.model.ProtocolType

@OptIn(ExperimentalMaterial3Api::class) // Для SegmentedButton
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> bitmap?.let { viewModel.onImageSelected(it) } }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.onImageSelected(it) } }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) cameraLauncher.launch(null)
        else Toast.makeText(context, "Нужен доступ к камере", Toast.LENGTH_SHORT).show()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Переключатель протоколов (ТЗ 2.4.1)
            Text("Выберите протокол:", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                ProtocolType.values().forEachIndexed { index, protocol ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = ProtocolType.values().size),
                        onClick = { viewModel.onProtocolChanged(protocol) },
                        selected = uiState.selectedProtocol == protocol,
                        enabled = !uiState.isLoading
                    ) {
                        Text(protocol.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = uiState.statusText,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (uiState.result != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.result ?: "",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { galleryLauncher.launch("image/*") },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Галерея")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) cameraLauncher.launch(null)
                    else permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Камера")
            }

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator()
            }
        }
    }
}