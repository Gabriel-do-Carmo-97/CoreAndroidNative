//package br.com.wgc.core.extensions
//
//@Composable
//fun rememberPermission(
//    permission: String,
//    onResult: (isGranted: Boolean) -> Unit
//): () -> Unit {
//    val context = LocalContext.current
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = onResult
//    )
//    ​
//    return remember {
//        {
//            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
//                onResult(true)
//            } else {
//                launcher.launch(permission)
//            }
//        }
//    }
//}
