package com.example.okhttp_lib.response;


import com.example.okhttp_lib.listener.OkHttpRequestListener;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by Cg on 2017/9/27.
 */

public class OkHttpResponseBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private BufferedSource mBufferedSource;
    private WeakReference<OkHttpRequestListener> mListenerReference;

    public OkHttpResponseBody(ResponseBody responseBody, OkHttpRequestListener downloadListener) {
        this.mResponseBody = responseBody;
        this.mListenerReference = new WeakReference<>(downloadListener);
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += (bytesRead != -1 ? bytesRead : 0);
                OkHttpRequestListener listener = mListenerReference.get();
                if (listener != null) {
                    listener.onDownloadProgress(totalBytesRead, contentLength(), bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }
}
