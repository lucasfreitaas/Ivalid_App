package com.example.ivalid_compose
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ivalid_compose.ui.cart.CartScreen
import com.example.ivalid_compose.ui.cart.CartViewModel
import com.example.ivalid_compose.ui.checkout.CheckoutScreen
import com.example.ivalid_compose.ui.checkout.CheckoutViewModel
import com.example.ivalid_compose.ui.home.HomeScreen
import com.example.ivalid_compose.ui.home.HomeViewModel
import com.example.ivalid_compose.ui.login.LoginScreen
import com.example.ivalid_compose.ui.login.LoginViewModel
import com.example.ivalid_compose.ui.product.ProductDetailsScreen
import com.example.ivalid_compose.ui.signup.SignUpScreen
import com.example.ivalid_compose.ui.signup.SignUpViewModel
import com.example.ivalid_compose.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    val cartVm: CartViewModel = viewModel()
    val homeVm: HomeViewModel = viewModel()

    Surface {
        NavHost(
            navController = nav,
            startDestination = "login"
        ) {
            composable("login") {
                val vm: LoginViewModel = viewModel()
                LoginScreen(
                    viewModel = vm,
                    navController = nav,
                    onForgotPassword = { /* TODO */},
                    onSignUp = {nav.navigate("signup")}
                )
            }

            composable("signup") {
                val vm: SignUpViewModel = viewModel()
                SignUpScreen(
                    viewModel = vm,
                    onBackToLogin = { nav.popBackStack() },
                    onShowTerms = { /* abrir termos/política */ },
                    onAccountCreated = {
                        nav.navigate("home") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable("home") {
                HomeScreen(
                    viewModel = homeVm,
                    onOpenProduct = { product -> nav.navigate("product/${product.id}") },
                    cartCount = cartVm.uiState.count,       // badge se atualiza com o state
                    onOpenCart = { nav.navigate("cart") }
                )
            }

            composable(
                route = "product/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStack ->
                val productId = backStack.arguments?.getString("productId")
                val product = homeVm.uiState.allProducts.find { it.id == productId }

                ProductDetailsScreen(
                    product = product,
                    onBack = { nav.popBackStack() },
                    onAddedToCart = {
                        nav.navigate("cart")
                    },
                    cartViewModel = cartVm
                )
            }

            composable("cart") {
                CartScreen(
                    cartViewModel = cartVm,
                    onBack = { nav.popBackStack() },
                    onCheckout = {
                        nav.navigate("checkout")
                    }
                )
            }

            composable("checkout"){
                val vm: CheckoutViewModel = viewModel()
                CheckoutScreen(
                    viewModel = vm,
                    cartViewModel = cartVm,
                    onFinalizarPedido = {
                        vm.finalizarPedido()
                    },
                    onTrocarEndereco = {
                    },
                    onBack = {
                        nav.popBackStack()
                    }
                )
            }

        }
    }
}