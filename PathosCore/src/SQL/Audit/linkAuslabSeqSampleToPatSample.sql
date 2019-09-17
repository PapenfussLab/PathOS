update seq_sample set
  pat_sample_id=(select id from pat_sample where sample='08001682')
where substring(seq_sample.sample_name,1,8)='08001682'
and pat_sample_id IS NULL;