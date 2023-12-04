package APIclient

import com.google.auth.oauth2.IdToken
import com.google.auth.oauth2.IdTokenProvider
import com.google.auth.oauth2.ServiceAccountCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream


object TokenClient {

    suspend fun getIdToken(type: String): String {

        // The url or target audience to obtain the ID token for.
        val targetAudienceKtor = "https://calendar-app-server-ycd64g7ulq-pd.a.run.app"
        val targetAudienceSpring = "https://calendar-app-planner-server-ycd64g7ulq-pd.a.run.app"

        try {
            val resourceDir = File(System.getProperty("compose.application.resources.dir"))
            val serviceAccountCredentials: ServiceAccountCredentials =
                withContext(Dispatchers.IO) {
                    ServiceAccountCredentials.fromStream(FileInputStream(resourceDir.resolve("calendarapp346-db9002f5232a.json")))
                }

            var token = ""
            // Obtain the id token by providing the target audience.
            // tokenOption: Enum of various credential-specific options to apply to the token. Applicable
            // only for credentials obtained through Compute Engine or Impersonation.
            val tokenOption: List<IdTokenProvider.Option> = mutableListOf<IdTokenProvider.Option>()
            if (type == "ktor") {
                val idToken: IdToken = serviceAccountCredentials.idTokenWithAudience(targetAudienceKtor, tokenOption)
                // The following method can also be used to generate the ID token.
                // IdTokenCredentials idTokenCredentials = IdTokenCredentials.newBuilder()
                //     .setIdTokenProvider(serviceAccountCredentials)
                //     .setTargetAudience(targetAudience)
                //     .build();
                token = idToken.getTokenValue()
            } else {
                val idToken: IdToken = serviceAccountCredentials.idTokenWithAudience(targetAudienceSpring, tokenOption)
                // The following method can also be used to generate the ID token.
                // IdTokenCredentials idTokenCredentials = IdTokenCredentials.newBuilder()
                //     .setIdTokenProvider(serviceAccountCredentials)
                //     .setTargetAudience(targetAudience)
                //     .build();
                token = idToken.getTokenValue()
            }

            return token

        } catch (e: Exception) {
            println("Failed to obtain ID token")
        }
        return ""
    }
}