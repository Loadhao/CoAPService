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

package cz.msebera.android.httpclient.nio.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.HttpEntityWrapper;
import cz.msebera.android.httpclient.nio.ContentDecoder;
import cz.msebera.android.httpclient.nio.IOControl;

/**
 * A {@link ConsumingNHttpEntity} that forwards available content to a
 * {@link ContentListener}.
 *
 * @since 4.0
 *
 * @deprecated use (4.2)
 *  {@link cz.msebera.android.httpclient.nio.protocol.BasicAsyncRequestProducer}
 *  or {@link cz.msebera.android.httpclient.nio.protocol.BasicAsyncResponseProducer}
 */
@Deprecated
public class ConsumingNHttpEntityTemplate
    extends HttpEntityWrapper implements ConsumingNHttpEntity {

    private final ContentListener contentListener;

    public ConsumingNHttpEntityTemplate(
            final HttpEntity httpEntity,
            final ContentListener contentListener) {
        super(httpEntity);
        this.contentListener = contentListener;
    }

    public ContentListener getContentListener() {
        return contentListener;
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Does not support blocking methods");
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public void writeTo(final OutputStream out) throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Does not support blocking methods");
    }

    public void consumeContent(
            final ContentDecoder decoder,
            final IOControl ioctrl) throws IOException {
        this.contentListener.contentAvailable(decoder, ioctrl);
    }

    public void finish() {
        this.contentListener.finished();
    }

}
