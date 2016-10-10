/*
 * Copyright (C) 2016 Alberto Pires de Oliveira Neto
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
package io.github.albertopires.mail;

import io.github.albertopires.commons.GenericProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mail {
	private Properties mailSettings;
	private Session session;
	private String userId;
	private String password;
	private String configFile;

	public static final String MAIL_FROM = "mail.from";
	public static final String MAIL_FROM_NAME = "mail.from.name";
	public static final String MAIL_SUBJECT = "mail.subject";
	public static final String MSG_FILE = "file.message";
	public static final String ATTCH_FILE = "file.attachment";
	public static final String RCPT_LIST = "rcpt.list";

	private static final String PARAM_USER = "java-utils.user";
	private static final String PARAM_PASS = "java-utils.password";
	private static final String PARAM_CONFIG = "java-utils.config";

	public Mail() {
		init();

		this.userId = mailSettings.getProperty(PARAM_USER);
		this.password = mailSettings.getProperty(PARAM_PASS);
	}

	public Mail(final String userId, final String password) {
		init();

		this.userId = userId;
		this.password = password;
	}

	private void init() {
		loadConfig();
		this.session = Session.getDefaultInstance(mailSettings, null);
	}

	private void loadConfig() {
		Properties config = new Properties();

		this.configFile = System.getProperties().getProperty(PARAM_CONFIG);
		try {
			if (configFile == null) {
				configFile = "sample_smtp.properties";
			}
			FileInputStream fi = new FileInputStream(configFile);
			config.load(fi);
		} catch (IOException e) {
			System.err.println("Fail to open configuration file : " + configFile);
			System.exit(1);
		}

		mailSettings = System.getProperties();
		mailSettings.putAll(config);
	}

	public final void sendTextMail(final GenericProperties param) throws AddressException, MessagingException, UnsupportedEncodingException, IOException {
		Message message;

		message = new MimeMessage(session);
		message.setFrom(new InternetAddress(param.getProperty(MAIL_FROM), param.getProperty(MAIL_FROM_NAME)));

		List<String> rcptList = param.getList(RCPT_LIST);
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(rcptList.get(0)));
		for (int i = 1; i < rcptList.size(); i++) {
			message.addRecipient(Message.RecipientType.CC, new InternetAddress(rcptList.get(i)));
		}

		message.setSubject(param.getProperty(MAIL_SUBJECT));

		String emailBody = new String(Files.readAllBytes(Paths.get(param.getProperty(MSG_FILE))));
		message.setContent(emailBody, "text/html");

		Transport transport = session.getTransport("smtp");

		transport.connect(mailSettings.getProperty("mail.smtp.relay"), userId, password);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

	public final void sendTextandAttachmentEmail(final GenericProperties param) throws AddressException, MessagingException, UnsupportedEncodingException, IOException {
		Message message;

		message = new MimeMessage(session);
		message.setFrom(new InternetAddress(param.getProperty(MAIL_FROM), param.getProperty(MAIL_FROM_NAME)));

		List<String> rcptList = param.getList(RCPT_LIST);
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(rcptList.get(0)));
		for (int i = 1; i < rcptList.size(); i++) {
			message.addRecipient(Message.RecipientType.CC, new InternetAddress(rcptList.get(i)));
		}

		message.setSubject(param.getProperty(MAIL_SUBJECT));

		// Create a multipart message
		Multipart multipart = new MimeMultipart();
		BodyPart messageBodyPart;

		// Now set the actual message
		String textFileName = param.getProperty(MSG_FILE);
		if (textFileName != null) {
			// Create the message part
			messageBodyPart = new MimeBodyPart();
			String messageText = new String(Files.readAllBytes(Paths.get(textFileName)));
			messageBodyPart.setText(messageText);
			// Set text message part
			multipart.addBodyPart(messageBodyPart);
		}

		// Part two is attachment
		String attchFile = param.getProperty(ATTCH_FILE);
		if (attchFile != null) {
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(attchFile);
			messageBodyPart.setDataHandler(new DataHandler(source));
			String[] aux = attchFile.split("/");
			messageBodyPart.setFileName(aux[aux.length - 1]);
			multipart.addBodyPart(messageBodyPart);
		}

		// Send the complete message parts
		message.setContent(multipart);

		Transport transport = session.getTransport("smtp");
		transport.connect(mailSettings.getProperty("mail.smtp.relay"), userId, password);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}
}
