package tec.spn.rieti.view

import android.graphics.fonts.FontStyle
import android.text.style.UnderlineSpan
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tec.spn.rieti.R

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreen() {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)) {
        Login(Modifier.align(Alignment.Center))
    }
}

@Composable
fun Login(modifier: Modifier) {
    Column(modifier = modifier) {
        HeaderImg(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(16.dp))
        UserField()
        Spacer(modifier = Modifier.padding(16.dp))
        PasswordField()
        Spacer(modifier = Modifier.padding(8.dp))
        ForgotPwd(Modifier.align(Alignment.End))
        Spacer(modifier = Modifier.padding(16.dp))
        LoginButton()
        Spacer(modifier = Modifier.padding(10.dp))
        AnonReport()
        Spacer(modifier = Modifier.padding(10.dp))
        Register(Modifier)
    }
}

@Composable
fun Register(modifier:Modifier) {
    Text(text = "¿No tienes una cuenta? Regístrate aquí",
        modifier = modifier.clickable {},
        fontSize = 12.sp,
        style = TextStyle(textDecoration = TextDecoration.Underline),
        color = Color(0xFF5C5C5C)
    )
}

@Composable
fun AnonReport() {
    Button(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFC4BECF),
            contentColor = Color.Black)

    ) {
        Text(text = "Realizar Reporte Anónimo")
    }
}

@Composable
fun LoginButton() {
    Button(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF64288F),
            disabledContainerColor = Color(0xFF9972B5),
            contentColor = Color.White,
            disabledContentColor = Color.White)

    ) {
        Text(text = "Iniciar Sesión")
    }
}

@Composable
fun ForgotPwd(modifier:Modifier) {
    Text(text = "Olvidaste la contraseña?",
        modifier = modifier.clickable { },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold

    )
}

@Composable
fun PasswordField() {
    TextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        placeholder = {Text(text = "Contraseña")},
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}


@Composable
fun UserField() {
    var user by remember { mutableStateOf("") }
    TextField(
        value = "",
        onValueChange = { user = it },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {Text(text = "Usuario")},
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}


@Composable
fun HeaderImg(modifier:Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo_rieti),
        contentDescription = "Header",
        modifier = modifier)
}