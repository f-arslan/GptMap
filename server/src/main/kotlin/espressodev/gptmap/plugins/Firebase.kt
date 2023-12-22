package espressodev.gptmap.plugins

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import java.io.FileInputStream


fun Application.configureFirebase() {
    val serviceAccount = FileInputStream("keyfile.json")
    val options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build()
    FirebaseApp.initializeApp(options)
}