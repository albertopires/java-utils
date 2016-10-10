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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class Example {

	public static void main(String[] args) throws AddressException, MessagingException, UnsupportedEncodingException, IOException {
		GenericProperties param;
		ArrayList<String> rcptList;
		Mail mail;

		String userId = System.getProperty("userId");
		String password = System.getProperty("password");

		if (args.length >= 6) {
			param = new GenericProperties();
			rcptList = new ArrayList<String>();
			if (userId != null) {
				mail = new Mail(userId, password);
			} else {
				mail = new Mail();
			}

			if (args[0].equals("-t")) {
				param.put(Mail.MSG_FILE, args[1]);
				param.put(Mail.MAIL_FROM, args[2]);
				param.put(Mail.MAIL_FROM_NAME, args[3]);
				param.put(Mail.MAIL_SUBJECT, args[4]);
				for (int i = 5; i < args.length; i++) {
					rcptList.add(args[i]);
				}
				param.put(Mail.RCPT_LIST, rcptList);
				mail.sendTextMail(param);
				System.exit(0);
			}

			if (args[0].equals("-f")) {
				param.put(Mail.ATTCH_FILE, args[1]);
				param.put(Mail.MAIL_FROM, args[2]);
				param.put(Mail.MAIL_FROM_NAME, args[3]);
				param.put(Mail.MAIL_SUBJECT, args[4]);
				for (int i = 5; i < args.length; i++) {
					rcptList.add(args[i]);
				}
				param.put(Mail.RCPT_LIST, rcptList);
				mail.sendTextandAttachmentEmail(param);
				System.exit(0);
			}

			if (args[0].equals("-m")) {
				param.put(Mail.MSG_FILE, args[1]);
				param.put(Mail.ATTCH_FILE, args[2]);
				param.put(Mail.MAIL_FROM, args[3]);
				param.put(Mail.MAIL_FROM_NAME, args[4]);
				param.put(Mail.MAIL_SUBJECT, args[5]);
				for (int i = 6; i < args.length; i++) {
					rcptList.add(args[i]);
				}
				param.put(Mail.RCPT_LIST, rcptList);
				mail.sendTextandAttachmentEmail(param);
				System.exit(0);
			}
		} else {
			System.out.println("Send Mail/File\n");
			System.err.println("Invalid parameters.");
			System.err.println("-t : send text message.");
			System.err.println("-f : send attached file");
			System.err.println("-m : send text message with attached file");
			System.err.println();
			System.err.println("-t <msg.txt> <from> <from_name> <subject> <rcpt1> <rcpt2> ... <rcptN>");
			System.err.println("-f <file.any_type> <from> <from_name> <subject> <rcpt1> <rcpt2> ... <rcptN>");
			System.err.println("-m <msg.txt> <file.any_type> <from> <from_name> <subject> <rcpt1> <rcpt2> ... <rcptN>");
		}
	}

}
