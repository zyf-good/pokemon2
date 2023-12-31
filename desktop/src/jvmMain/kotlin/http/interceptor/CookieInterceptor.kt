package http.interceptor

import okhttp3.Interceptor
import okhttp3.Response


class CookieIntercept : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}