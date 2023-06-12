package http

import com.zyf.pokemon.api.PokemonApi
import http.interceptor.CookieIntercept
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object PKRetrofitClient {
    private var pkApi: PokemonApi? = null
    fun getPokemonApi(): PokemonApi {
        if (pkApi == null) {
            pkApi = RetrofitClient.getApi(PokemonApi::class.java)
        }
        return pkApi!!
    }
}

object RetrofitClient {
    const val BASE_URL = "https://pokeapi.co/api/v2/"
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 10L
    fun <T> getApi(retrofit: Class<T>): T = createRetrofit().create(retrofit)

    private fun createRetrofit(url: String = BASE_URL): Retrofit {
        // okHttpClientBuilder
        val okHttpClientBuilder = OkHttpClient().newBuilder().apply {
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            addInterceptor(CookieIntercept())
        }

        return RetrofitBuild(
            url = url,
            client = okHttpClientBuilder.build(),
            gsonFactory = GsonConverterFactory.create()
        ).retrofit
    }

}

class RetrofitBuild(
    url: String, client: OkHttpClient,
    gsonFactory: GsonConverterFactory
) {
    val retrofit: Retrofit = Retrofit.Builder().apply {
        baseUrl(url)
        client(client)
        addConverterFactory(gsonFactory)

    }.build()
}