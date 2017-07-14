/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package cz.msebera.android.httpclient.impl.nio;

import javax.net.ssl.SSLContext;

import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.HttpRequestFactory;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.annotation.Immutable;
import cz.msebera.android.httpclient.config.ConnectionConfig;
import cz.msebera.android.httpclient.entity.ContentLengthStrategy;
import cz.msebera.android.httpclient.impl.ConnSupport;
import cz.msebera.android.httpclient.impl.DefaultHttpRequestFactory;
import cz.msebera.android.httpclient.impl.nio.codecs.DefaultHttpRequestParserFactory;
import cz.msebera.android.httpclient.nio.NHttpConnectionFactory;
import cz.msebera.android.httpclient.nio.NHttpMessageParserFactory;
import cz.msebera.android.httpclient.nio.NHttpMessageWriterFactory;
import cz.msebera.android.httpclient.nio.reactor.IOSession;
import cz.msebera.android.httpclient.nio.reactor.ssl.SSLIOSession;
import cz.msebera.android.httpclient.nio.reactor.ssl.SSLMode;
import cz.msebera.android.httpclient.nio.reactor.ssl.SSLSetupHandler;
import cz.msebera.android.httpclient.nio.util.ByteBufferAllocator;
import cz.msebera.android.httpclient.nio.util.HeapByteBufferAllocator;
import cz.msebera.android.httpclient.params.HttpParamConfig;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.util.Args;

/**
 * Default factory for SSL encrypted, non-blocking
 * {@link cz.msebera.android.httpclient.nio.NHttpServerConnection}s.
 *
 * @since 4.2
 */
@SuppressWarnings("deprecation")
@Immutable
public class SSLNHttpServerConnectionFactory
    implements NHttpConnectionFactory<DefaultNHttpServerConnection> {

    private final SSLContext sslcontext;
    private final SSLSetupHandler sslHandler;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final NHttpMessageParserFactory<HttpRequest> requestParserFactory;
    private final NHttpMessageWriterFactory<HttpResponse> responseWriterFactory;
    private final ByteBufferAllocator allocator;
    private final ConnectionConfig cconfig;

    /**
     * @deprecated (4.3) use {@link
     *   SSLNHttpServerConnectionFactory#SSLNHttpServerConnectionFactory(SSLContext,
     *      SSLSetupHandler, NHttpMessageParserFactory, NHttpMessageWriterFactory,
     *      ByteBufferAllocator, ConnectionConfig)}
     */
    @Deprecated
    public SSLNHttpServerConnectionFactory(
            final SSLContext sslcontext,
            final SSLSetupHandler sslHandler,
            final HttpRequestFactory requestFactory,
            final ByteBufferAllocator allocator,
            final HttpParams params) {
        super();
        Args.notNull(requestFactory, "HTTP request factory");
        Args.notNull(allocator, "Byte buffer allocator");
        Args.notNull(params, "HTTP parameters");
        this.sslcontext = sslcontext;
        this.sslHandler = sslHandler;
        this.incomingContentStrategy = null;
        this.outgoingContentStrategy = null;
        this.requestParserFactory = new DefaultHttpRequestParserFactory(null, requestFactory);
        this.responseWriterFactory = null;
        this.allocator = allocator;
        this.cconfig = HttpParamConfig.getConnectionConfig(params);
    }

    /**
     * @deprecated (4.3) use {@link
     *   SSLNHttpServerConnectionFactory#SSLNHttpServerConnectionFactory(SSLContext,
     *     SSLSetupHandler, ConnectionConfig)}
     */
    @Deprecated
    public SSLNHttpServerConnectionFactory(
            final SSLContext sslcontext,
            final SSLSetupHandler sslHandler,
            final HttpParams params) {
        this(sslcontext, sslHandler, DefaultHttpRequestFactory.INSTANCE,
                HeapByteBufferAllocator.INSTANCE, params);
    }

    /**
     * @deprecated (4.3) use {@link
     *   SSLNHttpServerConnectionFactory#SSLNHttpServerConnectionFactory(ConnectionConfig)}
     */
    @Deprecated
    public SSLNHttpServerConnectionFactory(final HttpParams params) {
        this(null, null, params);
    }

    /**
     * @since 4.3
     */
    public SSLNHttpServerConnectionFactory(
            final SSLContext sslcontext,
            final SSLSetupHandler sslHandler,
            final ContentLengthStrategy incomingContentStrategy,
            final ContentLengthStrategy outgoingContentStrategy,
            final NHttpMessageParserFactory<HttpRequest> requestParserFactory,
            final NHttpMessageWriterFactory<HttpResponse> responseWriterFactory,
            final ByteBufferAllocator allocator,
            final ConnectionConfig cconfig) {
        super();
        this.sslcontext = sslcontext;
        this.sslHandler = sslHandler;
        this.incomingContentStrategy = incomingContentStrategy;
        this.outgoingContentStrategy = outgoingContentStrategy;
        this.requestParserFactory = requestParserFactory;
        this.responseWriterFactory = responseWriterFactory;
        this.allocator = allocator;
        this.cconfig = cconfig != null ? cconfig : ConnectionConfig.DEFAULT;
    }

    /**
     * @since 4.3
     */
    public SSLNHttpServerConnectionFactory(
            final SSLContext sslcontext,
            final SSLSetupHandler sslHandler,
            final NHttpMessageParserFactory<HttpRequest> requestParserFactory,
            final NHttpMessageWriterFactory<HttpResponse> responseWriterFactory,
            final ByteBufferAllocator allocator,
            final ConnectionConfig cconfig) {
        this(sslcontext, sslHandler,
                null, null, requestParserFactory, responseWriterFactory, allocator, cconfig);
    }

    /**
     * @since 4.3
     */
    public SSLNHttpServerConnectionFactory(
            final SSLContext sslcontext,
            final SSLSetupHandler sslHandler,
            final NHttpMessageParserFactory<HttpRequest> requestParserFactory,
            final NHttpMessageWriterFactory<HttpResponse> responseWriterFactory,
            final ConnectionConfig cconfig) {
        this(sslcontext, sslHandler,
                null, null, requestParserFactory, responseWriterFactory, null, cconfig);
    }

    /**
     * @since 4.3
     */
    public SSLNHttpServerConnectionFactory(
            final SSLContext sslcontext,
            final SSLSetupHandler sslHandler,
            final ConnectionConfig config) {
        this(sslcontext, sslHandler, null, null, null, null, null, config);
    }

    /**
     * @since 4.3
     */
    public SSLNHttpServerConnectionFactory(final ConnectionConfig config) {
        this(null, null, null, null, null, null, null, config);
    }

    /**
     * @since 4.3
     */
    public SSLNHttpServerConnectionFactory() {
        this(null, null, null, null, null, null, null, null);
    }

    /**
     * @deprecated (4.3) no longer used.
     */
    @Deprecated
    protected DefaultNHttpServerConnection createConnection(
            final IOSession session,
            final HttpRequestFactory requestFactory,
            final ByteBufferAllocator allocator,
            final HttpParams params) {
        return new DefaultNHttpServerConnection(session, requestFactory, allocator, params);
    }

    /**
     * @since 4.3
     */
    protected SSLIOSession createSSLIOSession(
            final IOSession iosession,
            final SSLContext sslcontext,
            final SSLSetupHandler sslHandler) {
        final SSLIOSession ssliosession = new SSLIOSession(iosession, SSLMode.SERVER,
                (sslcontext != null ? sslcontext : SSLContextUtils.getDefault()),
                sslHandler);
        return ssliosession;
    }

    public DefaultNHttpServerConnection createConnection(final IOSession iosession) {
        final SSLIOSession ssliosession = createSSLIOSession(iosession, this.sslcontext, this.sslHandler);
        iosession.setAttribute(SSLIOSession.SESSION_KEY, ssliosession);
        return new DefaultNHttpServerConnection(ssliosession,
                this.cconfig.getBufferSize(),
                this.cconfig.getFragmentSizeHint(),
                this.allocator,
                ConnSupport.createDecoder(this.cconfig),
                ConnSupport.createEncoder(this.cconfig),
                this.cconfig.getMessageConstraints(),
                this.incomingContentStrategy,
                this.outgoingContentStrategy,
                this.requestParserFactory,
                this.responseWriterFactory);
    }

}
