package com.example.pm01p2026

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.pm01p2026.Configuracion.SQLiteConexion
import com.example.pm01p2026.Configuracion.Transacciones
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FormularioPersona(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun FormularioPersona(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    // Launchers para multimedia
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) Toast.makeText(context, "Foto guardada en la Galería", Toast.LENGTH_SHORT).show()
    }

    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        if (success) Toast.makeText(context, "Video guardado en la Galería", Toast.LENGTH_SHORT).show()
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.CAMERA] != true) {
            Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // botones multimedia
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (hasCameraPermission(context)) {
                        val uri = getPublicMediaUri(context, "image/jpeg", Environment.DIRECTORY_PICTURES, "jpg")
                        photoUri = uri
                        uri?.let { cameraLauncher.launch(it) }
                    } else {
                        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Tomar Foto") }

            Button(
                onClick = {
                    if (hasCameraPermission(context)) {
                        val uri = getPublicMediaUri(context, "video/mp4", Environment.DIRECTORY_MOVIES, "mp4")
                        videoUri = uri
                        uri?.let { videoLauncher.launch(it) }
                    } else {
                        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Grabar Video") }
        }

        // Campos de texto
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
        
        // Campo Edad
        OutlinedTextField(
            value = edad, 
            onValueChange = { if (it.all { char -> char.isDigit() }) edad = it }, 
            label = { Text("Edad") }, 
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Guardar
        Button(
            onClick = {
                if (validarCampos(nombre, apellido, edad, correo)) {
                    savePersonToDb(context, nombre, apellido, edad, correo, photoUri, videoUri) {
                        // Resetear campos
                        nombre = ""; apellido = ""; edad = ""; correo = ""
                        photoUri = null; videoUri = null
                    }
                } else {
                    Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Guardar Registro", style = MaterialTheme.typography.titleMedium)
        }
    }
}

//funciones y actividades
private fun hasCameraPermission(context: Context) = 
    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

private fun getPublicMediaUri(context: Context, mimeType: String, relativePath: String, extension: String): Uri? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "PM01_${timeStamp}.$extension")
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        }
    }
    
    val contentUri = if (extension == "jpg") MediaStore.Images.Media.EXTERNAL_CONTENT_URI 
                     else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                     
    return context.contentResolver.insert(contentUri, contentValues)
}

private fun validarCampos(n: String, a: String, e: String, c: String): Boolean {
    return n.isNotBlank() && a.isNotBlank() && e.isNotBlank() && c.isNotBlank()
}

private fun savePersonToDb(
    context: Context, n: String, a: String, e: String, c: String, 
    pUri: Uri?, vUri: Uri?, onSuccess: () -> Unit
) {
    try {
        val db = SQLiteConexion(context, Transacciones.dbname, null, Transacciones.dbversion).writableDatabase
        val values = ContentValues().apply {
            put(Transacciones.nombre, n)
            put(Transacciones.apellido, a)
            put(Transacciones.edad, e)
            put(Transacciones.correo, c)
            put(Transacciones.foto, pUri?.toString() ?: "")
        }
        
        val id = db.insert(Transacciones.tbpersons, null, values)
        if (id != -1L) {
            Toast.makeText(context, "Registro #$id guardado correctamente", Toast.LENGTH_LONG).show()
            onSuccess()
        }
        db.close()
    } catch (ex: Exception) {
        Toast.makeText(context, "Error al guardar: ${ex.message}", Toast.LENGTH_LONG).show()
    }
}
