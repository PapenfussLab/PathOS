<li>
    <g:form>
        <g:hiddenField name="id" value="${id}" />
        <g:actionSubmit class="delete"
                        value="Delete Variant"
                        action="delete"
                        controller="curVariant"
                        onclick="return confirm('Are you sure? Note: Deleting a curated variant in the Generic context will also delete the variant in all other contexts.');" />
    </g:form>
</li>