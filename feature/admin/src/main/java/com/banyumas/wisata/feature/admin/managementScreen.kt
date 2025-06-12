package com.banyumas.wisata.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme
import com.banyumas.wisata.core.model.Category
import com.banyumas.wisata.core.model.Destination
import kotlinx.coroutines.launch

@Composable
fun ManageDestinationScreen(
    onBack: () -> Unit,
) {
//    val context = LocalContext.current
//    val editorState by viewModel.destinationsState.collectAsStateWithLifecycle()
//    val snackbarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()
//    var isLoading by remember { mutableStateOf(false) }
//
//    var name by rememberSaveable { mutableStateOf("") }
//    var address by rememberSaveable { mutableStateOf("") }
//    var category by rememberSaveable { mutableStateOf(Category.list.first()) }
//    var rating by rememberSaveable { mutableStateOf("") }
//
//    LaunchedEffect(editorState) {
//        if (editorState is UiState.Success) {
//            val destination = (editorState as UiState.Success<Destination>).data
//            name = destination.name
//            address = destination.address
//            category = destination.category
//            rating = destination.rating.toString()
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        viewModel.editorEvent.collect { event ->
//            when (event) {
//                is ManagementViewModel.EditorEvent.Loading -> isLoading = true
//                is ManagementViewModel.EditorEvent.Success -> {
//                    isLoading = false
//                    scope.launch {
//                        snackbarHostState.showSnackbar(event.message.asString(context)) // Perlu Context untuk StringResource
//                    }
//                    onBack() // Kembali setelah sukses
//                }
//
//                is ManagementViewModel.EditorEvent.Error -> {
//                    isLoading = false
//                    scope.launch {
//                        snackbarHostState.showSnackbar(event.message.asString(context)) // Perlu Context
//                    }
//                }
//            }
//        }
//    }
//
//    ManageDestinationContent(
//        snackbarHostState = snackbarHostState,
//        isEditMode = editorState is UiState.Success,
//        isLoading = isLoading,
//        name = name,
//        address = address,
//        category = category,
//        rating = rating,
//        onNameChange = { name = it },
//        onAddressChange = { address = it },
//        onCategoryChange = { category = it },
//        onRatingChange = { rating = it },
//        onSaveClick = {
//            viewModel.saveOrUpdateDestination(
//                name = name,
//                address = address,
//                category = category,
//            )
//        },
//        onBack = onBack
//    )
}

//@Composable
//internal fun ManageDestinationContent(
//    snackbarHostState: SnackbarHostState,
//    isEditMode: Boolean,
//    isLoading: Boolean,
//    name: String,
//    address: String,
//    category: String,
//    rating: String,
//    onNameChange: (String) -> Unit,
//    onAddressChange: (String) -> Unit,
//    onCategoryChange: (String) -> Unit,
//    onRatingChange: (String) -> Unit,
//    onSaveClick: () -> Unit,
//    onBack: () -> Unit,
//) {
//    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
//    Scaffold(
//        snackbarHost = { SnackbarHost(snackbarHostState) },
//        topBar = {
//            TopAppBar(
//                title = { Text(if (isEditMode) "Update Wisata" else "Tambah Wisata") },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Box(modifier = Modifier.fillMaxSize()) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues)
//                    .padding(horizontal = 16.dp)
//                    .verticalScroll(rememberScrollState()),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = onNameChange,
//                    label = { Text("Nama Wisata") },
//                    modifier = Modifier.fillMaxWidth(),
//                    enabled = !isLoading,
//                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
//                )
//                OutlinedTextField(
//                    value = address,
//                    onValueChange = onAddressChange,
//                    label = { Text("Alamat") },
//                    modifier = Modifier.fillMaxWidth(),
//                    enabled = !isLoading,
//                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
//                )
//
//                ExposedDropdownMenuBox(
//                    expanded = isCategoryDropdownExpanded,
//                    onExpandedChange = { if (!isLoading) isCategoryDropdownExpanded = it }
//                ) {
//                    OutlinedTextField(
//                        value = category,
//                        onValueChange = {},
//                        readOnly = true,
//                        label = { Text("Kategori") },
//                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
//                        modifier = Modifier
//                            .menuAnchor()
//                            .fillMaxWidth()
//                    )
//                    ExposedDropdownMenu(
//                        expanded = isCategoryDropdownExpanded,
//                        onDismissRequest = { isCategoryDropdownExpanded = false }
//                    ) {
//                        Category.list.forEach { selectionOption ->
//                            DropdownMenuItem(
//                                text = { Text(selectionOption) },
//                                onClick = {
//                                    onCategoryChange(selectionOption)
//                                    isCategoryDropdownExpanded = false
//                                }
//                            )
//                        }
//                    }
//                }
//                OutlinedTextField(
//                    value = rating,
//                    onValueChange = onRatingChange,
//                    label = { Text("Rating (0.0 - 5.0)") },
//                    modifier = Modifier.fillMaxWidth(),
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Number,
//                        imeAction = ImeAction.Done
//                    ),
//                    enabled = !isLoading
//                )
//
//                Spacer(Modifier.weight(1f))
//
//                Button(
//                    onClick = onSaveClick,
//                    enabled = !isLoading,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 8.dp)
//                ) {
//                    Text(text = if (isEditMode) "Update" else "Simpan")
//                }
//            }
//
//            if (isLoading) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .align(Alignment.Center)
//                ) {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                }
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true, name = "Add Mode")
//@Composable
//private fun ManageDestinationContent_AddMode_Preview() {
//    WisataBanyumasTheme {
//        ManageDestinationContent(
//            snackbarHostState = remember { SnackbarHostState() },
//            isEditMode = false,
//            isLoading = false,
//            name = "",
//            address = "",
//            category = "Alam",
//            rating = "",
//            onNameChange = {},
//            onAddressChange = {},
//            onCategoryChange = {},
//            onRatingChange = {},
//            onSaveClick = {},
//            onBack = {}
//        )
//    }
//}
//
//@Preview(showBackground = true, name = "Edit Mode")
//@Composable
//private fun ManageDestinationContent_EditMode_Preview() {
//    WisataBanyumasTheme {
//        ManageDestinationContent(
//            snackbarHostState = remember { SnackbarHostState() },
//            isEditMode = true,
//            isLoading = false,
//            name = "Curug Cipendok",
//            address = "Cilongok, Banyumas",
//            category = "Alam",
//            rating = "4.5",
//            onNameChange = {},
//            onAddressChange = {},
//            onCategoryChange = {},
//            onRatingChange = {},
//            onSaveClick = {},
//            onBack = {}
//        )
//    }
//}
//
//@Preview(showBackground = true, name = "Loading State")
//@Composable
//private fun ManageDestinationContent_Loading_Preview() {
//    WisataBanyumasTheme {
//        ManageDestinationContent(
//            snackbarHostState = remember { SnackbarHostState() },
//            isEditMode = false,
//            isLoading = true, // <-- Loading state
//            name = "Curug Cipendok",
//            address = "Cilongok, Banyumas",
//            category = "Alam",
//            rating = "4.5",
//            onNameChange = {},
//            onAddressChange = {},
//            onCategoryChange = {},
//            onRatingChange = {},
//            onSaveClick = {},
//            onBack = {}
//        )
//    }
//}