package com.example.ivalid_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.ivalid_compose.ui.checkout.CheckoutViewModelFactory
import com.example.ivalid_compose.ui.checkout.OrderConfirmationScreen
import com.example.ivalid_compose.ui.home.HomeScreen
import com.example.ivalid_compose.ui.home.HomeViewModel
import com.example.ivalid_compose.ui.home.BottomNavItem
import com.example.ivalid_compose.ui.login.LoginScreen
import com.example.ivalid_compose.ui.login.LoginViewModel
import com.example.ivalid_compose.ui.orders.OrdersScreen
import com.example.ivalid_compose.ui.product.ProductDetailsScreen
import com.example.ivalid_compose.ui.signup.SignUpScreen
import com.example.ivalid_compose.ui.signup.SignUpViewModel
import com.example.ivalid_compose.ui.theme.AppTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ivalid_compose.ui.profile.ProfileScreen
import com.example.ivalid_compose.ui.profile.ProfileViewModel
import com.example.ivalid_compose.ui.theme.RedPrimary

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
    val checkoutViewModelFactory = remember {
        CheckoutViewModelFactory(cartVm)
    }

    val navBackStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf("home", "orders", "profile", "settings")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = nav)
            }
        }
    ) { paddingValues ->
            NavHost(
                navController = nav,
                startDestination = "login",
                modifier = Modifier.padding(paddingValues)
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

                // Rotas de Nível Principal (Devem ter Scaffold INTERNO para TopBar, mas não BottomBar)

                composable("home") {
                    // NOTE: HomeScreen deve ter um Scaffold interno para a TopAppBar e FAB.
                    HomeScreen(
                        viewModel = homeVm,
                        onOpenProduct = { product -> nav.navigate("product/${product.id}") },
                        cartCount = cartVm.uiState.count,
                        navController = nav
                    )
                }

                composable("orders"){
                    // NOTE: OrdersScreen deve ter um Scaffold interno para a TopAppBar.
                    OrdersScreen(
                        onOpenOrderDetails = { orderId -> }
                    )
                }

                composable("profile") {
                    // Se esta tela for de nível principal, ela deve ter um Scaffold interno.
                }
                composable("settings") {
                    // Se esta tela for de nível principal, ela deve ter um Scaffold interno.
                }

                // Rotas de Detalhe (não mostram Bottom Bar)

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
                    val vm: CheckoutViewModel = viewModel(factory = checkoutViewModelFactory)
                    CheckoutScreen(
                        viewModel = vm,
                        cartViewModel = cartVm,
                        onFinalizarPedido = {
                            vm.finalizarPedido()
                            val mockOrderId = "TEMP-12345"
                            val mockTotal = 150.99f

                            nav.navigate("order_confirmation/$mockOrderId/$mockTotal") {
                                popUpTo("checkout") { inclusive = true }
                            }

                        },
                        onTrocarEndereco = {
                        },
                        onBack = {
                            nav.popBackStack()
                        }
                    )
                }

                composable(
                    route = "order_confirmation/{orderId}/{totalValue}",
                    arguments = listOf(
                        navArgument("orderId") { type = NavType.StringType },
                        navArgument("totalValue") { type = NavType.FloatType }
                    )
                ) { backStackEntry ->
                    val orderId = backStackEntry.arguments?.getString("orderId") ?: "N/A"
                    val totalValue = backStackEntry.arguments?.getFloat("totalValue")?.toDouble() ?: 0.0

                    OrderConfirmationScreen(
                        orderId = orderId,
                        totalValue = totalValue,
                        onBackToHome = {
                            nav.navigate("home") {
                                popUpTo("home") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable("profile") {
                    val vm: ProfileViewModel = viewModel()
                    ProfileScreen(
                        viewModel = vm,
                        onLogout = {
                            // Navega para a tela de login após o logout
                            nav.navigate("login") {
                                popUpTo(nav.graph.id) { inclusive = true }
                            }
                        }
                    )
                }
                composable("settings") {
                    // SettingsScreen()
                }
            }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Orders,
        BottomNavItem.Profile,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        // Removido o Modifier.height fixo aqui.
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(item.label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RedPrimary,
                    selectedTextColor = RedPrimary,
                    indicatorColor = RedPrimary.copy(alpha = 0.08f)
                )
            )
        }
    }
}