/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.util

/**
 * Created by seleznev andrei on 10/11/2015.
 * send mails using the command line
 */
class Mailer {

    static String commandLineSendMail(String toAddress, String subject ,String body){

        //temp file for email body
        //
        File temp = File.createTempFile("temp",".scrap");
        temp.write(body)

        //run command line mail

        def cmd = "mail -s '${subject}' ${toAddress} < '${temp.getAbsolutePath()}'"

        def sout  = new RunCommand( cmd ).run()
        return sout
        //mail sending doesnt return anything
        //so neither does this
    }
}
