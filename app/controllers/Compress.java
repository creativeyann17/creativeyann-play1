package controllers;

import play.*;
import play.cache.Cache;
import play.mvc.*;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.googlecode.htmlcompressor.compressor.XmlCompressor;
import com.googlecode.htmlcompressor.compressor.YuiCssCompressor;
import com.googlecode.htmlcompressor.compressor.YuiJavaScriptCompressor;

public class Compress extends Controller {

	@Finally
	public static void compress() throws IOException {

		if (isGzipSupported()) {

			String text = response.out.toString();

			int before = text.length();

			if (isCompressXMLSupported() && "text/xml".equals(response.contentType)) {
				text = new XmlCompressor().compress(text);
			} else if ( isCompressHTMLSupported() && "text/html; charset=utf-8".equals(response.contentType)) {
				text = new HtmlCompressor().compress(text);
			} // TODO json ?

			final ByteArrayOutputStream gzip = gzip(text);
			response.setHeader("Content-Encoding", "gzip");
			response.setHeader("Content-Length", String.valueOf(gzip.size()));
			response.out = gzip;

			Logger.debug("[%s] Action: %s Compress %s -> %s -> %s Bytes", response.contentType, request.action, before, text.length(), gzip.size());
		}
	}
	
	protected static boolean isCompressHTMLSupported(){
		return Boolean.parseBoolean(play.Play.configuration.getProperty("optimization.compressHTML", "false"));
	}
	
	protected static boolean isCompressXMLSupported(){
		return Boolean.parseBoolean(play.Play.configuration.getProperty("optimization.compressXML", "false"));
	}

	protected static boolean isGzipSupported() {
		final Http.Header encodingHeader = request.headers.get("accept-encoding");
		return (encodingHeader != null && encodingHeader.value().contains("gzip")
				&& Boolean.parseBoolean(play.Play.configuration.getProperty("optimization.gzip", "false")));
	}

	public static ByteArrayOutputStream gzip(final String input) throws IOException {

		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		final ByteArrayOutputStream stringOutputStream = new ByteArrayOutputStream((int) (input.length() * 0.75));
		final OutputStream gzipOutputStream = new GZIPOutputStream(stringOutputStream);

		IOUtils.copy(inputStream, gzipOutputStream);

		IOUtils.closeQuietly(inputStream);
		IOUtils.closeQuietly(gzipOutputStream);

		return stringOutputStream;
	}

}