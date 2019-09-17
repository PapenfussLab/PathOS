%{--
  - Copyright (c) 2018. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation: Peter MacCallum Cancer Centre
  - Author: seleznev andrei
  --}%
<div class="fieldcontain">
<label for="panellist" >Panel</label>
<g:select name="panellist" noSelection="['':'Select a panel']" from="${panelList}" value="${params?.panel?:''}">
</g:select>
</div>