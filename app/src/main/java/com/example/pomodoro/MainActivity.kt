package com.example.pomodoro

import android.content.Context
import android.media.MediaPlayer
import android.media.midi.MidiOutputPort
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodoro.ui.theme.PomodoroTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.color.MaterialColors

import kotlin.concurrent.timer



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PomodoroTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    DefaultPreview()
                    playSound(this,R.raw.alarm)
                }
            }
        }
    }
}
fun playSound(context: Context, soundId: Int) {
    val mediaPlayer = MediaPlayer.create(context, soundId)
    mediaPlayer.setOnCompletionListener {
        mediaPlayer.release()
    }
    mediaPlayer.start()
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview(context: Context = LocalContext.current) {

    val systemUiController = rememberSystemUiController()

    var backgroundColor by remember {
        mutableStateOf(Color(1f, 0.25f, 0.184f))
    }
    var rectangleColor by remember {
        mutableStateOf(Color(255, 43, 27))
    }
    var tekst by remember {
        mutableStateOf("0")
    }
    var buttonText by remember {
        mutableStateOf("Start")
    }
    var isStopped by remember {
        mutableStateOf(true)
    }

    var remainingTime by remember {
        mutableStateOf(0L)
    }

    var timer: CountDownTimer? by remember {
        mutableStateOf(null)
    }

    fun formatTime(remainingTime: Long): String {
        val minutes = (remainingTime / 1000) / 60
        val seconds = (remainingTime / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    LaunchedEffect(isStopped) {
        if (!isStopped) {
            timer?.cancel()

            timer = object : CountDownTimer(
                if (remainingTime > 0) remainingTime else 5000L,
                1000
            ) {
                override fun onTick(millisUntilFinished: Long) {
                    Log.i("info", millisUntilFinished.toString())
                    tekst = formatTime(millisUntilFinished)
                    remainingTime = millisUntilFinished
                }

                override fun onFinish() {
                    tekst = "done!"
                    remainingTime = 5000
                    isStopped = true
                    buttonText = "Start"
                    systemUiController.setSystemBarsColor(
                        color = Color(53, 133, 138)
                    )
                    backgroundColor = Color(53, 133, 138)
                    rectangleColor = Color(76,145,150)

                    playSound(context,R.raw.alarm)
                }
            }.start()
        } else {
            timer?.cancel()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(color = backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            Modifier
                .background(color = rectangleColor, shape = RoundedCornerShape(20.dp))
                .defaultMinSize(300.dp, 300.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(35.dp)
            ) {
                ClickableText(
                    text = AnnotatedString("Pomodoro", spanStyle = SpanStyle(color = Color.White)),
                    onClick = {

                        systemUiController.setSystemBarsColor(
                            Color(1f, 0.25f, 0.184f)
                        )
                        backgroundColor = Color(1f, 0.25f, 0.184f)
                        rectangleColor = Color(255, 43, 27)
                        timer?.cancel()
                        remainingTime = 25 * 60 * 1000
                        tekst = "25:00"
                        isStopped = true
                        buttonText = "Start"

                    }
                )

                ClickableText(

                    text = AnnotatedString("Break", spanStyle = SpanStyle(color = Color.White)),
                    onClick = {
                        timer?.cancel()
                        tekst = "5:00"
                        remainingTime = 5 * 60 * 1000
                        isStopped = true
                        buttonText = "Start"
                        systemUiController.setSystemBarsColor(
                            color = Color(53, 133, 138)
                        )
                        backgroundColor = Color(53, 133, 138)
                        rectangleColor = Color(76,145,150)

                    }
                )
                ClickableText(
                    text = AnnotatedString("Long Break", spanStyle = SpanStyle(color = Color.White)),
                    onClick = {
                        timer?.cancel()
                        tekst = "15:00"
                        remainingTime = 15 * 60 * 1000
                        isStopped = true
                        buttonText = "Start"
                        systemUiController.setSystemBarsColor(
                            color = Color(53, 133, 138)
                        )
                        backgroundColor = Color(53, 133, 138)
                        rectangleColor = Color(76,145,150)
                    }
                )

            }

            Text(text = "It's time to work!", fontSize = 30.sp, color = Color.White)

            Text(text = tekst, fontSize = 50.sp, color = Color.White)

            Button(
                onClick = {
                    isStopped = !isStopped
                    buttonText = if (isStopped) "Start" else "Stop"
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            ) {
                Text(text = buttonText, fontSize = 30.sp, color = backgroundColor)
            }
        }
    }
}