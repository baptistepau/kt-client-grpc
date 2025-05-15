import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.grpc.ManagedChannelBuilder
import product.Product
import product.ProductServiceGrpc

@Composable
@Preview
fun App() {

    var name by remember { mutableStateOf("Nom du produit") }
    var description by remember { mutableStateOf("Description du produit") }

    val channel = remember {
        ManagedChannelBuilder.forAddress("localhost", 50051)
            .usePlaintext()
            .build()
    }
    val stub = remember{ProductServiceGrpc.newBlockingStub(channel)}

    LaunchedEffect(Unit) {
        val reponse = stub.getProduct(Product.GetProductRequest.newBuilder().setId(2).build())
        name = reponse.name;
        description = reponse.description;
    }



    MaterialTheme {
        Column {
            Text(text = "Nom du produit : $name")
            Text(text = "Description : $description")
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
