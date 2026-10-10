package com.bemychef.app

/**
 * The app holds exactly one address: the Be My Chef site on Netlify. There is
 * no API key here — the Groq key lives only in the Netlify function's own
 * environment variable, server-side, never in anything shipped to a device.
 */
object ApiConfig {
    val baseUrl: String = BuildConfig.API_BASE_URL
}
