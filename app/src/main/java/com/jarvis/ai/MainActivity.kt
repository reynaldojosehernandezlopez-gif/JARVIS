
package com.jarvis.ai

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {

    private var tts: TextToSpeech? = null
    private lateinit var respuesta: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(5, 10, 22)
        window.navigationBarColor = Color.BLACK

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "NI")
            }
        }

        val fondo = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 35, 28, 35)
            setBackgroundColor(Color.rgb(5, 10, 22))
        }

        val titulo = TextView(this).apply {
            text = "J.A.R.V.I.S"
            textSize = 34f
            setTextColor(Color.rgb(40, 180, 255))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val subtitulo = TextView(this).apply {
            text = "JUST A RATHER VERY INTELLIGENT SYSTEM"
            textSize = 10f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        val reactor = TextView(this).apply {
            text = "◉"
            textSize = 105f
            setTextColor(Color.rgb(0, 170, 255))
            gravity = Gravity.CENTER
        }

        val estado = TextView(this).apply {
            text = "SISTEMA EN LÍNEA"
            textSize = 14f
            setTextColor(Color.rgb(50, 220, 180))
            gravity = Gravity.CENTER
        }

        respuesta = TextView(this).apply {
            text = "Buenos días. Soy JARVIS. Estoy a su servicio."
            textSize = 17f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(10, 25, 10, 25)
        }

        val hora = TextView(this).apply {
            text = SimpleDateFormat(
                "dd/MM/yyyy  HH:mm",
                Locale.getDefault()
            ).format(Date())
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        val boton = Button(this).apply {
            text = "🎙  HABLAR CON JARVIS"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                setColor(Color.rgb(0, 95, 170))
                cornerRadius = 35f
            }
            setOnClickListener {
                escuchar()
            }
        }

        fondo.addView(titulo)
        fondo.addView(subtitulo)
        fondo.addView(reactor)
        fondo.addView(estado)
        fondo.addView(respuesta)
        fondo.addView(hora)
        fondo.addView(boton, LinearLayout.LayoutParams(
            -1, 65
        ).apply {
            topMargin = 35
        })

        setContentView(fondo)
    }

    private fun escuchar() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "es-NI"
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Te escucho, jefe..."
        )

        try {
            startActivityForResult(intent, 100)
        } catch (e: Exception) {
            respuesta.text = "No encuentro un servicio de reconocimiento de voz."
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val palabras = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )

            val comando = palabras?.firstOrNull() ?: return
            respuesta.text = "Tú: $comando"

            val texto = comando.lowercase(Locale.ROOT)

            val contestacion = when {
                texto.contains("buenos días") ->
                    "Buenos días, Reynaldo. ¿Qué haremos el día de hoy?"

                texto.contains("qué hora") ->
                    "Son las " + SimpleDateFormat(
                        "HH:mm", Locale.getDefault()
                    ).format(Date())

                texto.contains("qué fecha") ||
                texto.contains("qué día") ->
                    "Hoy es " + SimpleDateFormat(
                        "EEEE dd 'de' MMMM",
                        Locale("es", "NI")
                    ).format(Date())

                texto.contains("qué opinas") ->
                    "Sí... o bueno, haremos esto mejor."

                texto.contains("hola") ->
                    "Hola, jefe. Estoy a su servicio."

                else ->
                    "He recibido su comando, jefe. Todavía estoy aprendiendo."
            }

            respuesta.text = contestacion
            tts?.speak(
                contestacion,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "jarvis"
            )
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
