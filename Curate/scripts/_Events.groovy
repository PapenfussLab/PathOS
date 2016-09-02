/*
 Here we incremend build number on compile , as per: http://blog.apoapsys.com/2010/07/08/adding-an-auto-incrementing-build-number-to-a-grails-application/
 also throw our git rev into a file 
*/
eventCompileStart = { kind ->
    def buildNumber = metadata.'app.buildNumber'

    if (!buildNumber)
        buildNumber = 1
    else
        buildNumber = Integer.valueOf(buildNumber) + 1

    metadata.'app.buildNumber' = buildNumber.toString()

    metadata.persist()

    println "**** Compile Starting on Build #${buildNumber}"

    //  grab the output of git rev-parse HEAD which should be a 40char hash and a newline (41 chars)
    //  and if it is, throw the first 7 into a template so we can later display it
    def gitText = ("git rev-parse HEAD".execute().text)
    if(gitText && gitText?.length() == 41) {
        new File("grails-app/views/_git.gsp").text = gitText.substring(0, 7).replaceAll("/[^A-Za-z0-9 ]/", "");
    } else {
        new File("grails-app/views/_git.gsp").text = "-"
    }
}
