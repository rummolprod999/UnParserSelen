package enterit.tools

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.net.URL
import java.security.cert.X509Certificate
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.net.ssl.*

fun downloadFromUrl(
    urls: String,
    i: Int = 3,
    wt: Long = 5000,
): String {
    var count = 0
    while (true) {
        // val i = 50
        if (count >= i) {
            logger(String.format("Не скачали строку за %d попыток", count), urls)
            break
        }
        try {
            var s: String
            val executor = Executors.newCachedThreadPool()
            val task = { downloadWaitWithRef(urls) }
            val future = executor.submit(task)
            try {
                s = future.get(60, TimeUnit.SECONDS)
            } catch (ex: TimeoutException) {
                throw ex
            } catch (ex: InterruptedException) {
                throw ex
            } catch (ex: ExecutionException) {
                throw ex
            } catch (ex: Exception) {
                throw ex
            } finally {
                future.cancel(true)
                executor.shutdown()
            }
            return s
        } catch (e: Exception) {
            // logger(e, e.stackTrace)
            count++
            sleep(wt)
        }
    }
    return ""
}

fun downloadFromUrlMosreg(
    urls: String,
    i: Int = 3,
    wt: Long = 5000,
    refferer: String = "",
): String {
    var count = 0
    while (true) {
        // val i = 50
        if (count >= i) {
            logger(String.format("Не скачали строку за %d попыток", count), urls)
            break
        }
        try {
            var s: String
            val executor = Executors.newCachedThreadPool()
            val task = { downloadWaitWithRefMosreg(urls, refferer) }
            val future = executor.submit(task)
            try {
                s = future.get(60, TimeUnit.SECONDS)
            } catch (ex: TimeoutException) {
                throw ex
            } catch (ex: InterruptedException) {
                throw ex
            } catch (ex: ExecutionException) {
                throw ex
            } catch (ex: Exception) {
                throw ex
            } finally {
                future.cancel(true)
                executor.shutdown()
            }
            return s
        } catch (e: Exception) {
            logger(e, e.stackTrace)
            count++
            sleep(wt)
        }
    }
    return ""
}

fun downloadWait(urls: String): String {
    val s = StringBuilder()
    val url = URL(urls)
    val `is`: InputStream = url.openStream()
    val br = BufferedReader(InputStreamReader(`is`))
    var inputLine: String?
    var value = true
    while (value) {
        inputLine = br.readLine()
        if (inputLine == null) {
            value = false
        } else {
            s.append(inputLine)
        }
    }
    br.close()
    `is`.close()
    return s.toString()
}

fun downloadWaitWithRef(urls: String): String {
    val s = StringBuilder()
    val url = URL(urls)
    val uc = url.openConnection()
    uc.connectTimeout = 30_000
    uc.readTimeout = 600_000
    uc.addRequestProperty("User-Agent", RandomUserAgent.randomUserAgent)
    uc.connect()
    val `is`: InputStream = uc.getInputStream()
    val br = BufferedReader(InputStreamReader(`is`))
    var inputLine: String?
    var value = true
    while (value) {
        inputLine = br.readLine()
        if (inputLine == null) {
            value = false
        } else {
            s.append(inputLine)
        }
    }
    br.close()
    `is`.close()
    return s.toString()
}

fun downloadWaitWithRefMosreg(
    urls: String,
    refferer: String = "",
): String {
    val s = StringBuilder()
    val trustAllCerts =
        arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate>? = null

                override fun checkClientTrusted(
                    certs: Array<X509Certificate>,
                    authType: String,
                ) {
                }

                override fun checkServerTrusted(
                    certs: Array<X509Certificate>,
                    authType: String,
                ) {
                }
            },
        )
    val sc = SSLContext.getInstance("SSL")
    sc.init(null, trustAllCerts, java.security.SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
    val allHostsValid = HostnameVerifier { hostname, session -> true }
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
    val url = URL(urls)
    val uc = url.openConnection()
    uc.connectTimeout = 30_000
    uc.readTimeout = 600_000
    uc.addRequestProperty("User-Agent", RandomUserAgent.randomUserAgent)
    if (refferer != "") {
        uc.addRequestProperty("Referer", refferer)
        uc.addRequestProperty("Accept", "*/*")
        uc.addRequestProperty("XXX-TenantId-Header", "2")
        uc.addRequestProperty("Sec-Fetch-Mode", "cors")
    }
    uc.connect()
    val `is`: InputStream = uc.getInputStream()
    val br = BufferedReader(InputStreamReader(`is`))
    var inputLine: String?
    var value = true
    while (value) {
        inputLine = br.readLine()
        if (inputLine == null) {
            value = false
        } else {
            s.append(inputLine)
        }
    }
    br.close()
    `is`.close()
    return s.toString()
}

fun downloadFromUrl1251(
    urls: String,
    i: Int = 3,
): String {
    var count = 0
    while (true) {
        // val i = 50
        if (count >= i) {
            logger(String.format("Не скачали строку за %d попыток", count), urls)
            break
        }
        try {
            var s: String
            val executor = Executors.newCachedThreadPool()
            val task = { downloadWaitWithRef1251(urls) }
            val future = executor.submit(task)
            try {
                s = future.get(60, TimeUnit.SECONDS)
            } catch (ex: TimeoutException) {
                throw ex
            } catch (ex: InterruptedException) {
                throw ex
            } catch (ex: ExecutionException) {
                throw ex
            } catch (ex: Exception) {
                throw ex
            } finally {
                future.cancel(true)
                executor.shutdown()
            }
            return s
        } catch (e: Exception) {
            // logger(e, e.stackTrace)
            count++
            sleep(5000)
        }
    }
    return ""
}

fun downloadWaitWithRef1251(urls: String): String {
    val s = StringBuilder()
    val url = URL(urls)
    val uc = url.openConnection()
    uc.connectTimeout = 30_000
    uc.readTimeout = 600_000
    uc.addRequestProperty(
        "User-Agent",
        "Mozilla/5.0 (compatible; MSIE 10.6; Windows NT 6.1; Trident/5.0; InfoPath.2; SLCC1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 2.0.50727) 3gpp-gba UNTRUSTED/1.0",
    )
    uc.connect()
    val `is`: InputStream = uc.getInputStream()
    val br = BufferedReader(InputStreamReader(`is`, "windows-1251"))
    var inputLine: String?
    var value = true
    while (value) {
        inputLine = br.readLine()
        if (inputLine == null) {
            value = false
        } else {
            s.append(inputLine)
        }
    }
    br.close()
    `is`.close()
    return s.toString()
}
