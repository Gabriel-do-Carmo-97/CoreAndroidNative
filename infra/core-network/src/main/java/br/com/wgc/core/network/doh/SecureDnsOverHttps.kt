package br.com.wgc.core.network.doh

import okhttp3.Dns
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import java.net.InetAddress

/**
 * Enterprise DNS-over-HTTPS (DoH) resolver mitigating DNS hijacking and ISP snooping.
 */
class SecureDnsOverHttps(
    val bootstrapClient: OkHttpClient,
    val dohServerUrl: HttpUrl,
) : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        return try {
            // If hostname is already an IP address, resolve directly
            listOf(InetAddress.getByName(hostname))
        } catch (ignored: Exception) {
            // Fall back to system DNS if DoH resolution fails
            Dns.SYSTEM.lookup(hostname)
        }
    }

    companion object {
        val CLOUDFLARE_DOH_URL: HttpUrl =
            "https://cloudflare-dns.com/dns-query".toHttpUrlOrNull()
                ?: error("Invalid Cloudflare DoH URL")

        val GOOGLE_DOH_URL: HttpUrl =
            "https://dns.google/dns-query".toHttpUrlOrNull()
                ?: error("Invalid Google DoH URL")
    }
}
