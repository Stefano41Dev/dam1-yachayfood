package com.example.yachayfood.ui.view.escanear_producto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.yachayfood.api.RetrofitClient
import com.example.yachayfood.databinding.ActivityEscanearProductoBinding
import com.example.yachayfood.models.Nutriente
import com.example.yachayfood.models.Producto
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
import kotlinx.coroutines.withContext
import kotlin.math.round
import com.example.yachayfood.data.local.AppDatabase
import com.example.yachayfood.data.local.NutrimentsEntity
import com.example.yachayfood.data.local.ProductoEntity

class EscanearProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEscanearProductoBinding
    private lateinit var barcodeView: DecoratedBarcodeView
    private val productoDao by lazy { AppDatabase.getInstance(this).productoDao() }

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
            val editText = EditText(this)
            editText.hint = "Ingrese código del producto"

            AlertDialog.Builder(this)
                .setTitle("Código de producto")
                .setView(editText)
                .setPositiveButton("Buscar") { dialog, _ ->
                    val codigo = editText.text.toString()
                    if (codigo.isNotEmpty()) {
                        abrirDetalleProducto(codigo)
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Ingrese un código válido",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    // Permiso de cámara
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
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
            barcodeView.pause()
            Snackbar.make(binding.root, "Código detectado: $it", Snackbar.LENGTH_LONG).show()
            abrirDetalleProducto(it)
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

            Snackbar.make(
                binding.root,
                "Código detectado en imagen: ${result.text}",
                Snackbar.LENGTH_LONG
            ).show()
            abrirDetalleProducto(result.text)
        } catch (e: Exception) {
            Snackbar.make(
                binding.root,
                "No se detectó ningún código en la imagen",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun abrirDetalleProducto(codigo: String) {
        Snackbar.make(binding.root, "Buscando producto...", Snackbar.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getProductByCode(codigo)
                if (response.isSuccessful && response.body()?.status == 1) {
                    val productData = response.body()!!.product

                    val producto = Producto(
                        codigoProducto = codigo,
                        nombreProducto = productData?.product_name ?: "Producto sin nombre",
                        descripcion = productData?.generic_name ?: "Sin descripción disponible",
                        clasificacion = productData?.nutriscore_score?.let { score ->
                            when {
                                score <= 2 -> "A"
                                score <= 5 -> "B"
                                score <= 8 -> "C"
                                score <= 11 -> "D"
                                else -> "E"
                            }
                        } ?: "B",
                        categorias = productData?.categories_tags?.map { it.removePrefix("en:") }
                            ?.toMutableList()
                            ?: mutableListOf("Desconocido"),
                        cantidad = productData?.quantity?.replace("[^0-9.]".toRegex(), "")
                            ?.toDoubleOrNull() ?: 0.0,
                        empaquetado = productData?.packaging ?: "No especificado",
                        paises = productData?.countries ?: "Desconocido",
                        ingredientes = productData?.ingredients_text?.split(",")?.map { it.trim() }
                            ?.toMutableList()
                            ?: mutableListOf("No disponible"),
                        imagenUrl = productData?.image_url ?: "",
                        marcas = productData?.brands ?: "Desconocido",
                        pais = productData?.countries ?: "Desconocido",
                        nutrientes = Nutriente(
                            energia = round(
                                productData?.nutriments?.energy_kcal_100g
                                    ?: productData?.nutriments?.energy_100g?.div(4.184) ?: 0.0
                            ).toDouble(),
                            grasas = round(productData?.nutriments?.fat_100g ?: 0.0).toDouble(),
                            grasasSaturadas = round(
                                productData?.nutriments?.saturated_fat_100g ?: 0.0
                            ).toDouble(),
                            azucares = round(
                                productData?.nutriments?.sugars_100g ?: 0.0
                            ).toDouble(),
                            proteinas = round(
                                productData?.nutriments?.proteins_100g ?: 0.0
                            ).toDouble(),
                            carbohidratos = round(
                                productData?.nutriments?.carbohydrates_100g ?: 0.0
                            ).toDouble(),
                            hidratosCarbono = round(
                                productData?.nutriments?.carbohydrates_100g ?: 0.0
                            ).toDouble(),
                            fibrasAlimentarias = round(
                                productData?.nutriments?.fiber_100g ?: 0.0
                            ).toDouble()
                        )
                    )

                    // ---- GUARDAR EN ROOM ----
                    val productoEntity = ProductoEntity(
                        codigo = producto.codigoProducto,
                        nombre = producto.nombreProducto,
                        marca = producto.marcas,
                        paises = producto.paises,
                        empaque = producto.empaquetado,
                        cantidad = producto.cantidad,
                        imagenUrl = producto.imagenUrl,
                        ingredientes = producto.ingredientes.joinToString(","),
                        categorias = producto.categorias.joinToString(","),
                        nutriscoreScore = productData?.nutriscore_score,
                        fechaEscaneo = System.currentTimeMillis(),
                        nutriments = productData?.nutriments?.let { nutr ->
                            NutrimentsEntity(
                                energy_kcal_100g = nutr.energy_kcal_100g,
                                energy_100g = nutr.energy_100g,
                                fat_100g = nutr.fat_100g,
                                saturated_fat_100g = nutr.saturated_fat_100g,
                                sugars_100g = nutr.sugars_100g,
                                proteins_100g = nutr.proteins_100g,
                                carbohydrates_100g = nutr.carbohydrates_100g,
                                fiber_100g = nutr.fiber_100g
                            )
                        }
                    )
                    // Guardar en Room en hilo IO
                    CoroutineScope(Dispatchers.IO).launch {
                        productoDao.insertarProducto(productoEntity)
                    }
                    // ---- FIN GUARDADO EN ROOM ----

                    // Abrir DetalleProductoActivity
                    withContext(Dispatchers.Main) {
                        val intent = Intent(
                            this@EscanearProductoActivity,
                            com.example.yachayfood.ui.view.detalle_producto.DetalleProductoActivity::class.java
                        )
                        intent.putExtra("producto", producto)
                        startActivity(intent)
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(
                            binding.root,
                            "No se encontró el producto",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(
                        binding.root,
                        "Error al consultar el producto",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
