package es.moralzarzal.ligaf7

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.FirebaseDatabase
import es.moralzarzal.ligaf7.ui.theme.LigaF7Theme
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LigaF7Theme {
                // Usamos un NavController para gestionar la navegación
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Configuramos el NavHost para controlar las rutas de la navegación
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash") {
                            SplashScreen(navController = navController)
                        }
                        // Pantalla de lista de fotos
                        composable("photo_list") {
                            PhotoList(navController = navController)
                        }
                        // Pantalla de detalle de foto
                        composable("photo_detail/{photoId}") { backStackEntry ->
                            val photoId = backStackEntry.arguments?.getString("photoId")?.toInt()
                            photoId?.let { PhotoDetailV2(photoId = it, navController = navController) }
                        }
                    }
                    // Añadiendo el menú circular
                    CircularMenu(navController = navController)
                }
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)), // Color de fondo verde
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo), // Agrega tu logo aquí
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp) // Ajusta el tamaño según necesites
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Bienvenido a la Liga F7", color = Color.White, fontSize = 24.sp)
        }
    }

    LaunchedEffect(Unit) {
        // Navega a la lista de fotos después de 2 segundos
        delay(2000)
        navController.navigate("photo_list") {
            popUpTo("splash") { inclusive = true }
        }
    }
}

@Composable
fun CircularMenu(navController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Photos", "Votes", "Stats") // Opciones del menú
    val radius = 100.dp // Radio del menú circular

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(onClick = { expanded = !expanded }) {
            Image(
                painter = painterResource(id = R.drawable.ball), // Asegúrate de tener esta imagen
                contentDescription = "Menu",
                modifier = Modifier.size(60.dp)
            )
        }

        if (expanded) {
            options.forEachIndexed { index, option ->
                val angle = 360f / options.size * index
                // Usamos LocalDensity para convertir dp a px
                val density = LocalDensity.current
                val x = with(density) { radius.toPx() * cos(Math.toRadians(angle.toDouble())).toFloat() }
                val y = with(density) { radius.toPx() * sin(Math.toRadians(angle.toDouble())).toFloat() }

                Button(
                    onClick = {
                        when (option) {
                            "Photos" -> navController.navigate("photo_list")
                            "Votes" -> navController.navigate("votes") // Asegúrate de crear esta pantalla
                            "Stats" -> navController.navigate("stats") // Asegúrate de crear esta pantalla
                        }
                    },
                    modifier = Modifier
                        .offset(x.dp, y.dp)
                        .padding(8.dp)
                ) {
                    Text(option)
                }
            }
        }
    }
}
@Composable
fun PhotoList(navController: NavHostController, modifier: Modifier = Modifier) {
    val photoList = listOf(
        R.drawable.photo1, // Añade tus recursos de imágenes
        R.drawable.photo2,
        R.drawable.photo3
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photoList.size) { index ->
            PhotoItem(photoList[index], onClick = {
                navController.navigate("photo_detail/$index")
            })
        }
    }
}
@Composable
fun PhotoItem(photoRes: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick), // Navegación cuando se haga clic
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Image(
            painter = painterResource(id = photoRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
@Composable
fun PhotoDetailV2(photoId: Int, navController: NavHostController) {
    val photoList = listOf(
        R.drawable.photo1,
        R.drawable.photo2,
        R.drawable.photo3
    )

    // Pantalla de detalle donde se muestra la imagen seleccionada
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = photoList[photoId]),
            contentDescription = "Selected Photo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de votar
        Button(onClick = { /* Simula votar */ }) {
            Text(text = "Votar por esta foto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver a la lista
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Volver a la lista")
        }
    }
}
@Composable
fun PhotoDetail(photoId: Int, navController: NavHostController) {
    val photoList = listOf(
        R.drawable.photo1,
        R.drawable.photo2,
        R.drawable.photo3
    )

    // Verifica que el photoId no sea mayor que el tamaño de photoList
    if (photoId >= photoList.size) {
        // Manejo de error: podría mostrar un mensaje o redirigir al usuario
        return
    }

    // Referencia a la base de datos de Firebase
    val database = FirebaseDatabase.getInstance().getReference("photos")

    // Estado local para manejar el número de votos
    var votes by remember { mutableStateOf(0) }

    // Escuchar cambios en Firebase (para obtener los votos actuales)
    LaunchedEffect(Unit) {
        database.child("photo_$photoId/votes").get()
            .addOnSuccessListener { dataSnapshot ->
                votes = dataSnapshot.getValue(Int::class.java) ?: 0
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseError", "Error al obtener votos: ", exception)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = photoList[photoId]),
            contentDescription = "Selected Photo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Votos: $votes")

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de votar
        Button(onClick = {
            // Incrementa el número de votos en Firebase
            database.child("photo_$photoId/votes").setValue(votes + 1)
        }) {
            Text(text = "Votar por esta foto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver a la lista
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Volver a la lista")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PhotoListPreview() {
    LigaF7Theme {
        val navController = rememberNavController()
        PhotoList(navController = navController)
    }
}
