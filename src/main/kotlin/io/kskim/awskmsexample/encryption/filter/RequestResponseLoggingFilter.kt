package io.kskim.awskmsexample.encryption.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.kskim.awskmsexample.encryption.common.logger
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.StandardCharsets

@Component
class RequestResponseLoggingFilter(private val objectMapper: ObjectMapper) : OncePerRequestFilter() {
    private val log = logger()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val wrappedRequest = ContentCachingRequestWrapper(request)
        val wrappedResponse = ContentCachingResponseWrapper(response)

        filterChain.doFilter(wrappedRequest, wrappedResponse)

        if (log.isDebugEnabled) {
            logHttpTransaction(wrappedRequest, wrappedResponse)
        }

        wrappedResponse.copyBodyToResponse() // 응답 내용 복사 (중요)
    }

    private fun logHttpTransaction(request: ContentCachingRequestWrapper, response: ContentCachingResponseWrapper) {
        val clientIp = getClientIp(request)
        val requestBody = parseBody(request.contentAsByteArray)
        val responseBody = parseBody(response.contentAsByteArray)

        val logMessage = buildString {
            append("\n") // 가독성 개선
            append("--> ${request.method} ${request.requestURI}")
            request.queryString?.let { append("?").append(it) }
            append(" (Client IP: $clientIp)\n")

            // 요청 헤더
            request.headerNames.toList().forEach { header ->
                append("$header: ${request.getHeader(header)}\n")
            }

            // 요청 바디 (GET, DELETE는 body 제외)
            if (request.method !in listOf("GET", "DELETE")) {
                requestBody?.let { append("\n$it\n") }
            }

            append("\n<-- ${response.status} ${request.method} ${request.requestURI}\n")

            // 응답 헤더
            response.headerNames.forEach { header ->
                append("$header: ${response.getHeader(header)}\n")
            }

            // 응답 바디
            responseBody?.let { append("\n$it\n") }

            append("\n")
        }

        log.info(logMessage)
    }

    private fun parseBody(content: ByteArray): String? {
        if (content.isEmpty()) return null
        val bodyString = String(content, StandardCharsets.UTF_8)

        return try {
            objectMapper.readTree(bodyString).toPrettyString() // JSON이면 예쁘게 출력
        } catch (e: Exception) {
            bodyString // JSON이 아니면 그대로 출력
        }
    }

    private fun getClientIp(request: HttpServletRequest): String {
        return request.getHeader("X-Forwarded-For")?.split(",")?.first()?.trim()
            ?: request.getHeader("X-Real-IP")
            ?: request.remoteAddr
    }
}