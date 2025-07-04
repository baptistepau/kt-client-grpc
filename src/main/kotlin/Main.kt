import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.grpc.ManagedChannelBuilder
import product.Product
import product.ProductServiceGrpc

@Composable
fun ProductCard(name: String, price: Float, description: String) {
    Card(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(0.6f), // largeur relative à la fenêtre
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Prix : %.2f €".format(price),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
@Preview
fun App() {

    val channel = remember {
        ManagedChannelBuilder.forAddress("localhost", 50051)
            .usePlaintext()
            .build()
    }
    val stub = remember{ProductServiceGrpc.newBlockingStub(channel)}

    var name by remember { mutableStateOf("Nom du produit") }
    var description by remember { mutableStateOf("Description du produit") }
    var price by remember { mutableStateOf(0.0f) }

    var selectedId by remember { mutableIntStateOf(1) }
    val options = listOf(1, 2, 3)



    LaunchedEffect(selectedId) {
        val reponse = stub.getProduct(Product.GetProductRequest.newBuilder().setId(selectedId).build())
        name = reponse.name;
        description = reponse.description;
        price = reponse.price
    }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
        ) {
            SingleChoiceSegmentedButtonRow {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onClick = { selectedId = label },
                        selected = label == selectedId,
                        label = { Text(label.toString()) }
                    )
                }
            }
            ProductCard(
                name = name,
                price = price,
                description = description
            )
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
