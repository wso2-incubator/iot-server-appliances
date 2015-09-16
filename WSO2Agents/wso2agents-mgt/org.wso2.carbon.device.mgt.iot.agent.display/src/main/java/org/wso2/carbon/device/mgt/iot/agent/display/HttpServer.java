package org.wso2.carbon.device.mgt.iot.agent.kura.display;

import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.protocol.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.ssl.SSLContexts;
import org.wso2.carbon.device.mgt.iot.agent.kura.display.resource.ResourceUtil;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Embedded HTTP/1.1 file server based on a non-blocking I/O model and capable of direct channel
 * (zero copy) data transfer.
 */
public class HttpServer implements Runnable{

	private final static Logger log = Logger.getLogger(HttpServer.class.getName());

	private File serverDocRoot = null;
	private int serverPort = 8000;

	public HttpServer(File serverDocRoot, int serverPort) {
		this.serverDocRoot = serverDocRoot;
		this.serverPort = serverPort;
	}

	public void run(){
		log.info("Starting HttpServer @" + serverDocRoot.getAbsolutePath() + " Port:" + serverPort);
		SSLContext sslcontext = null;
		if (serverPort == 8443) {
			// Initialize SSL context
			URL url = HttpServer.class.getResource(
					File.separator + LauncherConstants.CARBON_KEYSTORE_PATH);
			if (url == null) {
				System.out.println("Keystore not found");
				System.exit(1);
			}
			try {
				sslcontext = SSLContexts.custom().loadKeyMaterial(url,
																  LauncherConstants.CARBON_KEYSTORE_SECRET
																		  .toCharArray(),
																  LauncherConstants.CARBON_KEYSTORE_SECRET
																		  .toCharArray()).build();
			} catch (NoSuchAlgorithmException |KeyManagementException|KeyStoreException |UnrecoverableKeyException |CertificateException |IOException e) {
				log.severe("Error on adding SSL key " + e.getMessage());
			}
		}

		IOReactorConfig config = IOReactorConfig.custom().setSoTimeout(15000).setTcpNoDelay(true)
				.build();

		final org.apache.http.impl.nio.bootstrap.HttpServer server = ServerBootstrap.bootstrap()
				.setListenerPort(serverPort).setServerInfo("Test/1.1").setIOReactorConfig(config)
				.setSslContext(sslcontext).setExceptionLogger(ExceptionLogger.STD_ERR)
				.registerHandler("*", new HttpFileHandler(serverDocRoot)).create();

		try {
			server.start();
			server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		}catch (IOException |InterruptedException e) {
			log.severe("Error on server start " + e.getMessage());
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				server.shutdown(5, TimeUnit.SECONDS);
			}
		});
		log.info("HttpServer Started...");
	}

	static class HttpFileHandler implements HttpAsyncRequestHandler<HttpRequest> {

		private static final String[] DEFAULT_INDEX_FILES_LIST = new String[] { "index.html",
				"index.htm" };

		private final File docRoot;

		public HttpFileHandler(final File docRoot) {
			super();
			this.docRoot = docRoot;
		}

		public HttpAsyncRequestConsumer<HttpRequest> processRequest(final HttpRequest request,
																	final HttpContext context) {
			// Buffer request content in memory for simplicity
			return new BasicAsyncRequestConsumer();
		}

		public void handle(final HttpRequest request, final HttpAsyncExchange httpexchange,
						   final HttpContext context) throws HttpException, IOException {
			HttpResponse response = httpexchange.getResponse();
			handleInternal(request, response, context);
			httpexchange.submitResponse(new BasicAsyncResponseProducer(response));
		}

		private void handleInternal(final HttpRequest request, final HttpResponse response,
									final HttpContext context) throws HttpException, IOException {

			String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
			if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
				throw new MethodNotSupportedException(method + " method not supported");
			}

			String target = request.getRequestLine().getUri();
			File file = new File(this.docRoot, URLDecoder.decode(target, "UTF-8"));
			if (!file.exists()) {

				response.setStatusCode(HttpStatus.SC_NOT_FOUND);
				log.severe("File " + file.getPath() + " not found");
				return;

			} else if (!file.canRead()) {

				response.setStatusCode(HttpStatus.SC_FORBIDDEN);
				log.severe("Cannot read file " + file.getPath());
				return;

			} else if (file.isDirectory()) {

				if (target.endsWith("/")) {

					boolean isExist = false;
					for (String index : DEFAULT_INDEX_FILES_LIST) {
						final File t_file = new File(this.docRoot + File.separator + target, index);
						if (t_file.exists()) {
							file = t_file;
							isExist = true;
							break;
						}
					}
					if(!isExist)return;

				} else {
					//redirect browser - doing basically what apache does
					response.setStatusCode(HttpStatus.SC_MOVED_PERMANENTLY);
					String newUrl = target + "/";
					response.setHeader("Location", newUrl);
					return;
				}

			}

			HttpCoreContext coreContext = HttpCoreContext.adapt(context);
			HttpConnection conn = coreContext.getConnection(HttpConnection.class);
			response.setStatusCode(HttpStatus.SC_OK);

			String displayAgentHome = ResourceUtil.getDisplayAgentHome();
			System.setProperty("content.types.user.table",displayAgentHome + File.separator + LauncherConstants.CONF_PATH + File.separator + "content-types.properties");
			String mimeType= URLConnection.guessContentTypeFromName(file.getName());

			NFileEntity body = new NFileEntity(file, ContentType.create(mimeType));
			response.setEntity(body);
			//log.info(conn + ": serving file " + file.getPath());
		}

	}

}