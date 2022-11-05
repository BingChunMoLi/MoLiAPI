package com.bingchunmoli.api.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 不支持的http响应状态码枚举
 * @author bingchunmoli
 */
@Getter
@AllArgsConstructor
public enum NotSupportHttpCode {
    /**
     * {@code 103 Checkpoint}.
     * @see <a href="https://code.google.com/p/gears/wiki/ResumableHttpRequestsProposal">A proposal for supporting
     * resumable POST/PUT HTTP requests in HTTP/1.0</a>
     */
    CHECKPOINT(103, HttpStatus.Series.INFORMATIONAL, "Checkpoint"),
    /**
     * {@code 205 Reset Content}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.6">HTTP/1.1: Semantics and Content, section 6.3.6</a>
     */
    RESET_CONTENT(205, HttpStatus.Series.SUCCESSFUL, "Reset Content"),
    /**
     * {@code 208 Already Reported}.
     * @see <a href="https://tools.ietf.org/html/rfc5842#section-7.1">WebDAV Binding Extensions</a>
     */
    ALREADY_REPORTED(208, HttpStatus.Series.SUCCESSFUL, "Already Reported"),
    /**
     * {@code 226 IM Used}.
     * @see <a href="https://tools.ietf.org/html/rfc3229#section-10.4.1">Delta encoding in HTTP</a>
     */
    IM_USED(226, HttpStatus.Series.SUCCESSFUL, "IM Used"),
    /**
     * @deprecated See
     * <a href="https://tools.ietf.org/rfcdiff?difftype=--hwdiff&amp;url2=draft-ietf-webdav-protocol-06.txt">
     *     WebDAV Draft Changes</a>
     */
    @Deprecated
    INSUFFICIENT_SPACE_ON_RESOURCE(419, HttpStatus.Series.CLIENT_ERROR, "Insufficient Space On Resource"),
    /**
     * {@code 428 Precondition Required}.
     * @see <a href="https://tools.ietf.org/html/rfc6585#section-3">Additional HTTP Status Codes</a>
     */
    PRECONDITION_REQUIRED(428, HttpStatus.Series.CLIENT_ERROR, "Precondition Required"),
    /**
     * {@code 505 HTTP Version Not Supported}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.6">HTTP/1.1: Semantics and Content, section 6.6.6</a>
     */
    HTTP_VERSION_NOT_SUPPORTED(505, HttpStatus.Series.SERVER_ERROR, "HTTP Version not supported");

    private final int value;

    private final HttpStatus.Series series;

    private final String reasonPhrase;
}
