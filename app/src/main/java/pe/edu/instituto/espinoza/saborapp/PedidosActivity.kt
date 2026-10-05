package pe.edu.instituto.espinoza.saborapp

import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.google.android.material.button.MaterialButton

import pe.edu.instituto.espinoza.saborapp.data.DBHelper
import pe.edu.instituto.espinoza.saborapp.model.DetallePedido
import pe.edu.instituto.espinoza.saborapp.model.Mesa
import pe.edu.instituto.espinoza.saborapp.model.Pedido
import pe.edu.instituto.espinoza.saborapp.model.Plato

import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PedidosActivity : AppCompatActivity() {

    // =====================================================
    // BASE DE DATOS
    // =====================================================

    private lateinit var dbHelper: DBHelper


    // =====================================================
    // USUARIO LOGUEADO
    // =====================================================

    private var idUsuario: Int = 1
    private var nombreUsuario: String = ""
    private var rolUsuario: String = "MOZO"


    // =====================================================
    // VISTAS
    // =====================================================

    private lateinit var spinnerMesa: Spinner
    private lateinit var spinnerPlato: Spinner
    private lateinit var etCantidad: EditText

    private lateinit var layoutDetalles: LinearLayout
    private lateinit var tvTotal: TextView

    private lateinit var btnAgregarDetalle: MaterialButton
    private lateinit var btnGuardarPedido: MaterialButton
    private lateinit var btnLimpiarPedido: MaterialButton
    private lateinit var btnRegresarPedidos: MaterialButton


    // =====================================================
    // LISTAS
    // =====================================================

    private var mesas = mutableListOf<Mesa>()
    private var platos = mutableListOf<Plato>()


    // =====================================================
    // DETALLES TEMPORALES
    // =====================================================

    private val detallesTemporales =
        mutableListOf<DetalleTemporal>()


    // =====================================================
    // ON CREATE
    // =====================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_pedidos)


        // =================================================
        // AJUSTAR BARRAS DEL SISTEMA
        // =================================================

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }


        // =================================================
        // BASE DE DATOS
        // =================================================

        dbHelper = DBHelper(this)


        // =================================================
        // RECIBIR USUARIO DESDE LOGIN / MENU
        // =================================================

        idUsuario =
            intent.getIntExtra(
                "ID_USUARIO",
                1
            )

        nombreUsuario =
            intent.getStringExtra(
                "NOMBRE_USUARIO"
            ).orEmpty()

        rolUsuario =
            intent.getStringExtra(
                "ROL"
            ) ?: "MOZO"


        // =================================================
        // VISTAS
        // =================================================

        spinnerMesa =
            findViewById(
                R.id.spinnerMesa
            )

        spinnerPlato =
            findViewById(
                R.id.spinnerPlato
            )

        etCantidad =
            findViewById(
                R.id.etCantidad
            )

        layoutDetalles =
            findViewById(
                R.id.layoutDetalles
            )

        tvTotal =
            findViewById(
                R.id.tvTotal
            )

        btnAgregarDetalle =
            findViewById(
                R.id.btnAgregarDetalle
            )

        btnGuardarPedido =
            findViewById(
                R.id.btnGuardarPedido
            )

        btnLimpiarPedido =
            findViewById(
                R.id.btnLimpiarPedido
            )

        btnRegresarPedidos =
            findViewById(
                R.id.btnRegresarPedidos
            )


        // =================================================
        // CARGAR DATOS
        // =================================================

        cargarMesas()

        cargarPlatos()


        // =================================================
        // AGREGAR PLATO
        // =================================================

        btnAgregarDetalle.setOnClickListener {

            agregarDetalle()
        }


        // =================================================
        // GUARDAR PEDIDO
        // =================================================

        btnGuardarPedido.setOnClickListener {

            guardarPedido()
        }


        // =================================================
        // LIMPIAR
        // =================================================

        btnLimpiarPedido.setOnClickListener {

            limpiarPedido()
        }


        // =================================================
        // REGRESAR
        // =================================================

        btnRegresarPedidos.setOnClickListener {

            finish()
        }


        // =================================================
        // MOSTRAR TOTAL INICIAL
        // =================================================

        actualizarTotal()
    }


    // =====================================================
    // CARGAR MESAS
    // =====================================================

    private fun cargarMesas() {

        mesas =
            dbHelper.obtenerMesas()

        val nombresMesas =
            mutableListOf<String>()


        for (mesa in mesas) {

            nombresMesas.add(
                "Mesa ${mesa.numero} - ${mesa.estado}"
            )
        }


        if (nombresMesas.isEmpty()) {

            nombresMesas.add(
                "No hay mesas registradas"
            )
        }


        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                nombresMesas
            )


        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )


        spinnerMesa.adapter =
            adapter
    }


    // =====================================================
    // CARGAR PLATOS
    // =====================================================

    private fun cargarPlatos() {

        platos =
            dbHelper.obtenerPlatos()

        val nombresPlatos =
            mutableListOf<String>()


        for (plato in platos) {

            nombresPlatos.add(
                "${plato.nombre} - S/ %.2f"
                    .format(plato.precio)
            )
        }


        if (nombresPlatos.isEmpty()) {

            nombresPlatos.add(
                "No hay platos registrados"
            )
        }


        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                nombresPlatos
            )


        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )


        spinnerPlato.adapter =
            adapter
    }


    // =====================================================
    // AGREGAR DETALLE
    // =====================================================

    private fun agregarDetalle() {

        // =================================================
        // VALIDAR MESAS
        // =================================================

        if (mesas.isEmpty()) {

            Toast.makeText(
                this,
                "Primero debes registrar una mesa",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // =================================================
        // VALIDAR PLATOS
        // =================================================

        if (platos.isEmpty()) {

            Toast.makeText(
                this,
                "Primero debes registrar un plato",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // =================================================
        // OBTENER CANTIDAD
        // =================================================

        val cantidadTexto =
            etCantidad.text
                .toString()
                .trim()


        if (cantidadTexto.isEmpty()) {

            etCantidad.error =
                "Ingrese una cantidad"

            return
        }


        val cantidad =
            cantidadTexto.toIntOrNull()


        if (
            cantidad == null ||
            cantidad <= 0
        ) {

            etCantidad.error =
                "Cantidad inválida"

            return
        }


        // =================================================
        // OBTENER PLATO SELECCIONADO
        // =================================================

        val posicionPlato =
            spinnerPlato.selectedItemPosition


        if (
            posicionPlato < 0 ||
            posicionPlato >= platos.size
        ) {
            return
        }


        val plato =
            platos[posicionPlato]


        // =================================================
        // BUSCAR SI YA EXISTE
        // =================================================

        val existente =
            detallesTemporales.find {

                it.platoId == plato.id
            }


        if (existente != null) {

            // Si ya existe, aumentamos cantidad

            existente.cantidad +=
                cantidad

        } else {

            // Si no existe, agregamos nuevo detalle

            detallesTemporales.add(

                DetalleTemporal(

                    platoId =
                        plato.id!!,

                    nombrePlato =
                        plato.nombre,

                    cantidad =
                        cantidad,

                    precio =
                        plato.precio
                )
            )
        }


        // =================================================
        // LIMPIAR CANTIDAD
        // =================================================

        etCantidad.setText("1")


        // =================================================
        // ACTUALIZAR PANTALLA
        // =================================================

        mostrarDetalles()

        actualizarTotal()
    }


    // =====================================================
    // MOSTRAR DETALLES
    // =====================================================

    private fun mostrarDetalles() {

        layoutDetalles.removeAllViews()


        for (detalle in detallesTemporales) {

            val importe =
                detalle.cantidad *
                        detalle.precio


            val tv =
                TextView(this)


            tv.text =
                "🍽 ${detalle.nombrePlato}   " +
                        "x${detalle.cantidad}   " +
                        "S/ %.2f"
                            .format(importe)


            tv.textSize =
                15f


            tv.setPadding(
                8,
                12,
                8,
                12
            )


            layoutDetalles.addView(
                tv
            )
        }


        // =================================================
        // SI NO HAY DETALLES
        // =================================================

        if (detallesTemporales.isEmpty()) {

            val tvVacio =
                TextView(this)


            tvVacio.text =
                "No hay platos agregados"


            tvVacio.textSize =
                14f


            tvVacio.setPadding(
                8,
                12,
                8,
                12
            )


            layoutDetalles.addView(
                tvVacio
            )
        }
    }


    // =====================================================
    // CALCULAR TOTAL
    // =====================================================

    private fun calcularTotalTemporal(): Double {

        var total =
            0.0


        for (detalle in detallesTemporales) {

            total +=
                detalle.cantidad *
                        detalle.precio
        }


        return total
    }


    // =====================================================
    // MOSTRAR TOTAL
    // =====================================================

    private fun actualizarTotal() {

        val total =
            calcularTotalTemporal()


        tvTotal.text =
            "TOTAL: S/ %.2f"
                .format(total)
    }


    // =====================================================
    // GUARDAR PEDIDO
    // =====================================================

    private fun guardarPedido() {

        // =================================================
        // VALIDAR MESAS
        // =================================================

        if (mesas.isEmpty()) {

            Toast.makeText(
                this,
                "No hay mesas registradas",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // =================================================
        // VALIDAR DETALLES
        // =================================================

        if (detallesTemporales.isEmpty()) {

            Toast.makeText(
                this,
                "Agrega al menos un plato",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // =================================================
        // OBTENER MESA
        // =================================================

        val posicionMesa =
            spinnerMesa.selectedItemPosition


        if (
            posicionMesa < 0 ||
            posicionMesa >= mesas.size
        ) {
            return
        }


        val mesa =
            mesas[posicionMesa]


        // =================================================
        // CALCULAR TOTAL
        // =================================================

        val total =
            calcularTotalTemporal()


        // =================================================
        // FECHA
        // =================================================

        val fecha =
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            ).format(
                Date()
            )


        // =================================================
        // USUARIO LOGUEADO
        // =================================================

        val usuarioId =
            idUsuario


        // =================================================
        // CREAR PEDIDO
        // =================================================

        val pedido =
            Pedido(

                mesaId =
                    mesa.id!!,

                usuarioId =
                    usuarioId,

                fecha =
                    fecha,

                estado =
                    "ABIERTO",

                total =
                    total
            )


        // =================================================
        // INSERTAR PEDIDO
        // =================================================

        val pedidoId =
            dbHelper.insertarPedido(
                pedido
            )


        if (pedidoId == -1L) {

            Toast.makeText(
                this,
                "No se pudo guardar el pedido",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // =================================================
        // GUARDAR DETALLES
        // =================================================

        for (
        detalle in detallesTemporales
        ) {

            val detallePedido =
                DetallePedido(

                    pedidoId =
                        pedidoId.toInt(),

                    platoId =
                        detalle.platoId,

                    cantidad =
                        detalle.cantidad,

                    precio =
                        detalle.precio
                )


            dbHelper.insertarDetallePedido(
                detallePedido
            )
        }


        // =================================================
        // CAMBIAR MESA A OCUPADA
        // =================================================

        val mesaOcupada =
            Mesa(

                id =
                    mesa.id,

                numero =
                    mesa.numero,

                capacidad =
                    mesa.capacidad,

                estado =
                    "OCUPADA"
            )


        dbHelper.actualizarMesa(
            mesaOcupada
        )


        // =================================================
        // MENSAJE
        // =================================================

        Toast.makeText(
            this,
            "Pedido #$pedidoId creado correctamente",
            Toast.LENGTH_LONG
        ).show()


        // =================================================
        // GENERAR COMPROBANTE PDF
        // =================================================

        val fechaComprobante =
            SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(
                Date()
            )


        generarComprobantePdf(

            pedidoId =
                pedidoId,

            mesaTexto =
                "Mesa ${mesa.numero}",

            total =
                total,

            fecha =
                fechaComprobante
        )


        // =================================================
        // LIMPIAR FORMULARIO
        // =================================================

        limpiarPedido()
    }


    // =====================================================
    // LIMPIAR PEDIDO
    // =====================================================

    private fun limpiarPedido() {

        detallesTemporales.clear()

        etCantidad.setText("1")

        mostrarDetalles()

        actualizarTotal()
    }


    // =====================================================
    // GENERAR COMPROBANTE PDF
    // =====================================================

    private fun generarComprobantePdf(

        pedidoId: Long,

        mesaTexto: String,

        total: Double,

        fecha: String

    ) {

        try {

            // =================================================
            // CREAR DOCUMENTO PDF
            // =================================================

            val pdfDocument =
                PdfDocument()


            val pageInfo =
                PdfDocument.PageInfo.Builder(
                    595,
                    842,
                    1
                ).create()


            val page =
                pdfDocument.startPage(
                    pageInfo
                )


            val canvas =
                page.canvas


            val paint =
                Paint()


            var y =
                50f


            // =================================================
            // ENCABEZADO
            // =================================================

            paint.textSize =
                22f

            paint.isFakeBoldText =
                true


            canvas.drawText(
                "SABOR APP",
                220f,
                y,
                paint
            )


            y += 30


            paint.textSize =
                14f

            paint.isFakeBoldText =
                false


            canvas.drawText(
                "POLLERÍA EL BUEN SABOR",
                190f,
                y,
                paint
            )


            y += 35


            paint.textSize =
                18f

            paint.isFakeBoldText =
                true


            canvas.drawText(
                "COMPROBANTE DE VENTA",
                175f,
                y,
                paint
            )


            y += 40


            // =================================================
            // INFORMACIÓN
            // =================================================

            paint.textSize =
                13f

            paint.isFakeBoldText =
                false


            canvas.drawText(
                "Pedido N°: $pedidoId",
                50f,
                y,
                paint
            )


            y += 22


            canvas.drawText(
                "Mesa: $mesaTexto",
                50f,
                y,
                paint
            )


            y += 22


            canvas.drawText(
                "Fecha: $fecha",
                50f,
                y,
                paint
            )


            y += 30


            canvas.drawText(
                "------------------------------------------------------",
                50f,
                y,
                paint
            )


            y += 25


            // =================================================
            // ENCABEZADO PRODUCTOS
            // =================================================

            paint.isFakeBoldText =
                true


            canvas.drawText(
                "PRODUCTO",
                50f,
                y,
                paint
            )


            canvas.drawText(
                "CANT.",
                330f,
                y,
                paint
            )


            canvas.drawText(
                "PRECIO",
                410f,
                y,
                paint
            )


            y += 22


            paint.isFakeBoldText =
                false


            // =================================================
            // DETALLES DEL PEDIDO
            // =================================================

            val detalles =
                dbHelper.obtenerDetallesPedido(
                    pedidoId.toInt()
                )


            val platosDb =
                dbHelper.obtenerPlatos()


            for (
            detalle in detalles
            ) {

                val plato =
                    platosDb.find {

                        it.id ==
                                detalle.platoId
                    }


                if (plato != null) {

                    val nombre =
                        plato.nombre


                    val cantidad =
                        detalle.cantidad


                    val precio =
                        detalle.precio


                    canvas.drawText(
                        nombre.take(25),
                        50f,
                        y,
                        paint
                    )


                    canvas.drawText(
                        cantidad.toString(),
                        335f,
                        y,
                        paint
                    )


                    canvas.drawText(
                        "S/ %.2f"
                            .format(precio),
                        410f,
                        y,
                        paint
                    )


                    y += 24
                }
            }


            // =================================================
            // TOTAL
            // =================================================

            y += 15


            canvas.drawText(
                "------------------------------------------------------",
                50f,
                y,
                paint
            )


            y += 35


            paint.textSize =
                18f

            paint.isFakeBoldText =
                true


            canvas.drawText(
                "TOTAL:",
                330f,
                y,
                paint
            )


            canvas.drawText(
                "S/ %.2f"
                    .format(total),
                410f,
                y,
                paint
            )


            y += 50


            // =================================================
            // PIE DEL COMPROBANTE
            // =================================================

            paint.textSize =
                13f

            paint.isFakeBoldText =
                false


            canvas.drawText(
                "Gracias por su preferencia",
                205f,
                y,
                paint
            )


            y += 22


            canvas.drawText(
                "SaborApp - El Buen Sabor",
                205f,
                y,
                paint
            )


            // =================================================
            // FINALIZAR PÁGINA
            // =================================================

            pdfDocument.finishPage(
                page
            )


            // =================================================
            // CREAR CARPETA
            // =================================================

            val carpeta =
                File(
                    cacheDir,
                    "comprobantes"
                )


            if (!carpeta.exists()) {

                carpeta.mkdirs()
            }


            // =================================================
            // ARCHIVO PDF
            // =================================================

            val nombreArchivo =
                "comprobante_pedido_$pedidoId.pdf"


            val archivoPdf =
                File(
                    carpeta,
                    nombreArchivo
                )


            val outputStream =
                FileOutputStream(
                    archivoPdf
                )


            pdfDocument.writeTo(
                outputStream
            )


            outputStream.close()


            pdfDocument.close()


            // =================================================
            // URI DEL PDF
            // =================================================

            val uri: Uri =
                FileProvider.getUriForFile(

                    this,

                    "${packageName}.fileprovider",

                    archivoPdf
                )


            // =================================================
            // ABRIR PDF
            // =================================================

            val intent =
                Intent(
                    Intent.ACTION_VIEW
                )


            intent.setDataAndType(
                uri,
                "application/pdf"
            )


            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )


            try {

                startActivity(
                    intent
                )

            } catch (e: Exception) {

                Toast.makeText(

                    this,

                    "PDF generado correctamente, pero el dispositivo no tiene una aplicación para abrir PDF.",

                    Toast.LENGTH_LONG

                ).show()
            }


        } catch (e: Exception) {

            Toast.makeText(

                this,

                "Error al generar comprobante: ${e.message}",

                Toast.LENGTH_LONG

            ).show()
        }
    }


    // =====================================================
    // MODELO TEMPORAL
    // =====================================================

    data class DetalleTemporal(

        val platoId: Int,

        val nombrePlato: String,

        var cantidad: Int,

        val precio: Double
    )
}