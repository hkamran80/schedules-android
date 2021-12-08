package com.hkamran.schedules.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hkamran.schedules.R

val Lato = FontFamily(
    Font(R.font.lato_light, FontWeight.Light),
    Font(R.font.lato_regular),
    Font(R.font.lato_bold, FontWeight.Bold),
)

// Set of Material typography styles to start with
val Typography = Typography(
    h5 = TextStyle(fontFamily = Lato, fontWeight = FontWeight.Bold, fontSize = 34.sp),
    h4 = TextStyle(fontFamily = Lato, fontWeight = FontWeight.Normal, fontSize = 34.sp),
    h6 = TextStyle(fontFamily = Lato, fontWeight = FontWeight.Light, fontSize = 20.sp),
    body1 = TextStyle(
        fontFamily = Lato,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)