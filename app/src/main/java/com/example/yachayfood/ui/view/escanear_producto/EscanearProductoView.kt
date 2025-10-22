package com.example.yachayfood.ui.view.escanear_producto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.yachayfood.databinding.ActivityEscanearProductoBinding
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult

class EscanearProductoView : AppCompatActivity() {

    private lateinit var binding: ActivityEscanearProductoBinding
    private var isScanning = false

    // Configuración: solo códigos de barras 1D
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_CODE_39
        )
        .build()
    private val barcodeScannerML = BarcodeScanning.getClient(options)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEscanearProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEscanear.setOnClickListener { iniciarEscaneoEnVivo() }
        binding.btnSubirImagen.setOnClickListener { abrirGaleria() }
        binding.btnIngresarCodigo.setOnClickListener {
            Toast.makeText(this, "Función: ingresar código manual", Toast.LENGTH_SHORT).show()
        }
    }

    private fun iniciarEscaneoEnVivo() {
        if (isScanning) return
        isScanning = true
        binding.barcodeScannerView.resume()
        binding.barcodeScannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    isScanning = false
                    binding.barcodeScannerView.pause()
                    Toast.makeText(this@EscanearProductoView, "Código detectado: ${it.text}", Toast.LENGTH_LONG).show()
                }
            }
            override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {}
        })
    }

    override fun onResume() { super.onResume(); binding.barcodeScannerView.resume() }
    override fun onPause() { super.onPause(); binding.barcodeScannerView.pause() }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galeriaLauncher.launch(intent)
    }

    private val galeriaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                val image = InputImage.fromFilePath(this, it)
                escanearCodigoDesdeImagen(image)
            }
        }
    }

    private fun escanearCodigoDesdeImagen(image: InputImage) {
        barcodeScannerML.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val codigo = barcodes.first().rawValue ?: "Código no leído"
                    Toast.makeText(this, "Código detectado: $codigo", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "No se detectó ningún código en la imagen", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al escanear imagen: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
