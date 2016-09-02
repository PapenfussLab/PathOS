/*
 * Copyright (c) 2015. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: doig ken
 */

package org.petermac.util

import groovy.util.logging.Log4j;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Description:
 *
 * Find the number of Google hits for a list of search strings in a file (one per line)
 *
 * User: Ken Doig adapted from RAJESH Kharche
 * See: http://stackoverflow.com/questions/18204814/easiest-legal-way-to-programmatically-get-the-google-search-result-count
 * Date: 28-jul-2015
 */

@Log4j
public class GoogleCount
{
    /**
     * Main execution thread
     *
     * @param args  CLI arguments
     */
    static void main(args)
    {
        def cli = new CliBuilder(
                usage: 'GoogleCount [options] in.txt',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nCount Google hits for each line in in.txt\n')

        cli.with
                {
                    h(longOpt: 'help', 'Usage Information', required: false)
                    u(longOpt: 'url', args: 1, 'Search URL' )
                    s(longOpt: 'scholar', 'Search Google Scholar' )
                }
        def opt = cli.parse(args)

        if ( ! opt ) return
        if ( opt.h )
        {
            cli.usage()
            return
        }

        //  Set search url
        //
        String url = 'https://www.google.com.au/search?q='
        if ( opt.scholar ) url = 'https://scholar.google.com.au/scholar?q='
        if ( opt.url ) url = opt.url

        //  Run the program
        //
        log.info("GoogleCount " + args )

        List<String> extra = opt.arguments()
        def searchFile = new File( extra[0] as String )

        if ( ! searchFile.exists())
        {
            log.error("Search file doesn't exist: " + searchFile.name)
            return
        }

        int nlines = 0
        searchFile.eachLine()
        {
            line ->

                Double res = -999
                try
                {
                    res = getResultsCount( line, url, opt.scholar )
                    println( "${line}\t${res}")
                }
                catch (IOException ex)
                {
                    log.fatal( "Google search failed for ${line} " + ex.toString())
                    System.exit(1)
                }
                ++nlines
        }

        log.info("Done, processed ${nlines} lines")
    }

    /**
     * Perform a Google search on a string
     *
     * @param   query   String to query
     * @return          Number of hits
     * @throws  IOException
     */
    static Double getResultsCount( final String query, String urlstr, boolean scholar ) throws IOException
    {
        URL url = new URL( urlstr + URLEncoder.encode(query, "UTF-8"))
        URLConnection connection = url.openConnection()

        connection.setConnectTimeout(60000)
        connection.setReadTimeout(60000)
        connection.addRequestProperty("User-Agent", "Google Chrome/36")             //put the browser name/version

        final Scanner reader = new Scanner(connection.getInputStream(), "UTF-8")    //scanning a buffer from object returned by http request

        while(reader.hasNextLine())
        {
            //  for each line in buffer
            //
            final String line = reader.nextLine()

            // "gs_ab_md">About 282 results
            if( ! line.contains( scholar ? "\"gs_ab_md\">" : "\"resultStats\">" ))  //line by line scanning for "resultstats" field because we want to extract number after it
                continue

            try
            {
                //  finally extract the number convert from string to integer
                //
                if ( ! scholar )
                    return Double.parseDouble(line.split("\"resultStats\">")[1].split("<")[0].replaceAll("[^\\d]", ""))

                def match = ( line =~ /\"gs_ab_md\">About (\d+) results/ )
                if ( match.count == 1 )
                {
                    return Integer.parseInt( match[0][1])	//	count of google scholar
                }
                return 0
            }
            finally
            {
                reader.close()
            }
        }

        reader.close()
        return 0
    }
}