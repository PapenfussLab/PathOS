<%@ page import="org.petermac.pathos.curate.SeqSample" %>

<form action="<g:context/>/seqVariant/authoriseReview/${seqSample.id}" method="post" name="authorise{review}Form" id="authorise{review}Form">

    <sec:ifAnyGranted roles="ROLE_ADMIN, ROLE_DEV, ROLE_CURATOR, ROLE_LAB">
        <input type="hidden" name="review" value="${review}" id="authorise{review}">
        <input type="submit" id="authorise{review}Submit" name="authorise{review}Submit" value="Authorise ${review} Review">
    </sec:ifAnyGranted>
</form>
