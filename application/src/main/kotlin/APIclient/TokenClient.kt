package APIclient

import com.google.auth.oauth2.IdToken
import com.google.auth.oauth2.IdTokenProvider
import com.google.auth.oauth2.ServiceAccountCredentials
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.GeneralSecurityException
import java.util.concurrent.ExecutionException


object TokenClient {



    suspend fun getIdToken() {

        val jsonCredentialPath = "path-to-json-credential-file"

        // The url or target audience to obtain the ID token for.
        val targetAudience = "https://calendar-app-server-ycd64g7ulq-pd.a.run.app"

        try {
            val serviceAccountCredentials: ServiceAccountCredentials =
                ServiceAccountCredentials.fromStream(FileInputStream(File(javaClass.getResource("/calendarapp346-db9002f5232a.json").toURI())))

            // Obtain the id token by providing the target audience.
            // tokenOption: Enum of various credential-specific options to apply to the token. Applicable
            // only for credentials obtained through Compute Engine or Impersonation.
            val tokenOption: List<IdTokenProvider.Option> = mutableListOf<IdTokenProvider.Option>()
            val idToken: IdToken = serviceAccountCredentials.idTokenWithAudience(targetAudience, tokenOption)

            // The following method can also be used to generate the ID token.
            // IdTokenCredentials idTokenCredentials = IdTokenCredentials.newBuilder()
            //     .setIdTokenProvider(serviceAccountCredentials)
            //     .setTargetAudience(targetAudience)
            //     .build();
            val token: String = idToken.getTokenValue()
            println(token);
            println("Generated ID token.")

        } catch (e: Exception) {
            println("Failed to obtain ID token")
        }
    }
}