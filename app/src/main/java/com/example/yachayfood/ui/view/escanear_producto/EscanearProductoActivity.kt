package com.example.yachayfood.ui.view.escanear_producto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.yachayfood.databinding.ActivityEscanearProductoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class EscanearProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEscanearProductoBinding
    private lateinit var barcodeView: DecoratedBarcodeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEscanearProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barcodeView = binding.barcodeScannerView
        barcodeView.decodeContinuous(callback)

        // Botón: Escanear con cámara
        binding.btnEscanear.setOnClickListener {
            checkCameraPermission()
        }

        // Botón: Subir imagen
        binding.btnSubirImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        // Botón: Ingresar código manualmente
        binding.btnIngresarCodigo.setOnClickListener {
            Snackbar.make(binding.root, "Abrir ingreso manual de código", Snackbar.LENGTH_SHORT).show()
        }
    }

    // Permiso de cámara
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            barcodeView.resume()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    // Callback del escaneo en vivo
    private val callback = BarcodeCallback { result: BarcodeResult ->
        result.text?.let {
            Snackbar.make(binding.root, "Código detectado: $it", Snackbar.LENGTH_LONG).show()
            barcodeView.pause()
        }
    }

    // Selector de imagen
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { scanFromImage(it) }
        }
    }

    // Escanear QR o código de barras desde imagen
    private fun scanFromImage(imageUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val intArray = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            // Detectar varios formatos (QR y códigos de barras 1D)
            val hints = mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(
                    BarcodeFormat.CODE_128,
                    BarcodeFormat.EAN_13,
                    BarcodeFormat.UPC_A,
                    BarcodeFormat.CODE_39,
                    BarcodeFormat.QR_CODE
                )
            )
            val reader = MultiFormatReader().apply { setHints(hints) }
            val result = reader.decode(binaryBitmap)

            Snackbar.make(binding.root, "Código detectado en imagen: ${result.text}", Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            Snackbar.make(binding.root, "No se detectó ningún código en la imagen", Snackbar.LENGTH_SHORT).show()
        }
    }
}