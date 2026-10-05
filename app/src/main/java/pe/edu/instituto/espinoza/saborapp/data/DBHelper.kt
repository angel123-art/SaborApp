package pe.edu.instituto.espinoza.saborapp.data
import pe.edu.instituto.espinoza.saborapp.model.DetallePedido
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import pe.edu.instituto.espinoza.saborapp.model.Plato
import pe.edu.instituto.espinoza.saborapp.model.Mesa
import pe.edu.instituto.espinoza.saborapp.model.Pedido
class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // =====================================================
    // CREAR BASE DE DATOS
    // =====================================================

    override fun onCreate(db: SQLiteDatabase) {

        // =================================================
        // TABLA USUARIO
        // =================================================

        db.execSQL(
            """
            CREATE TABLE usuario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                usuario TEXT NOT NULL UNIQUE,
                clave TEXT NOT NULL,
                rol TEXT NOT NULL
            )
            """.trimIndent()
        )
// =================================================
// USUARIO ADMINISTRADOR POR DEFECTO
// =================================================

        val admin = ContentValues().apply {

            put(
                "nombre",
                "Administrador"
            )

            put(
                "usuario",
                "admin"
            )

            put(
                "clave",
                "1234"
            )

            put(
                "rol",
                "ADMIN"
            )
        }

        db.insert(
            "usuario",
            null,
            admin
        )

        // =================================================
        // TABLA PLATO
        // =================================================

        db.execSQL(
            """
            CREATE TABLE plato (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                categoria TEXT,
                precio REAL NOT NULL,
                disponible INTEGER NOT NULL DEFAULT 1
            )
            """.trimIndent()
        )


        // =================================================
        // TABLA MESA
        // =================================================

        db.execSQL(
            """
            CREATE TABLE mesa (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero INTEGER NOT NULL UNIQUE,
                capacidad INTEGER NOT NULL,
                estado TEXT NOT NULL DEFAULT 'LIBRE'
            )
            """.trimIndent()
        )


        // =================================================
        // TABLA PEDIDO
        // =================================================

        db.execSQL(
            """
            CREATE TABLE pedido (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                mesa_id INTEGER NOT NULL,
                usuario_id INTEGER NOT NULL,
                fecha TEXT NOT NULL,
                estado TEXT NOT NULL,
                total REAL NOT NULL DEFAULT 0,
                FOREIGN KEY (mesa_id) REFERENCES mesa(id),
                FOREIGN KEY (usuario_id) REFERENCES usuario(id)
            )
            """.trimIndent()
        )


        // =================================================
        // TABLA DETALLE PEDIDO
        // =================================================

        db.execSQL(
            """
            CREATE TABLE detalle_pedido (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                pedido_id INTEGER NOT NULL,
                plato_id INTEGER NOT NULL,
                cantidad INTEGER NOT NULL,
                precio REAL NOT NULL,
                FOREIGN KEY (pedido_id) REFERENCES pedido(id),
                FOREIGN KEY (plato_id) REFERENCES plato(id)
            )
            """.trimIndent()
        )
    }


    // =====================================================
    // ACTUALIZAR BASE DE DATOS
    // =====================================================

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        db.execSQL("DROP TABLE IF EXISTS detalle_pedido")
        db.execSQL("DROP TABLE IF EXISTS pedido")
        db.execSQL("DROP TABLE IF EXISTS mesa")
        db.execSQL("DROP TABLE IF EXISTS plato")
        db.execSQL("DROP TABLE IF EXISTS usuario")

        onCreate(db)
    }

// =====================================================
// CRUD DE USUARIOS
// =====================================================

// =====================================================
// CREATE - REGISTRAR USUARIO
// =====================================================

    fun registrarUsuario(
        nombre: String,
        usuario: String,
        clave: String,
        rol: String
    ): Long {

        val db = writableDatabase

        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("usuario", usuario)
            put("clave", clave)
            put("rol", rol)
        }

        return db.insert(
            "usuario",
            null,
            valores
        )
    }


// =====================================================
// VALIDAR SI EXISTE UN USUARIO
// =====================================================

    fun existeUsuario(usuario: String): Boolean {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT id
        FROM usuario
        WHERE usuario = ?
        """.trimIndent(),
            arrayOf(usuario)
        )

        val existe = cursor.moveToFirst()

        cursor.close()

        return existe
    }


// =====================================================
// VALIDAR LOGIN
// =====================================================

    fun validarUsuario(
        usuario: String,
        clave: String
    ): Boolean {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT id
        FROM usuario
        WHERE usuario = ?
        AND clave = ?
        """.trimIndent(),
            arrayOf(usuario, clave)
        )

        val valido = cursor.moveToFirst()

        cursor.close()

        return valido
    }


// =====================================================
// OBTENER DATOS DEL USUARIO
// =====================================================

    fun obtenerDatosUsuario(
        usuario: String
    ): Triple<Int, String, String>? {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT id, nombre, rol
        FROM usuario
        WHERE usuario = ?
        """.trimIndent(),
            arrayOf(usuario)
        )

        if (cursor.moveToFirst()) {

            val id = cursor.getInt(
                cursor.getColumnIndexOrThrow("id")
            )

            val nombre = cursor.getString(
                cursor.getColumnIndexOrThrow("nombre")
            )

            val rol = cursor.getString(
                cursor.getColumnIndexOrThrow("rol")
            )

            cursor.close()

            return Triple(
                id,
                nombre,
                rol
            )
        }

        cursor.close()

        return null
    }


// =====================================================
// ASEGURAR USUARIO ADMIN
// =====================================================

    fun asegurarUsuarioAdmin() {

        if (!existeUsuario("admin")) {

            registrarUsuario(
                nombre = "Administrador",
                usuario = "admin",
                clave = "1234",
                rol = "ADMIN"
            )
        }
    }
    // =====================================================
    // CRUD DE PLATOS
    // =====================================================


    // =====================================================
    // CREATE - INSERTAR PLATO
    // =====================================================

    fun insertarPlato(plato: Plato): Long {

        val db = writableDatabase

        val valores = ContentValues().apply {

            put("nombre", plato.nombre)
            put("categoria", plato.categoria)
            put("precio", plato.precio)
            put(
                "disponible",
                if (plato.disponible) 1 else 0
            )
        }

        return db.insert(
            "plato",
            null,
            valores
        )
    }


    // =====================================================
    // READ - OBTENER TODOS LOS PLATOS
    // =====================================================

    fun obtenerPlatos(): MutableList<Plato> {

        val lista = mutableListOf<Plato>()

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT id, nombre, categoria, precio, disponible
            FROM plato
            ORDER BY id DESC
            """.trimIndent(),
            null
        )


        while (cursor.moveToNext()) {

            val plato = Plato(

                id = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
                ),

                nombre = cursor.getString(
                    cursor.getColumnIndexOrThrow("nombre")
                ),

                categoria = cursor.getString(
                    cursor.getColumnIndexOrThrow("categoria")
                ),

                precio = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("precio")
                ),

                disponible = cursor.getInt(
                    cursor.getColumnIndexOrThrow("disponible")
                ) == 1
            )

            lista.add(plato)
        }


        cursor.close()

        return lista
    }


    // =====================================================
    // UPDATE - ACTUALIZAR PLATO
    // =====================================================

    fun actualizarPlato(plato: Plato): Int {

        val db = writableDatabase

        val valores = ContentValues().apply {

            put("nombre", plato.nombre)
            put("categoria", plato.categoria)
            put("precio", plato.precio)
            put(
                "disponible",
                if (plato.disponible) 1 else 0
            )
        }


        return db.update(
            "plato",
            valores,
            "id = ?",
            arrayOf(plato.id.toString())
        )
    }


    // =====================================================
    // DELETE - ELIMINAR PLATO
    // =====================================================

    fun eliminarPlato(id: Int): Int {

        val db = writableDatabase

        return db.delete(
            "plato",
            "id = ?",
            arrayOf(id.toString())
        )
    }
    // ==================================================
// OBTENER PLATOS POR CATEGORÍA
// ==================================================

    fun obtenerPlatosPorCategoria(categoria: String): MutableList<Plato> {

        val lista = mutableListOf<Plato>()

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT id, nombre, categoria, precio, disponible
        FROM plato
        WHERE categoria = ?
        ORDER BY id DESC
        """.trimIndent(),
            arrayOf(categoria)
        )

        while (cursor.moveToNext()) {

            val plato = Plato(

                id = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
                ),

                nombre = cursor.getString(
                    cursor.getColumnIndexOrThrow("nombre")
                ),

                categoria = cursor.getString(
                    cursor.getColumnIndexOrThrow("categoria")
                ),

                precio = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("precio")
                ),

                disponible = cursor.getInt(
                    cursor.getColumnIndexOrThrow("disponible")
                ) == 1
            )

            lista.add(plato)
        }

        cursor.close()

        return lista
    }
// =====================================================
// CRUD DE MESAS
// =====================================================

// =====================================================
// CREATE - INSERTAR MESA
// =====================================================

    fun insertarMesa(mesa: pe.edu.instituto.espinoza.saborapp.model.Mesa): Long {

        val db = writableDatabase

        val valores = ContentValues().apply {
            put("numero", mesa.numero)
            put("capacidad", mesa.capacidad)
            put("estado", mesa.estado)
        }

        return db.insert(
            "mesa",
            null,
            valores
        )
    }


// =====================================================
// READ - OBTENER TODAS LAS MESAS
// =====================================================

    fun obtenerMesas(): MutableList<pe.edu.instituto.espinoza.saborapp.model.Mesa> {

        val lista =
            mutableListOf<pe.edu.instituto.espinoza.saborapp.model.Mesa>()

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT id, numero, capacidad, estado
        FROM mesa
        ORDER BY numero ASC
        """.trimIndent(),
            null
        )

        while (cursor.moveToNext()) {

            val mesa =
                pe.edu.instituto.espinoza.saborapp.model.Mesa(

                    id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                    ),

                    numero = cursor.getInt(
                        cursor.getColumnIndexOrThrow("numero")
                    ),

                    capacidad = cursor.getInt(
                        cursor.getColumnIndexOrThrow("capacidad")
                    ),

                    estado = cursor.getString(
                        cursor.getColumnIndexOrThrow("estado")
                    )
                )

            lista.add(mesa)
        }

        cursor.close()

        return lista
    }


// =====================================================
// UPDATE - ACTUALIZAR MESA
// =====================================================

    fun actualizarMesa(
        mesa: pe.edu.instituto.espinoza.saborapp.model.Mesa
    ): Int {

        val db = writableDatabase

        val valores = ContentValues().apply {
            put("numero", mesa.numero)
            put("capacidad", mesa.capacidad)
            put("estado", mesa.estado)
        }

        return db.update(
            "mesa",
            valores,
            "id = ?",
            arrayOf(mesa.id.toString())
        )
    }


// =====================================================
// DELETE - ELIMINAR MESA
// =====================================================

    fun eliminarMesa(id: Int): Int {

        val db = writableDatabase

        return db.delete(
            "mesa",
            "id = ?",
            arrayOf(id.toString())
        )
    }

    // =====================================================
    // BASE DE DATOS
    // =====================================================
// =====================================================
// CRUD DE PEDIDOS
// =====================================================

// =====================================================
// CREATE - INSERTAR PEDIDO
// =====================================================

    fun insertarPedido(pedido: Pedido): Long {

        val db = writableDatabase

        val valores = ContentValues().apply {
            put("mesa_id", pedido.mesaId)
            put("usuario_id", pedido.usuarioId)
            put("fecha", pedido.fecha)
            put("estado", pedido.estado)
            put("total", pedido.total)
        }

        return db.insert(
            "pedido",
            null,
            valores
        )
    }


// =====================================================
// READ - OBTENER TODOS LOS PEDIDOS
// =====================================================

    fun obtenerPedidos(): MutableList<Pedido> {

        val lista = mutableListOf<Pedido>()

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT id, mesa_id, usuario_id, fecha, estado, total
        FROM pedido
        ORDER BY id DESC
        """.trimIndent(),
            null
        )

        while (cursor.moveToNext()) {

            val pedido = Pedido(
                id = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
                ),
                mesaId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("mesa_id")
                ),
                usuarioId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("usuario_id")
                ),
                fecha = cursor.getString(
                    cursor.getColumnIndexOrThrow("fecha")
                ),
                estado = cursor.getString(
                    cursor.getColumnIndexOrThrow("estado")
                ),
                total = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("total")
                )
            )

            lista.add(pedido)
        }

        cursor.close()

        return lista
    }


// =====================================================
// READ - OBTENER PEDIDO POR ID
// =====================================================

    fun obtenerPedidoPorId(id: Int): Pedido? {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT id, mesa_id, usuario_id, fecha, estado, total
        FROM pedido
        WHERE id = ?
        """.trimIndent(),
            arrayOf(id.toString())
        )

        var pedido: Pedido? = null

        if (cursor.moveToFirst()) {

            pedido = Pedido(
                id = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
                ),
                mesaId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("mesa_id")
                ),
                usuarioId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("usuario_id")
                ),
                fecha = cursor.getString(
                    cursor.getColumnIndexOrThrow("fecha")
                ),
                estado = cursor.getString(
                    cursor.getColumnIndexOrThrow("estado")
                ),
                total = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("total")
                )
            )
        }

        cursor.close()

        return pedido
    }


// =====================================================
// UPDATE - ACTUALIZAR PEDIDO
// =====================================================

    fun actualizarPedido(pedido: Pedido): Int {

        val db = writableDatabase

        val valores = ContentValues().apply {
            put("mesa_id", pedido.mesaId)
            put("usuario_id", pedido.usuarioId)
            put("fecha", pedido.fecha)
            put("estado", pedido.estado)
            put("total", pedido.total)
        }

        return db.update(
            "pedido",
            valores,
            "id = ?",
            arrayOf(pedido.id.toString())
        )
    }


// =====================================================
// DELETE - ELIMINAR PEDIDO
// =====================================================

    fun eliminarPedido(id: Int): Int {

        val db = writableDatabase

        return db.delete(
            "pedido",
            "id = ?",
            arrayOf(id.toString())
        )
    }
    // =====================================================
// CRUD DE DETALLE DE PEDIDO
// =====================================================

// =====================================================
// CREATE - INSERTAR DETALLE
// =====================================================

    fun insertarDetallePedido(
        detalle: DetallePedido
    ): Long {

        val db = writableDatabase

        val valores = ContentValues().apply {
            put("pedido_id", detalle.pedidoId)
            put("plato_id", detalle.platoId)
            put("cantidad", detalle.cantidad)
            put("precio", detalle.precio)
        }

        return db.insert(
            "detalle_pedido",
            null,
            valores
        )
    }


// =====================================================
// READ - OBTENER DETALLES DE UN PEDIDO
// =====================================================

    fun obtenerDetallesPedido(
        pedidoId: Int
    ): MutableList<DetallePedido> {

        val lista = mutableListOf<DetallePedido>()

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT id, pedido_id, plato_id, cantidad, precio
        FROM detalle_pedido
        WHERE pedido_id = ?
        ORDER BY id ASC
        """.trimIndent(),
            arrayOf(pedidoId.toString())
        )

        while (cursor.moveToNext()) {

            val detalle = DetallePedido(
                id = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
                ),
                pedidoId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("pedido_id")
                ),
                platoId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("plato_id")
                ),
                cantidad = cursor.getInt(
                    cursor.getColumnIndexOrThrow("cantidad")
                ),
                precio = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("precio")
                )
            )

            lista.add(detalle)
        }

        cursor.close()

        return lista
    }


// =====================================================
// UPDATE - ACTUALIZAR DETALLE
// =====================================================

    fun actualizarDetallePedido(
        detalle: DetallePedido
    ): Int {

        val db = writableDatabase

        val valores = ContentValues().apply {
            put("pedido_id", detalle.pedidoId)
            put("plato_id", detalle.platoId)
            put("cantidad", detalle.cantidad)
            put("precio", detalle.precio)
        }

        return db.update(
            "detalle_pedido",
            valores,
            "id = ?",
            arrayOf(detalle.id.toString())
        )
    }


// =====================================================
// DELETE - ELIMINAR DETALLE
// =====================================================

    fun eliminarDetallePedido(id: Int): Int {

        val db = writableDatabase

        return db.delete(
            "detalle_pedido",
            "id = ?",
            arrayOf(id.toString())
        )
    }


// =====================================================
// DELETE - ELIMINAR TODOS LOS DETALLES DE UN PEDIDO
// =====================================================

    fun eliminarDetallesPedido(pedidoId: Int): Int {

        val db = writableDatabase

        return db.delete(
            "detalle_pedido",
            "pedido_id = ?",
            arrayOf(pedidoId.toString())
        )
    }
    // =====================================================
// CALCULAR TOTAL DEL PEDIDO
// =====================================================

    fun calcularTotalPedido(pedidoId: Int): Double {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT SUM(cantidad * precio) AS total
        FROM detalle_pedido
        WHERE pedido_id = ?
        """.trimIndent(),
            arrayOf(pedidoId.toString())
        )

        var total = 0.0

        if (cursor.moveToFirst()) {

            if (!cursor.isNull(
                    cursor.getColumnIndexOrThrow("total")
                )
            ) {
                total = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("total")
                )
            }
        }

        cursor.close()

        return total
    }
    // =====================================================
// REPORTES
// =====================================================

// =====================================================
// VENTA DEL DÍA
// =====================================================

    fun ventaDelDia(): Pair<Int, Double> {

        val db = readableDatabase

        var cantidadPedidos = 0
        var totalVentas = 0.0

        val cursor = db.rawQuery(
            """
        SELECT 
            COUNT(*) AS cantidad,
            COALESCE(SUM(total), 0) AS total
        FROM pedido
        WHERE estado = 'CERRADO'
        AND date(fecha) = date('now', 'localtime')
        """.trimIndent(),
            null
        )

        if (cursor.moveToFirst()) {

            cantidadPedidos =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "cantidad"
                    )
                )

            totalVentas =
                cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                        "total"
                    )
                )
        }

        cursor.close()

        return Pair(
            cantidadPedidos,
            totalVentas
        )
    }


// =====================================================
// TOP 5 PLATOS
// =====================================================

    fun topPlatos(): MutableList<String> {

        val lista =
            mutableListOf<String>()

        val db =
            readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT 
            plato.nombre,
            SUM(detalle_pedido.cantidad) AS cantidad
        FROM detalle_pedido
        INNER JOIN pedido
            ON detalle_pedido.pedido_id = pedido.id
        INNER JOIN plato
            ON detalle_pedido.plato_id = plato.id
        WHERE pedido.estado = 'CERRADO'
        AND date(pedido.fecha) = date('now', 'localtime')
        GROUP BY plato.id, plato.nombre
        ORDER BY cantidad DESC
        LIMIT 5
        """.trimIndent(),
            null
        )

        var posicion = 1

        while (cursor.moveToNext()) {

            val nombre =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "nombre"
                    )
                )

            val cantidad =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "cantidad"
                    )
                )

            lista.add(
                "$posicion. $nombre - $cantidad vendidos"
            )

            posicion++
        }

        cursor.close()

        return lista
    }


// =====================================================
// VENTA POR MESA
// =====================================================

    fun ventaPorMesa(): MutableList<String> {

        val lista =
            mutableListOf<String>()

        val db =
            readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT 
            mesa.numero,
            SUM(pedido.total) AS total
        FROM pedido
        INNER JOIN mesa
            ON pedido.mesa_id = mesa.id
        WHERE pedido.estado = 'CERRADO'
        AND date(pedido.fecha) = date('now', 'localtime')
        GROUP BY mesa.id, mesa.numero
        ORDER BY total DESC
        """.trimIndent(),
            null
        )

        while (cursor.moveToNext()) {

            val numeroMesa =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "numero"
                    )
                )

            val total =
                cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                        "total"
                    )
                )

            lista.add(
                "Mesa $numeroMesa - S/ %.2f"
                    .format(total)
            )
        }

        cursor.close()

        return lista
    }
    // =====================================================
// PEDIDOS ABIERTOS
// =====================================================

    fun obtenerPedidosAbiertos(): MutableList<pe.edu.instituto.espinoza.saborapp.model.Pedido> {

        val lista =
            mutableListOf<pe.edu.instituto.espinoza.saborapp.model.Pedido>()

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT id, mesa_id, usuario_id, fecha, estado, total
        FROM pedido
        WHERE estado = 'ABIERTO'
        ORDER BY id DESC
        """.trimIndent(),
            null
        )

        while (cursor.moveToNext()) {

            val pedido =
                pe.edu.instituto.espinoza.saborapp.model.Pedido(

                    id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                    ),

                    mesaId = cursor.getInt(
                        cursor.getColumnIndexOrThrow("mesa_id")
                    ),

                    usuarioId = cursor.getInt(
                        cursor.getColumnIndexOrThrow("usuario_id")
                    ),

                    fecha = cursor.getString(
                        cursor.getColumnIndexOrThrow("fecha")
                    ),

                    estado = cursor.getString(
                        cursor.getColumnIndexOrThrow("estado")
                    ),

                    total = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("total")
                    )
                )

            lista.add(pedido)
        }

        cursor.close()

        return lista
    }


// =====================================================
// CERRAR PEDIDO
// =====================================================

    fun cerrarPedido(pedidoId: Int): Boolean {

        val db = writableDatabase

        db.beginTransaction()

        try {

            // ---------------------------------------------
            // BUSCAR LA MESA DEL PEDIDO
            // ---------------------------------------------

            val cursor = db.rawQuery(
                """
            SELECT mesa_id
            FROM pedido
            WHERE id = ?
            AND estado = 'ABIERTO'
            """.trimIndent(),
                arrayOf(pedidoId.toString())
            )

            if (!cursor.moveToFirst()) {

                cursor.close()

                db.endTransaction()

                return false
            }

            val mesaId =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow("mesa_id")
                )

            cursor.close()


            // ---------------------------------------------
            // CAMBIAR PEDIDO A CERRADO
            // ---------------------------------------------

            val valoresPedido = ContentValues().apply {

                put(
                    "estado",
                    "CERRADO"
                )
            }

            val pedidoActualizado =
                db.update(
                    "pedido",
                    valoresPedido,
                    "id = ? AND estado = 'ABIERTO'",
                    arrayOf(pedidoId.toString())
                )

            if (pedidoActualizado != 1) {

                throw Exception(
                    "No se pudo cerrar el pedido"
                )
            }


            // ---------------------------------------------
            // LIBERAR MESA
            // ---------------------------------------------

            val valoresMesa = ContentValues().apply {

                put(
                    "estado",
                    "LIBRE"
                )
            }

            val mesaActualizada =
                db.update(
                    "mesa",
                    valoresMesa,
                    "id = ?",
                    arrayOf(mesaId.toString())
                )

            if (mesaActualizada != 1) {

                throw Exception(
                    "No se pudo liberar la mesa"
                )
            }


            // ---------------------------------------------
            // CONFIRMAR TRANSACCIÓN
            // ---------------------------------------------

            db.setTransactionSuccessful()

            return true

        } catch (e: Exception) {

            return false

        } finally {

            db.endTransaction()
        }
    }
    // =====================================================
// REPORTES POR USUARIO
// =====================================================


// =====================================================
// VENTA DEL DÍA POR USUARIO
// =====================================================

    fun ventaDelDiaPorUsuario(
        usuarioId: Int
    ): Pair<Int, Double> {

        val db = readableDatabase

        var cantidadPedidos = 0
        var totalVentas = 0.0

        val cursor = db.rawQuery(
            """
        SELECT
            COUNT(*) AS cantidad,
            COALESCE(SUM(total), 0) AS total
        FROM pedido
        WHERE estado = 'CERRADO'
        AND usuario_id = ?
        AND date(fecha) = date('now', 'localtime')
        """.trimIndent(),
            arrayOf(
                usuarioId.toString()
            )
        )

        if (cursor.moveToFirst()) {

            cantidadPedidos =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "cantidad"
                    )
                )

            totalVentas =
                cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                        "total"
                    )
                )
        }

        cursor.close()

        return Pair(
            cantidadPedidos,
            totalVentas
        )
    }


// =====================================================
// TOP 5 PLATOS POR USUARIO
// =====================================================

    fun topPlatosPorUsuario(
        usuarioId: Int
    ): MutableList<String> {

        val lista =
            mutableListOf<String>()

        val db =
            readableDatabase

        val cursor =
            db.rawQuery(
                """
            SELECT
                plato.nombre,
                SUM(
                    detalle_pedido.cantidad
                ) AS cantidad
            FROM detalle_pedido

            INNER JOIN pedido
                ON detalle_pedido.pedido_id =
                   pedido.id

            INNER JOIN plato
                ON detalle_pedido.plato_id =
                   plato.id

            WHERE pedido.estado = 'CERRADO'

            AND pedido.usuario_id = ?

            AND date(pedido.fecha) =
                date('now', 'localtime')

            GROUP BY
                plato.id,
                plato.nombre

            ORDER BY cantidad DESC

            LIMIT 5
            """.trimIndent(),
                arrayOf(
                    usuarioId.toString()
                )
            )

        var posicion = 1

        while (cursor.moveToNext()) {

            val nombre =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "nombre"
                    )
                )

            val cantidad =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "cantidad"
                    )
                )

            lista.add(
                "$posicion. $nombre - $cantidad vendidos"
            )

            posicion++
        }

        cursor.close()

        return lista
    }


// =====================================================
// VENTA POR MESA POR USUARIO
// =====================================================

    fun ventaPorMesaPorUsuario(
        usuarioId: Int
    ): MutableList<String> {

        val lista =
            mutableListOf<String>()

        val db =
            readableDatabase

        val cursor =
            db.rawQuery(
                """
            SELECT
                mesa.numero,
                SUM(pedido.total) AS total

            FROM pedido

            INNER JOIN mesa
                ON pedido.mesa_id =
                   mesa.id

            WHERE pedido.estado = 'CERRADO'

            AND pedido.usuario_id = ?

            AND date(pedido.fecha) =
                date('now', 'localtime')

            GROUP BY
                mesa.id,
                mesa.numero

            ORDER BY total DESC
            """.trimIndent(),
                arrayOf(
                    usuarioId.toString()
                )
            )

        while (cursor.moveToNext()) {

            val numeroMesa =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "numero"
                    )
                )

            val total =
                cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                        "total"
                    )
                )

            lista.add(
                "Mesa $numeroMesa - S/ %.2f"
                    .format(total)
            )
        }

        cursor.close()

        return lista
    }
    companion object {

        private const val DATABASE_NAME = "saborapp.db"

        private const val DATABASE_VERSION = 1
    }
}