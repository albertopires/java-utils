
## STMP ##

This is basically a wrapper class for java.mailx. There is one class: Mail and two methods:

* void sendTextMail(GenerictProperties param)
* void sendTextandAttachmentEmail(GenerictProperties param)

There three options that can passed via command line (using -D):

* User Name: java-utils.user
* Password: java-utils.password
* Configuration file: java-utils.config

Probably, **java-utils.config** will be the most useful. It if not defined then **sample_config.properties** on the current directory will be used.

User and password can be used if one does not want to store them on the properties files.

To create an email, create an instance of **GenerictProperties** and set the following properties:

* MAIL_FROM : From address
* MAIL_FROM_NAME : Sender name
* MAIL_SUBJECT : Mail subject
* MSG_FILE :  Text file with a message
* ATTCH_FILE :  Any file to be attached
* RCPT_LIST :  `java.util.List<String>` of recipients

An example of how to run the Example class:

``java -cp ./target/java-utils-0.0.1.jar io.github.albertopires.mail.Example
 -m ./sample_msg.txt any_file.png someone@example.com "Mr. SomeOne" rcpt@anotherdomain.com``
