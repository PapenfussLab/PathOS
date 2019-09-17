<%@ page import="org.petermac.pathos.curate.SeqSample" %>

<form action="<g:context/>/seqVariant/revokeReview/${seqSample.id}" method="post" name="revoke${review}Form" id="revoke${review}Form">
    ${review} review completed by ${user} on ${date?.format("d-MMM-yyyy")}

    <sec:ifAnyGranted roles="ROLE_ADMIN, ROLE_DEV">
        <input type="submit" id="revoke${review}Submit" name="revoke${review}Submit" value="Revoke ${review} Review">
        <input type="hidden" name="review" value="${review}" id="revoke${review}">
    </sec:ifAnyGranted>
</form>

