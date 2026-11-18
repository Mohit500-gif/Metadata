package com.digi.uniprr.config;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class UniprrHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] requestBody;

    public UniprrHttpServletRequestWrapper(HttpServletRequest request, String requestBody) throws IOException {
        super(request);
        this.requestBody = requestBody.getBytes();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CustomServletInputStream(requestBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    private static class CustomServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream inputStream;

        public CustomServletInputStream(byte[] requestBody) {
            this.inputStream = new ByteArrayInputStream(requestBody);
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // Not implemented
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return inputStream.read(b, off, len);
        }
    }
}
