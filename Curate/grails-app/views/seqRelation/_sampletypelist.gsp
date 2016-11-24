%{--
  - Copyright (c) 2016. PathOS Variant Curation System. All rights reserved.
  -
  - Organisation: Peter MacCallum Cancer Centre
  - Author: seleznev andrei
  --}%

<g:select name="seqsample_set_type" from="${org.petermac.pathos.curate.SeqSample.constraints.sampleType['inList']}" noSelection="${[null: 'No sample type']}" value="${sampletype}"  />
