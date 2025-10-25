package com.example.yachayfood.ui.view.escanear_producto

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.yachayfood.data.database.AppDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EscanearProductoView : AppCompatActivity() {

    private lateinit var binding: ActivityEscanearProductoBinding
    private lateinit var barcodeView: DecoratedBarcodeView

    private val viewModel: EscanearProductoViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return EscanearProductoViewModel(AppDatabase.getInstance(this@EscanearProductoView)) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEscanearProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barcodeView = binding.barcodeScannerView
        barcodeView.decodeContinuous(callback)

        setupObservers()
        setupButtons()
    }

    private fun setupObservers() {
        viewModel.mensaje.observe(this) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.producto.observe(this) { producto ->
            producto?.let {
                val intent = Intent(this, com.example.yachayfood.ui.view.detalle_producto.DetalleProductoView::class.java)
                intent.putExtra("producto", it)
                startActivity(intent)
            }
        }
    }

    private fun setupButtons() {
        binding.btnEscanear.setOnClickListener { checkCameraPermission() }

        binding.btnSubirImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.btnIngresarCodigo.setOnClickListener { mostrarDialogoCodigoManual() }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            barcodeView.resume()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }
    }

    override fun onPause() { super.onPause(); barcodeView.pause() }
    override fun onResume() { super.onResume(); barcodeView.resume() }

    private val callback = BarcodeCallback { result: BarcodeResult ->
        result.text?.let {
            barcodeView.pause()
            viewModel.buscarProductoPorCodigo(it)
        }
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { scanFromImage(it) }
        }
    }

    private fun scanFromImage(imageUri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val pixels = IntArray(bitmap.width * bitmap.height)
                bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                val source = RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

                val hints = mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(
                    BarcodeFormat.EAN_13, BarcodeFormat.UPC_A, BarcodeFormat.CODE_128
                ))

                val reader = MultiFormatReader().apply { setHints(hints) }
                val result = reader.decode(binaryBitmap)

                viewModel.buscarProductoPorCodigo(result.text)
            } catch (e: Exception) {
                runOnUiThread {
                    Snackbar.make(binding.root, "No se detectó ningún código", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarDialogoCodigoManual() {
        val editText = EditText(this)
        editText.hint = "Ingrese código"

        AlertDialog.Builder(this)
            .setTitle("Buscar producto")
            .setView(editText)
            .setPositiveButton("Buscar") { dialog, _ ->
                val codigo = editText.text.toString()
                if (codigo.isNotEmpty()) {
                    viewModel.buscarProductoPorCodigo(codigo)
                } else {
                    Snackbar.make(binding.root, "Código vacío", Snackbar.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
