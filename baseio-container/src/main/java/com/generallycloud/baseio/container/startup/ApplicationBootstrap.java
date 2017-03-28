/*
 * Copyright 2015-2017 GenerallyCloud.com
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.generallycloud.baseio.container.startup;

import java.io.File;

import com.generallycloud.baseio.acceptor.SocketChannelAcceptor;
import com.generallycloud.baseio.common.ClassUtil;
import com.generallycloud.baseio.common.CloseUtil;
import com.generallycloud.baseio.common.Encoding;
import com.generallycloud.baseio.common.FileUtil;
import com.generallycloud.baseio.common.Logger;
import com.generallycloud.baseio.common.LoggerFactory;
import com.generallycloud.baseio.common.SharedBundle;
import com.generallycloud.baseio.component.NioSocketChannelContext;
import com.generallycloud.baseio.component.SocketChannelContext;
import com.generallycloud.baseio.component.ssl.SSLUtil;
import com.generallycloud.baseio.component.ssl.SslContext;
import com.generallycloud.baseio.configuration.PropertiesSCLoader;
import com.generallycloud.baseio.configuration.ServerConfiguration;
import com.generallycloud.baseio.configuration.ServerConfigurationLoader;
import com.generallycloud.baseio.container.ApplicationContext;
import com.generallycloud.baseio.container.ApplicationContextEnricher;
import com.generallycloud.baseio.container.ApplicationExtLoader;
import com.generallycloud.baseio.container.ApplicationIoEventHandle;
import com.generallycloud.baseio.container.DefaultExtLoader;
import com.generallycloud.baseio.container.configuration.ApplicationConfiguration;
import com.generallycloud.baseio.container.configuration.ApplicationConfigurationLoader;
import com.generallycloud.baseio.container.configuration.FileSystemACLoader;

public class ApplicationBootstrap {

	public void bootstrap(ApplicationConfigurationLoader acLoader, String rootPath,
			ApplicationConfiguration ac, ServerConfiguration sc) throws Exception {

		SharedBundle bundle = SharedBundle.instance();

		if (rootPath == null) {
			rootPath = FileUtil.getCurrentPath();
		}

		ApplicationContext applicationContext = new ApplicationContext(rootPath, ac);

		SocketChannelContext channelContext = new NioSocketChannelContext(sc);
		//		SocketChannelContext channelContext = new AioSocketChannelContext(sc);

		SocketChannelAcceptor acceptor = new SocketChannelAcceptor(channelContext);

		try {

			applicationContext.setChannelContext(channelContext);

			ApplicationExtLoader applicationExtLoader = loadApplicationExtLoader(
					bundle.getProperty("intf.ApplicationExtLoader"));

			ApplicationContextEnricher enricher = loadApplicationContextEnricher(
					bundle.getProperty("intf.ApplicationContextEnricher"));

			applicationContext.setApplicationExtLoader(applicationExtLoader);

			enricher.enrich(applicationContext);

			channelContext
					.setIoEventHandleAdaptor(new ApplicationIoEventHandle(applicationContext));

			if (sc.isSERVER_ENABLE_SSL()) {

				File certificate = bundle.loadFile("conf/generallycloud.com.crt");
				File privateKey = bundle.loadFile("conf/generallycloud.com.key");

				SslContext sslContext = SSLUtil.initServer(privateKey, certificate);

				channelContext.setSslContext(sslContext);
			}

			sc.setSERVER_PORT(getServerPort(sc.getSERVER_PORT(), sc.isSERVER_ENABLE_SSL()));

			acceptor.bind();

		} catch (Throwable e) {

			Logger logger = LoggerFactory.getLogger(getClass());

			logger.error(e.getMessage(), e);

			CloseUtil.unbind(acceptor);
		}

	}

	public void bootstrap(String rootPath) throws Exception {

		SharedBundle bundle = SharedBundle.instance().loadAllProperties(rootPath);

		LoggerFactory.configure(bundle.loadProperties("conf/log4j.properties", Encoding.UTF8));

		ApplicationConfigurationLoader acLoader = loadConfigurationLoader(
				bundle.getProperty("intf.ApplicationConfigurationLoader"));

		ServerConfigurationLoader configurationLoader = new PropertiesSCLoader();

		ServerConfiguration serverConfiguration = configurationLoader.loadConfiguration(bundle);

		bootstrap(acLoader, rootPath, null, serverConfiguration);
	}

	private ApplicationConfigurationLoader loadConfigurationLoader(String className) {
		Class<?> clazz = ClassUtil.forName(className, FileSystemACLoader.class);
		return (ApplicationConfigurationLoader) ClassUtil.newInstance(clazz);
	}

	private ApplicationContextEnricher loadApplicationContextEnricher(String className)
			throws Exception {
		Class<?> clazz = ClassUtil.forName(className);
		if (clazz == null) {
			throw new Exception("intf.ApplicationContextEnricher is empty");
		}
		return (ApplicationContextEnricher) ClassUtil.newInstance(clazz);
	}

	private ApplicationExtLoader loadApplicationExtLoader(String className) throws Exception {
		Class<?> clazz = ClassUtil.forName(className, DefaultExtLoader.class);
		return (ApplicationExtLoader) ClassUtil.newInstance(clazz);
	}

	private int getServerPort(int port, boolean enableSSL) {
		if (port != 0) {
			return port;
		}
		return enableSSL ? 443 : 80;
	}

	public static void main(String[] args) throws Exception {

		ApplicationBootstrap startup = new ApplicationBootstrap();

		String base = null;

		if (args != null && args.length > 0) {
			base = args[0];
		}

		startup.bootstrap(base);
	}
}
