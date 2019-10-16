<div class="buttons">
    <span class="button">
        <input type="button"
               class="btn"
               value="${cancelText}" 
               onclick="return grailsFilterPane.hideElement('${containerId}');" />
    </span>
    <span class="button">
        <input type="button"
               class="btn"
               value="${clearText}" 
               onclick="return grailsFilterPane.clearFilterPane('${formName}');" />
    </span>
    <span class="button">
    	<g:actionSubmit class="btn" value="${applyText}" action="${action}" />
    </span>
</div>